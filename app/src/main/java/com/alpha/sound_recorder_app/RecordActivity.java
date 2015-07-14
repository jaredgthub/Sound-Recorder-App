package com.alpha.sound_recorder_app;

/**
 * Created by huangshihe on 2015/7/14.
 */
import java.io.IOException;
import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class RecordActivity extends Activity {

    private static final String LOG_TAG = "AudioRecordTest";
    //语音文件保存路径
    private String FileName = null;

    //界面控件
    private Button startRecordBtn;
    private Button startPlayBtn;
    private Button stopRecordBtn;
    private Button stopPlayBtn;

    //语音操作对象
    private MediaPlayer mPlayer = null;
    private MediaRecorder mRecorder = null;
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
        stopRecordBtn = (Button)findViewById(R.id.stopRecord);
        stopRecordBtn.setText(R.string.stopRecord);
        stopRecordBtn.setOnClickListener(new stopRecordListener());

        //开始播放
        startPlayBtn = (Button)findViewById(R.id.startPlay);
        startPlayBtn.setText(R.string.startPlay);
        //绑定监听器
        startPlayBtn.setOnClickListener(new startPlayListener());

        //结束播放
        stopPlayBtn = (Button)findViewById(R.id.stopPlay);
        stopPlayBtn.setText(R.string.stopPlay);
        stopPlayBtn.setOnClickListener(new stopPlayListener());

        //设置sdcard的路径
        FileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        //TODO random record name.
        FileName += "/hello.3gp";
    }

    //开始录音
    class startRecordListener implements OnClickListener{
        @Override
        public void onClick(View v) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(FileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            try {
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e(LOG_TAG, "prepare() failed");
            }
            //error!
            try{
                mRecorder.start();
            }catch (Exception e){
                e.printStackTrace();
                Log.e(LOG_TAG,"error in start! ");
            }
            Log.e(LOG_TAG,"start !");
            startRecordBtn.setEnabled(false);
            stopRecordBtn.setEnabled(true);
        }
    }

    /**
     * 停止录音
     */
    class stopRecordListener implements OnClickListener{
        @Override
        public void onClick(View v) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            startRecordBtn.setEnabled(true);
            stopRecordBtn.setEnabled(false);
        }
    }

    /*
     * 播放录音
     */
    class startPlayListener implements OnClickListener{
        @Override
        public void onClick(View v) {
            mPlayer = new MediaPlayer();
            try{
                mPlayer.setDataSource(FileName);
                mPlayer.prepare();
                mPlayer.start();
            }catch(IOException e){
                Log.e(LOG_TAG,"播放失败");
            }
            startPlayBtn.setEnabled(false);
            stopPlayBtn.setEnabled(true);
        }
    }

    /*
     * 停止播放录音
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
}