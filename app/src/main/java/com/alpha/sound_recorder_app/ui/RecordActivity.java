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

public class RecordActivity extends Activity {

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
        setContentView(R.layout.record);

        recordDao = new RecordDao(this);

        //开始录音
        startRecordBtn = (Button)findViewById(R.id.startRecord);
        startRecordBtn.setText(R.string.startRecord);
        //绑定监听器
        startRecordBtn.setOnClickListener(new StartRecordListener());

        //暂停录音
        pauseRecordBtn = (Button) findViewById(R.id.pauseRecord);
        pauseRecordBtn.setText(R.string.pauseRcord);
        pauseRecordBtn.setOnClickListener(new PauseRecordBtn());

        //结束录音
        stopRecordBtn = (Button) findViewById(R.id.stopRecord);
        stopRecordBtn.setText(R.string.stopRecord);
        stopRecordBtn.setOnClickListener(new StopRecordListener());

        showListBtn = (Button) findViewById(R.id.showList);
        showListBtn.setText(R.string.showList);
        showListBtn.setOnClickListener(new ShowListListener());

        settingsBtn = (Button) findViewById(R.id.settings);
        settingsBtn.setText(R.string.settings);
        settingsBtn.setOnClickListener(new SettingsListener());

        showTimeTv = (TextView) findViewById(R.id.showTime);

        startRecordBtn.setEnabled(true);
        stopRecordBtn.setEnabled(false);
        pauseRecordBtn.setEnabled(false);

        //检测是否存在SD卡
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
            fileLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + Global.PATH;
        } else{
            Toast.makeText(RecordActivity.this, "without SD card! ", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * start record
     */
    class StartRecordListener implements OnClickListener{
        @Override
        public void onClick(View v) {
            if(record == null){
                record = new Record();
            }
            record.startRecord();
            showTimeTv.setText("");
            startRecordBtn.setEnabled(false);
            stopRecordBtn.setEnabled(true);
            pauseRecordBtn.setEnabled(true);
        }
    }

    /**
     * stop record
     */
    class StopRecordListener implements OnClickListener{
        @Override
        public void onClick(View v) {
            record.stopRecord();
            showTimeTv.setText(record.getRecordTime());
            //save
            if(recordDao.addRecord(record)){
                Toast.makeText(RecordActivity.this, "save success! ", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(RecordActivity.this, "save fail!", Toast.LENGTH_LONG).show();
            }
            record = null;
            startRecordBtn.setEnabled(true);
            stopRecordBtn.setEnabled(false);
            pauseRecordBtn.setEnabled(false);
        }
    }

    class ShowListListener implements OnClickListener{
        @Override
        public void onClick(View v) {
            startActivity(new Intent(RecordActivity.this,RecordListActivity.class));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(record != null){
            record.stopRecord();
            //save
            if(recordDao.addRecord(record)){
                Toast.makeText(RecordActivity.this, "save success! ", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(RecordActivity.this, "save fail!", Toast.LENGTH_LONG).show();
            }
            record = null;
        }
        recordDao.close();
        recordDao = null;
    }

    private class SettingsListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            //TODO finish settings
//            startActivity(new Intent(RecordActivity.this,RecordListActivity.class));
        }
    }

    private class PauseRecordBtn implements OnClickListener {
        @Override
        public void onClick(View v) {
            record.onPause();
            startRecordBtn.setEnabled(true);
            pauseRecordBtn.setEnabled(false);
            stopRecordBtn.setEnabled(true);
        }
    }

}