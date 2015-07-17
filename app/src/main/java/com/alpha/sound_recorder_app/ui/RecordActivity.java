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
import com.alpha.sound_recorder_app.dao.RecordDao;
import com.alpha.sound_recorder_app.model.Record;
import com.alpha.sound_recorder_app.util.Global;

import java.io.File;
import java.io.IOException;

public class RecordActivity extends Activity {

    //语音文件保存路径
    private String fileLocation;

    //界面控件
    private Button startRecordBtn;
    private Button pauseRecordBtn;
    private Button startPlayBtn;
    private Button stopRecordBtn;
    private Button stopPlayBtn;
    private Button showListBtn;
    private Button settingsBtn;

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

        pauseRecordBtn = (Button) findViewById(R.id.pauseRecord);
        pauseRecordBtn.setText(R.string.pauseRcord);
        pauseRecordBtn.setOnClickListener(new PauseRecordBtn());

        //结束录音
        stopRecordBtn = (Button) findViewById(R.id.stopRecord);
        stopRecordBtn.setText(R.string.stopRecord);
        stopRecordBtn.setOnClickListener(new StopRecordListener());

        //开始播放
        startPlayBtn = (Button) findViewById(R.id.startPlay);
        startPlayBtn.setText(R.string.startPlay);
        startPlayBtn.setOnClickListener(new StartPlayListener());

        //结束播放
        stopPlayBtn = (Button) findViewById(R.id.stopPlay);
        stopPlayBtn.setText(R.string.stopPlay);
        stopPlayBtn.setOnClickListener(new StopPlayListener());

        showListBtn = (Button) findViewById(R.id.showList);
        showListBtn.setText(R.string.showList);
        showListBtn.setOnClickListener(new ShowListListener());

        settingsBtn = (Button) findViewById(R.id.settings);
        settingsBtn.setText(R.string.settings);
        settingsBtn.setOnClickListener(new SettingsListener());

        startRecordBtn.setEnabled(true);
        stopRecordBtn.setEnabled(false);
        startPlayBtn.setEnabled(false);
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
    class StartRecordListener implements OnClickListener{
        @Override
        public void onClick(View v) {

            //设置sdcard的路径
            fileLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + Global.PATH;
            //如果Global.PATH不存在，则创建目录
            File rootLocation = new File(fileLocation);
            if (!rootLocation.exists()) {
                rootLocation.mkdirs();
            }
            mRecorder = new MediaRecorder();
            //设置麦克风
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            /*
             * 设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default
             * THREE_GPP(3gp格式，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
             */
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

            record = new Record();
            fileLocation += record.getName();
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
    class StopRecordListener implements OnClickListener{
        @Override
        public void onClick(View v) {
            //save
            if(recordDao.addRecord(record)){
                Toast.makeText(RecordActivity.this, "save success! ", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(RecordActivity.this, "save fail!", Toast.LENGTH_LONG).show();
            }
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            startRecordBtn.setEnabled(true);
            stopRecordBtn.setEnabled(false);
            startPlayBtn.setEnabled(true);
        }
    }

    /**
     * start play
     */
    class StartPlayListener implements OnClickListener{
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
    class StopPlayListener implements OnClickListener{
        @Override
        public void onClick(View v) {
            mPlayer.release();
            mPlayer = null;
            startPlayBtn.setEnabled(true);
            stopPlayBtn.setEnabled(false);
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
        recordDao.close();
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

        }
    }


//    ///////////////////////////////////////////
//    /**
//     * 停止录音
//     */
//    public void stop() {
//        timer.cancel();
//        Toast.makeText(context, "录音时间:"+minute+"分"+second+"秒", 1).show();
//        // 这里写暂停处理的 文件！加上list里面 语音合成起来
//        if (isPause) {
//            // 在暂停状态按下结束键,处理list就可以了
//            if (inThePause) {
//                getInputCollection(list, false);
//            }
//            // 在正在录音时，处理list里面的和正在录音的语音
//            else {
//                list.add(myRecAudioFile.getPath());
//                recodeStop();
//                getInputCollection(list, true);
//            }
//
//            // 还原标志位
//            isPause = false;
//            inThePause = false;
//        }
//        // 若录音没有经过任何暂停
//        else {
//            if (myRecAudioFile != null) {
//                // 停止录音
//                mMediaRecorder01.stop();
//                mMediaRecorder01.release();
//                mMediaRecorder01 = null;
//                DecimalFormat df = new DecimalFormat("#.000");
//                if (myRecAudioFile.length() <= 1024 * 1024) {
//                    // length1 = (myRecAudioFile.length() / 1024.0)+"";
//
//                    length1 = df.format(myRecAudioFile.length() / 1024.0) + "K";
//                } else {
//                    // length1 = (myRecAudioFile.length() / 1024.0 / 1024)+"";
//                    // DecimalFormat df = new DecimalFormat("#.000");
//                    length1 = df
//                            .format(myRecAudioFile.length() / 1024.0 / 1024)
//                            + "M";
//                }
//                Log.d("Record", "录音文件大小：" + length1);
//            }
//
//        }
//    }
//
//    /**
//     * 暂停录音
//     */
//    public void onPause() {
//        isPause = true;
//        // 已经暂停过了，再次点击按钮 开始录音，录音状态在录音中
//        if (inThePause) {
//            recordStart();
//            inThePause = false;
//        }
//        // 正在录音，点击暂停,现在录音状态为暂停
//        else {
//            // 当前正在录音的文件名，全程
//            recodeStop();
//            // 计时停止
//            timer.cancel();
//            list.add(myRecAudioFile.getPath());
//            inThePause = true;
//        }
//    }
//    private void recordStart(){
//        timer = new Timer();
//        TimerTask timerTask = new TimerTask() {
//
//            @Override
//            public void run() {
//                second++;
//                if (second >= 60) {
//                    second = 0;
//                    minute++;
//                }
//
//            }
//        };
//        timer.schedule(timerTask, 1000, 1000);
//        String mMinute1 = getTime();
//        // 创建音频文件
//        myRecAudioFile = new File(myRecAudioDir, mMinute1 + SUFFIX);
//        mMediaRecorder01 = new MediaRecorder();
//        mMediaRecorder01.reset();
//        // 设置录音为麦克风
//        mMediaRecorder01.setAudioSource(MediaRecorder.AudioSource.MIC);
//        mMediaRecorder01.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
//        mMediaRecorder01.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//
//        // 录音文件保存这里
//        mMediaRecorder01.setOutputFile(myRecAudioFile.getAbsolutePath());
//        try {
//            mMediaRecorder01.prepare();
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mMediaRecorder01.start();
//    }
//
//    // 打开录音播放程序
//    private void openFile(File f) {
//        Intent intent = new Intent();
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setAction(android.content.Intent.ACTION_VIEW);
//        String type = getMIMEType(f);
//        intent.setDataAndType(Uri.fromFile(f), type);
//        context.startActivity(intent);
//    }
//
//    private String getMIMEType(File f) {
//
//        String end = f
//                .getName()
//                .substring(f.getName().lastIndexOf(".") + 1,
//                        f.getName().length()).toLowerCase();
//        String type = "";
//        if (end.equals("mp3") || end.equals("aac") || end.equals("amr")
//                || end.equals("mpeg") || end.equals("mp4")) {
//            type = "audio";
//        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
//                || end.equals("jpeg")) {
//            type = "image";
//        } else {
//            type = "*";
//        }
//        type += "/";
//        return type;
//    }
//
//    /**
//     * @param isAddLastRecord
//     *            是否需要添加list之外的最新录音，一起合并
//     * @return 将合并的流用字符保存
//     */
//    public void getInputCollection(List list, boolean isAddLastRecord) {
//
//        String mMinute1 = getTime();
//        // Toast.makeText(EX07.this,
//        // "当前时间是:"+mMinute1,Toast.LENGTH_LONG).show();
//
//        // 创建音频文件,合并的文件放这里
//        bestFile = new File(myRecAudioDir, mMinute1 + SUFFIX);
//        FileOutputStream fileOutputStream = null;
//
//        if (!bestFile.exists()) {
//            try {
//                bestFile.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        try {
//            fileOutputStream = new FileOutputStream(bestFile);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        // list里面为暂停录音 所产生的 几段录音文件的名字，中间几段文件的减去前面的6个字节头文件
//
//        for (int i = 0; i < list.size(); i++) {
//            File file = new File((String) list.get(i));
//            Log.d("list的长度", list.size() + "");
//            try {
//                FileInputStream fileInputStream = new FileInputStream(file);
//                byte[] myByte = new byte[fileInputStream.available()];
//                // 文件长度
//                int length = myByte.length;
//                System.out.println("开始长度："+length);
//                // 头文件
//                if (i == 0) {
//                    while (fileInputStream.read(myByte) != -1) {
//                        fileOutputStream.write(myByte, 0, length);
//                    }
//                }
//
//                // 之后的文件，去掉头文件就可以了
//                else {
//                    while (fileInputStream.read(myByte) != -1) {
//
//                        fileOutputStream.write(myByte, 6, length - 6);
//                    }
//                }
//
//                fileOutputStream.flush();
//                fileInputStream.close();
//                System.out.println("合成文件长度：" + bestFile.length());
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//        // 结束后关闭流
//        try {
//            fileOutputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        // 合成一个文件后，删除之前暂停录音所保存的零碎合成文件
//        deleteListRecord(isAddLastRecord);
//    }
//
//    /**
//     * 删除录音片段
//     * @param isAddLastRecord
//     */
//    private void deleteListRecord(boolean isAddLastRecord) {
//        for (int i = 0; i < list.size(); i++) {
//            File file = new File((String) list.get(i));
//            if (file.exists()) {
//                file.delete();
//            }
//        }
//        // 正在暂停后，继续录音的这一段音频文件
//        if (isAddLastRecord) {
//            myRecAudioFile.delete();
//        }
//    }
//
//    private void recodeStop() {
//        if (mMediaRecorder01 != null && !isStopRecord) {
//            // 停止录音
//            mMediaRecorder01.stop();
//            mMediaRecorder01.release();
//            mMediaRecorder01 = null;
//        }
//
//        timer.cancel();
//    }
//
//    private String getTime() {
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH：mm：ss");
//        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
//        String time = formatter.format(curDate);
//        System.out.println("当前时间");
//        return time;
//    }
//
//    // 结束后需要释放资源
//    public void onDestroy() {
//        // TODO Auto-generated method stub
//        if (mMediaRecorder01 != null && !isStopRecord) {
//            // 停止录音
//            mMediaRecorder01.stop();
//            mMediaRecorder01.release();
//            mMediaRecorder01 = null;
//        }
//    }

    //////////////////////////////////////////
}