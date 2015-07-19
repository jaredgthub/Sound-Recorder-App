package com.alpha.sound_recorder_app.ui;

/**
 * Created by huangshihe on 2015/7/14.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alpha.sound_recorder_app.R;
import com.alpha.sound_recorder_app.dao.RecordDao;
import com.alpha.sound_recorder_app.model.BaseRecord;
import com.alpha.sound_recorder_app.model.RecordAwr;
import com.alpha.sound_recorder_app.model.RecordWav;
import com.alpha.sound_recorder_app.util.Global;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    //界面控件
    private Button startRecordBtn;
    private Button stopRecordBtn;
    private Button showListBtn;
    private Button settingsBtn;
    private Timer timer = new Timer();

    private TextView showTimeTV;

    //语音操作对象
    private RecordDao recordDao;
    private BaseRecord record;

    SharedPreferences settings;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if(record != null){
                showTimeTV.setText(record.getRecordTime());
            }
        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showTimeTV = (TextView) findViewById(R.id.showTimeTV);
        showTimeTV.setText("00:00");

        //检测是否存在SD卡
        if (!Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
            Toast.makeText(MainActivity.this, "without SD card! ", Toast.LENGTH_LONG).show();
        }else{
            File rootLocation = new File(Global.PATH);
            if (!rootLocation.exists()) {
                rootLocation.mkdirs();
            }
        }

        settings = getSharedPreferences("com.alpha.sound_recorder_app_preferences", Context.MODE_PRIVATE);
        recordDao = new RecordDao(this);

        startRecordBtn = (Button) findViewById(R.id.startRecordBtn);
        startRecordBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if(record == null){
                            switch (settings.getString("example_list",""+Global.TYPE_AWR)){
                                case ""+Global.TYPE_AWR:
                                    record = new RecordAwr();
                                    break;
                                case ""+Global.TYPE_WAV:
                                    record = new RecordWav();
                                    break;
                                default:
                                    record = new RecordAwr();
                            }
                        }
                        record.startRecord();
                        setTimerTask();

                        stopRecordBtn.setEnabled(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        record.onPause();
                        break;
                }
                return false;
            }
        });

        stopRecordBtn = (Button) findViewById(R.id.stopRecordBtn);
        stopRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record.stopRecord();
                showTimeTV.setText(record.getRecordTime());
                //save
                if(recordDao.addRecord(record)){
                    Toast.makeText(MainActivity.this, "save success! ", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(MainActivity.this, "save fail!", Toast.LENGTH_LONG).show();
                }
                record = null;
                showTimeTV.setText("00:00");
                stopRecordBtn.setEnabled(false);
                startActivity(new Intent(MainActivity.this,RecordListActivity.class));
            }
        });
        stopRecordBtn.setEnabled(false);

        showListBtn = (Button) findViewById(R.id.showListBtn);
        showListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeTV.setText("00:00");
                startActivity(new Intent(MainActivity.this, RecordListActivity.class));
            }
        });

        settingsBtn = (Button) findViewById(R.id.settingsBtn);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this,SettingsActivity.class));
            }
        });
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

    private void setTimerTask(){
        timer.schedule(new TimerTask(){
            @Override
            public void run(){
                Message message = new Message();
                handler.sendMessage(message);
            }
        }, 0, 1000);
    }

}