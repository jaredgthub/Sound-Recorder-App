package com.alpha.sound_recorder_app.ui;

/**
 * Created by huangshihe on 2015/7/14.
 */

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alpha.sound_recorder_app.R;
import com.alpha.sound_recorder_app.dao.RecordDao;
import com.alpha.sound_recorder_app.model.Record;
import com.alpha.sound_recorder_app.util.Global;

public class MainActivity extends Activity {

    //语音文件保存路径
    private String fileLocation;

    //界面控件
    private Button startRecordBtn;
    private Button pauseRecordBtn;
    //    private Button startPlayBtn;
    private Button stopRecordBtn;
    //    private Button stopPlayBtn;
    private Button showListBtn;
    private Button settingsBtn;

    private TextView showTimeTv;

    //语音操作对象
    private MediaPlayer mPlayer = null;
    private MediaRecorder mRecorder = null;
    private RecordDao recordDao;
    private Record record;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recordDao = new RecordDao(this);


        //检测是否存在SD卡
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
            fileLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + Global.PATH;
        } else{
            Toast.makeText(MainActivity.this, "without SD card! ", Toast.LENGTH_LONG).show();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(record != null){
            record.stopRecord();
            //save
            if(recordDao.addRecord(record)){
                Toast.makeText(MainActivity.this, "save success! ", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(MainActivity.this, "save fail!", Toast.LENGTH_LONG).show();
            }
            record = null;
        }
        recordDao.close();
        recordDao = null;
    }



}