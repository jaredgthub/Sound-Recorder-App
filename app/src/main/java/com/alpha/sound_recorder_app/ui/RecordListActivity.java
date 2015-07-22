package com.alpha.sound_recorder_app.ui;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.alpha.sound_recorder_app.model.Record;
import com.alpha.sound_recorder_app.model.RecordAwr;
import com.alpha.sound_recorder_app.util.Global;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;

public class RecordListActivity extends ListActivity {

    private SimpleCursorAdapter adapter;
    private RecordDao recordDao;

    private MediaPlayer mPlayer = null;

    private Record record;
    private EditText editText;
    private Menu menu;

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
                menu.findItem(R.id.menu_search).setVisible(false);
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
        Button playMusicBtn = (Button) findViewById(R.id.play_music);
        playMusicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPlayer != null){
                    mPlayer.start();
                }
            }
        });

        //显示actionbar上的返回
        getActionBar().setDisplayHomeAsUpEnabled(true);
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
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search)
                .getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                resultTV.setText(query);
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
        }else if(id == R.id.menu_rename){
            if (record == null) {
                Toast.makeText(RecordListActivity.this, "haven't choose any file.", Toast.LENGTH_SHORT).show();
            } else {
                new AlertDialog.Builder(RecordListActivity.this)
                    .setTitle("Rename")
                    .setMessage(Global.getFileNameWithoutSuffix(record.getName()))
                    .setIcon(R.drawable.actionbar_renamebutton)
                    .setView(editText = new EditText(RecordListActivity.this))
                    .setPositiveButton("sure", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int position) {
                            //TODO 只能输入数字和英文（不能有空格和 '.' ）
                            String newname = editText.getText().toString();
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
                            menu.findItem(R.id.menu_search).setVisible(true);
                        }
                    }).setNegativeButton("cancel", null).show();
            }
            return true;
        }else if(id == R.id.menu_del){
            if(record == null){
                Toast.makeText(RecordListActivity.this, "haven't choose any file.", Toast.LENGTH_SHORT).show();
            }else{
                new AlertDialog.Builder(RecordListActivity.this).setTitle("delete").setMessage("are you sure del?")
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
                            menu.findItem(R.id.menu_search).setVisible(true);
                        }
                    }).setNegativeButton("cancel", null).show();
            }
            return true;
        }else if(id == android.R.id.home){
            //actionbar上的返回
            if(menu.findItem(R.id.menu_search).isVisible()){
                startActivity(new Intent(RecordListActivity.this,MainActivity.class));
            }else{
                menu.findItem(R.id.menu_rename).setVisible(false);
                menu.findItem(R.id.menu_del).setVisible(false);
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
