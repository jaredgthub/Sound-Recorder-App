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
import android.widget.Toast;

import com.alpha.sound_recorder_app.R;
import com.alpha.sound_recorder_app.model.Record;
import com.alpha.sound_recorder_app.util.Global;

import java.io.IOException;

public class RecordActivity extends Activity {

    //语音文件保存路径
//    private String fileName = null;
    private String fileLocation;

    //界面控件
    private Button startRecordBtn;
    private Button startPlayBtn;
    private Button stopRecordBtn;
    private Button stopPlayBtn;
    private Button showListBtn;

    //语音操作对象
    private MediaPlayer mPlayer = null;
    private MediaRecorder mRecorder = null;
    //TODO insert into db;
//    private Db db;
//    private RecordDao recordDao;
    private Record record;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record);

        //开始录音
        startRecordBtn = (Button)findViewById(R.id.startRecord);
        startRecordBtn.setText(R.string.startRecord);
        //绑定监听器
        startRecordBtn.setOnClickListener(new startRecordListener());

        //结束录音
        stopRecordBtn = (Button) findViewById(R.id.stopRecord);
        stopRecordBtn.setText(R.string.stopRecord);
        stopRecordBtn.setOnClickListener(new stopRecordListener());

        //开始播放
        startPlayBtn = (Button) findViewById(R.id.startPlay);
        startPlayBtn.setText(R.string.startPlay);
        startPlayBtn.setOnClickListener(new startPlayListener());

        //结束播放
        stopPlayBtn = (Button) findViewById(R.id.stopPlay);
        stopPlayBtn.setText(R.string.stopPlay);
        stopPlayBtn.setOnClickListener(new stopPlayListener());

        showListBtn = (Button) findViewById(R.id.showList);
        showListBtn.setText(R.string.showList);
        showListBtn.setOnClickListener(new showListListener());

        startRecordBtn.setEnabled(true);
        stopRecordBtn.setEnabled(false);
        startPlayBtn.setEnabled(true);
        stopPlayBtn.setEnabled(false);

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
    class startRecordListener implements OnClickListener{
        @Override
        public void onClick(View v) {

            //设置sdcard的路径
            fileLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + Global.PATH;

            mRecorder = new MediaRecorder();
            //设置麦克风
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            /*
             * 设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default
             * THREE_GPP(3gp格式，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
             */
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

            record = new Record();
            record.setName("");
            fileLocation += record.getName() + ".3gp";
            mRecorder.setOutputFile(fileLocation);

            //设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            try {
                mRecorder.prepare();
            } catch (IOException e) {
                System.out.println("prepare() failed");
            }
            try{
                mRecorder.start();
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("error in start! ");
            }
            startRecordBtn.setEnabled(false);
            stopRecordBtn.setEnabled(true);
        }
    }

    /**
     * stop record
     */
    class stopRecordListener implements OnClickListener{
        @Override
        public void onClick(View v) {
//            db = new Db(RecordActivity.this);
//            recordDao = new RecordDao(db);
//
//            //save
//            if(recordDao.addRecord(record)){
//                Toast.makeText(RecordActivity.this, "save success", Toast.LENGTH_LONG).show();
//            }else{
//                Toast.makeText(RecordActivity.this, "save fail!", Toast.LENGTH_LONG).show();
//            }
//
//            recordDao.close();

            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            startRecordBtn.setEnabled(true);
            stopRecordBtn.setEnabled(false);
        }
    }

    /**
     * start play
     */
    class startPlayListener implements OnClickListener{
        @Override
        public void onClick(View v) {
            mPlayer = new MediaPlayer();
            try{
                mPlayer.setDataSource(fileLocation);
                mPlayer.prepare();
                mPlayer.start();
            } catch(IOException e){
                System.out.println("play fail! ");
            }
            startPlayBtn.setEnabled(false);
            stopPlayBtn.setEnabled(true);
        }
    }

    /**
     * stop play
     */
    class stopPlayListener implements OnClickListener{
        @Override
        public void onClick(View v) {
            mPlayer.release();
            mPlayer = null;
            startPlayBtn.setEnabled(true);
            stopPlayBtn.setEnabled(false);
        }
    }

    class showListListener implements OnClickListener{
        @Override
        public void onClick(View v) {
            startActivity(new Intent(RecordActivity.this,RecordListActivity.class));
        }
    }

}