package com.alpha.sound_recorder_app.ui;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.alpha.sound_recorder_app.R;
import com.alpha.sound_recorder_app.dao.Db;
import com.alpha.sound_recorder_app.dao.RecordDao;
import com.alpha.sound_recorder_app.model.Record;
import com.alpha.sound_recorder_app.util.Global;

import java.io.File;
import java.io.FilenameFilter;

public class RecordListActivity extends ListActivity {

    private SimpleCursorAdapter adapter;
    private Db db;
    private RecordDao recordDao;
    private String fileLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);
        db = new Db(this);
        recordDao = new RecordDao(db);

        //检测是否存在SD卡
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
            //设置sdcard的路径
            fileLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + Global.PATH;
//            fileName += "/hello.3gp";
            // 更新所有录音到数据库中
            updateRecordList();
        } else{
            Toast.makeText(RecordListActivity.this, "without SD card! ", Toast.LENGTH_LONG).show();
        }



        adapter = new SimpleCursorAdapter(this,R.layout.record_list_item,null,new String[]{"_id","name"},new int[]{R.id.idItem,R.id.nameItem});
        setListAdapter(adapter);
        //长按
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                new AlertDialog.Builder(RecordListActivity.this).setTitle("alert").setMessage("are you sure del?")
                        .setPositiveButton("sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Cursor c = adapter.getCursor();
                        c.moveToPosition(position);
                        int itemId = c.getInt(c.getColumnIndex("_id"));
                        recordDao.delRecord(itemId);
                        refreshListView();
                    }
                }).setNegativeButton("cancel",null).show();
                //此次长按有效，返回true
                return true;
            }
        });
        refreshListView();
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
        //扫描文件夹下的所有录音文件(扫描后缀，不扫描前缀)，并将数据写入数据库。
        // 取得指定位置的文件设置显示到播放列表
        //清空数据库中record的记录
        recordDao.clearRecord();
        File rootPath = new File(fileLocation);
        if (rootPath.listFiles(new RecordFilter()).length > 0){
            for (File file : rootPath.listFiles(new RecordFilter())){
                Record record = new Record();
                record.setName(file.getName());
                record.setRecordFile(file);
//                record.setCreateTime(file.ge);
                //mMusicList.add(file.getName());
                recordDao.addRecord(record);
            }
        }
    }

    class RecordFilter implements FilenameFilter {
        public boolean accept(File dir, String name){
//            return (name.endsWith(".amr"));
            return (name.endsWith(".3gp"));
        }
    }
}
