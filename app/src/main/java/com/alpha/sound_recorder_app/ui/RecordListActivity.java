package com.alpha.sound_recorder_app.ui;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.alpha.sound_recorder_app.R;
import com.alpha.sound_recorder_app.dao.RecordDao;
import com.alpha.sound_recorder_app.model.BaseRecord;
import com.alpha.sound_recorder_app.model.Record;
import com.alpha.sound_recorder_app.model.RecordAwr;
import com.alpha.sound_recorder_app.util.CommonUploadUtils;
import com.alpha.sound_recorder_app.util.DownloadUtil;
import com.alpha.sound_recorder_app.util.Global;
import com.baidu.frontia.Frontia;
import com.baidu.frontia.api.FrontiaAuthorization;
import com.baidu.frontia.api.FrontiaSocialShare;
import com.baidu.frontia.api.FrontiaSocialShareContent;
import com.baidu.frontia.api.FrontiaSocialShareListener;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class RecordListActivity extends ListActivity {

    private SimpleCursorAdapter adapter;
    private RecordDao recordDao;

    private MediaPlayer mPlayer = null;

    private Record record;
    private EditText editText;
    private Menu menu;

    // 是否播放
    private boolean isPlay;
    // 互斥变量，防止定时器与SeekBar拖动时进度冲突
    private boolean isChanging;
    private SeekBar seekbar;

    private Timer timer;
    private TimerTask timerTask;

    Button playMusicBtn;
    Button pauseMusicBtn;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Toast.makeText(getApplicationContext(), "upload success! ", Toast.LENGTH_SHORT).show();
        };
    };
    private FrontiaSocialShare mSocialShare;
    private FrontiaSocialShareContent mImageContent = new FrontiaSocialShareContent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);

        recordDao = new RecordDao(this);

        adapter = new SimpleCursorAdapter(this,R.layout.record_list_item,null,new String[]{"_id","name","createTime","length"},new int[]{R.id._idItem,R.id.nameItem,R.id.createTimeItem,R.id.lengthItem});
        setListAdapter(adapter);
        //长按
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                Cursor c = adapter.getCursor();
                c.moveToPosition(position);
                int itemId = c.getInt(c.getColumnIndex("_id"));

                record = new Record();
                record.set_id(itemId);
                record.setName(c.getString(c.getColumnIndex("name")));

                menu.findItem(R.id.menu_rename).setVisible(true);
                menu.findItem(R.id.menu_del).setVisible(true);
                menu.findItem(R.id.menu_share).setVisible(true);
                menu.findItem(R.id.menu_search).setVisible(false);
                //此次长按有效，返回true
                return true;
            }
        });

        //点击
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                seekbar.setVisibility(View.VISIBLE);
                //播放
                Cursor cursor = adapter.getCursor();
                cursor.moveToPosition(position);
                String fileName = cursor.getString(cursor.getColumnIndex("name"));
                isChanging = true;
                if (mPlayer != null) {
                    mPlayer.stop();
                    mPlayer.release();
                    mPlayer = null;
                }
                mPlayer = new MediaPlayer();
                try {
                    mPlayer.setDataSource(Global.PATH + fileName);
                    mPlayer.prepare();
                    isPlay = false;
                    playerFinish();
                } catch (Exception e) {
                    Toast.makeText(RecordListActivity.this, "record file is missing !", Toast.LENGTH_LONG).show();

                    new AlertDialog.Builder(RecordListActivity.this)
                        .setTitle("you need update List")
                        .setMessage("are you sure update?")
                        .setPositiveButton("sure", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    updateRecordList();
                                    refreshListView();
                                }
                            }
                        ).setNegativeButton("cancel", null).show();
                }
            }
        });

        refreshListView();

        //播放音乐
        seekbar = (SeekBar) findViewById(R.id.sb_play);
        seekbar.setOnSeekBarChangeListener(new MySeekbar());
        seekbar.setVisibility(View.INVISIBLE);

        playMusicBtn = (Button) findViewById(R.id.play_music);
        pauseMusicBtn = (Button) findViewById(R.id.pause_music);
        pauseMusicBtn.setVisibility(View.INVISIBLE);

        playMusicBtn.setOnClickListener(new PlayOrPauseListener());
        pauseMusicBtn.setOnClickListener(new PlayOrPauseListener());
        //显示actionbar上的返回
        getActionBar().setDisplayHomeAsUpEnabled(true);

        clearNotification();
    }

    private class PlayOrPauseListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (mPlayer != null) {
                //如果是正在播放，则点击即为暂停
                if (isPlay) {
                    playMusicBtn.setVisibility(View.VISIBLE);
                    pauseMusicBtn.setVisibility(View.INVISIBLE);
                    isPlay = false;
                    mPlayer.pause();
                } else {
                    //不是正在播放，即点击要进行播放
                    playMusicBtn.setVisibility(View.INVISIBLE);
                    pauseMusicBtn.setVisibility(View.VISIBLE);

                    isPlay = true;
                    mPlayer.start();
                    seekbar.setMax(mPlayer.getDuration());
                    timer = new Timer();
                    isChanging = false;
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            if (isChanging) {
                                return;
                            } else {
                                seekbar.setProgress(mPlayer.getCurrentPosition());
                            }
                        }
                    };
                    timer.schedule(timerTask, 0, 10);
                    mPlayer.start();
                }
            }
        }
    }

    private void playerFinish() {
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isPlay = false;
                mPlayer.seekTo(0);
                mPlayer.pause();
                playMusicBtn.setVisibility(View.VISIBLE);
                pauseMusicBtn.setVisibility(View.INVISIBLE);
            }
        });
    }

    // 进度条处理
    class MySeekbar implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isChanging = true;
        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mPlayer.seekTo(seekbar.getProgress());
            isChanging = false;
        }
    }

    public void refreshListView(){
        adapter.changeCursor(recordDao.getAllRecord());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        getMenuInflater().inflate(R.menu.menu_record_list, menu);
        this.menu = menu;

        // 搜索
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.changeCursor(recordDao.findRecordByName(query));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                resultTV.setText(newText);
                adapter.changeCursor(recordDao.findRecordByName(newText));
                return true;
            }
        });

        menu.findItem(R.id.menu_rename).setVisible(false);
        menu.findItem(R.id.menu_del).setVisible(false);
        menu.findItem(R.id.menu_share).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(record == null && (id == R.id.menu_del || id == R.id.menu_share || id == R.id.menu_rename)){
            Toast.makeText(RecordListActivity.this, "haven't choose any file.", Toast.LENGTH_SHORT).show();
            return false;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if(id == R.id.menu_rename){
            new AlertDialog.Builder(RecordListActivity.this)
                .setTitle("Rename")
                .setMessage(Global.getFileNameWithoutSuffix(record.getName()))
                .setIcon(R.drawable.actionbar_renamebutton)
                .setView(editText = new EditText(RecordListActivity.this))
                .setPositiveButton("sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        String newname = editText.getText().toString();
                        if (!Global.isLegal(newname)) {
                            Toast.makeText(RecordListActivity.this, "text not legal", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String oldname = record.getName();
                        File oldFile = new File(Global.PATH + oldname);
                        File newFile = new File(Global.PATH + newname + Global.getSuffix(oldname));
                        oldFile.renameTo(newFile);
                        if (recordDao.updateRecordFileName(record.get_id(), newFile.getName())) {
                            refreshListView();
                            Toast.makeText(RecordListActivity.this, "rename success!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RecordListActivity.this, "rename fail!", Toast.LENGTH_SHORT).show();
                        }
                        menu.findItem(R.id.menu_rename).setVisible(false);
                        menu.findItem(R.id.menu_del).setVisible(false);
                        menu.findItem(R.id.menu_share).setVisible(false);
                        menu.findItem(R.id.menu_search).setVisible(true);
                        seekbar.setVisibility(View.INVISIBLE);
                    }
                }).setNegativeButton("cancel", null).show();
            return true;
        }else if(id == R.id.menu_del){
            new AlertDialog.Builder(RecordListActivity.this).setTitle("delete").setMessage("del "+record.getName()+"?")
                .setPositiveButton("sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (recordDao.delRecord(record.get_id())) {
                            Toast.makeText(RecordListActivity.this, "delete success! ", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(RecordListActivity.this, "delete fail! ", Toast.LENGTH_LONG).show();
                        }
                        refreshListView();
                        menu.findItem(R.id.menu_rename).setVisible(false);
                        menu.findItem(R.id.menu_del).setVisible(false);
                        menu.findItem(R.id.menu_share).setVisible(false);
                        menu.findItem(R.id.menu_search).setVisible(true);
                        seekbar.setVisibility(View.INVISIBLE);
                    }
                }).setNegativeButton("cancel", null).show();
            return true;
        }else if(id == R.id.menu_share){
            //upload and share.
            boolean isInit = Frontia.init(getApplicationContext(), "wBk3HUHSnGPzGw9V43B2UTWz");
            if(isInit){
                //Use Frontia
                mSocialShare = Frontia.getSocialShare();
                mSocialShare.setContext(this);
                mSocialShare.setClientId(FrontiaAuthorization.MediaType.SINAWEIBO.toString(), "2788353227");
//        mSocialShare.setClientId(MediaType.QZONE.toString(), "100358052");
//        mSocialShare.setClientId(MediaType.QQFRIEND.toString(), "100358052");
//        mSocialShare.setClientName(MediaType.QQFRIEND.toString(), "百度");
//        mSocialShare.setClientId(MediaType.WEIXIN.toString(), "wx329c742cb69b41b8");
                mImageContent.setTitle("alpha sound recorder app");
                mImageContent.setContent("Check out this recording I made with this great new android app. You can get the app here: ");
                //分享的链接地址，应该为录音的存储位置(不能用空格)
                mImageContent.setLinkUrl(DownloadUtil.getUrl(record.getName()));
            }else{
                Toast.makeText(RecordListActivity.this, "init error!", Toast.LENGTH_LONG).show();
            }
            //upload the file!
            new CommonUploadUtils(handler).runUpload(record.getName());
            new AlertDialog.Builder(RecordListActivity.this)
                .setTitle("share?")
                .setMessage("share record  "+ record.getName() + " ?")
                .setPositiveButton("sure", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mSocialShare.share(mImageContent, FrontiaAuthorization.MediaType.BATCHSHARE.toString(), new ShareListener(), true);
                        }
                    }
                ).setNegativeButton("cancel", null).show();
            return true;
        }else if(id == android.R.id.home){
            //actionbar上的返回
            if(menu.findItem(R.id.menu_search).isVisible()){
                //不能new Intent，否则当前的activity没有destroy
//                startActivity(new Intent(RecordListActivity.this,MainActivity.class));
                finish();
            }else{
                menu.findItem(R.id.menu_rename).setVisible(false);
                menu.findItem(R.id.menu_del).setVisible(false);
                menu.findItem(R.id.menu_share).setVisible(false);
                menu.findItem(R.id.menu_search).setVisible(true);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 播放列表
     */
    public void updateRecordList(){
        //扫描文件夹下的所有录音文件(扫描后缀)，并将数据写入数据库。
        // 取得指定位置的文件设置显示到播放列表
        recordDao.clearRecord();
        File rootPath = new File(Global.PATH);
        if (rootPath.listFiles(new RecordFilter()).length > 0){
            for (File file : rootPath.listFiles(new RecordFilter())){
                BaseRecord record = new RecordAwr();
                record.setName(file.getName());
                record.setRecordFile(file);
                record.setCreateTime(new Date(file.lastModified()));
                record.setLength(file.length());
                recordDao.addRecord(record);
            }
        }
    }

    class RecordFilter implements FilenameFilter {
        public boolean accept(File dir, String name){
            return (name.endsWith(".amr") || name.endsWith(".wav"));
        }
    }

    private class ShareListener implements FrontiaSocialShareListener {

        @Override
        public void onSuccess() {
            Toast.makeText(RecordListActivity.this, "share success! ", Toast.LENGTH_LONG).show();
            Log.d("Test", "share success");
        }

        @Override
        public void onFailure(int errCode, String errMsg) {
            Toast.makeText(RecordListActivity.this, "share fail!", Toast.LENGTH_LONG).show();
            Log.d("Test","share errCode "+errCode);
        }

        @Override
        public void onCancel() {
            Log.d("Test","cancel ");
            Toast.makeText(RecordListActivity.this, "you cancel this share!", Toast.LENGTH_SHORT).show();
        }
    }

    //notification
    private void showNotification() {
        NotificationManager barmanager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notice = new Notification(android.R.drawable.ic_media_play,"Record is running...",System.currentTimeMillis());

        notice.flags= Notification.FLAG_AUTO_CANCEL;
        Intent appIntent = new Intent(Intent.ACTION_MAIN);
        appIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        appIntent.setComponent(new ComponentName(this.getPackageName(), this.getPackageName() + "." + this.getLocalClassName()));
        appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);//启动
        PendingIntent contentIntent =PendingIntent.getActivity(this, 0,appIntent,0);
        notice.setLatestEventInfo(this,"Sound-Recorder-App","running", contentIntent);
        barmanager.notify(0,notice);
    }

    private void clearNotification(){
        // 启动后删除之前我们定义的通知
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }

    @Override
    protected void onStop() {
        if(isPlay){
            showNotification();
        }
        isChanging = true;
        super.onStop();
    }

    @Override
    protected void onStart() {
        clearNotification();
        isChanging = false;
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPlayer != null){
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }

        recordDao.close();
    }

}
