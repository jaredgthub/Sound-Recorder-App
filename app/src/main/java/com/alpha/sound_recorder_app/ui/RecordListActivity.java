package com.alpha.sound_recorder_app.ui;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.alpha.sound_recorder_app.R;
import com.alpha.sound_recorder_app.dao.Db;
import com.alpha.sound_recorder_app.dao.RecordDao;
import com.alpha.sound_recorder_app.model.BaseRecord;
import com.alpha.sound_recorder_app.model.RecordAwr;
import com.alpha.sound_recorder_app.util.Global;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;

public class RecordListActivity extends ListActivity {

    private SimpleCursorAdapter adapter;
    private Db db;
    private RecordDao recordDao;

    private MediaPlayer mPlayer = null;

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

                new AlertDialog.Builder(RecordListActivity.this).setTitle("delete").setMessage("are you sure del?")
                        .setPositiveButton("sure", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Cursor c = adapter.getCursor();
                                c.moveToPosition(position);
                                int itemId = c.getInt(c.getColumnIndex("_id"));
                                if (recordDao.delRecord(itemId)) {
                                    Toast.makeText(RecordListActivity.this, "delete success! ", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(RecordListActivity.this, "delete fail! ", Toast.LENGTH_LONG).show();
                                }
                                refreshListView();
                            }
                        }).setNegativeButton("cancel", null).show();
                //此次长按有效，返回true
                return true;
            }
        });

        //点击
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //播放
                Cursor cursor = adapter.getCursor();
                cursor.moveToPosition(position);
                String fileName = cursor.getString(cursor.getColumnIndex("name"));
                if (mPlayer != null) {
                    mPlayer.stop();
                    mPlayer.release();
                    mPlayer = null;
                }
                mPlayer = new MediaPlayer();
                try {
                    mPlayer.setDataSource(Global.PATH + fileName);
                    mPlayer.prepare();
                    //mPlayer.start();
                } catch (Exception e) {
                    Toast.makeText(RecordListActivity.this, "record file is missing !", Toast.LENGTH_LONG).show();

                    new AlertDialog.Builder(RecordListActivity.this).setTitle("you need update List").setMessage("are you sure update?")
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
        Button playMusicBtn = (Button) findViewById(R.id.play_music);
        playMusicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPlayer != null){
                    mPlayer.start();
                }
            }
        });

    }

    public void refreshListView(){
        adapter.changeCursor(recordDao.getAllRecord());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_record_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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
