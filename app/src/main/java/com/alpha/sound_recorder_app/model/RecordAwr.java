package com.alpha.sound_recorder_app.model;

import android.media.MediaPlayer;
import android.media.MediaRecorder;

import com.alpha.sound_recorder_app.util.Global;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by huangshihe on 2015/7/15.
 */
public class RecordAwr implements BaseRecord{

    public RecordAwr(){
        list = new ArrayList<String>();
        isPause = false;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getName() {
        if(name == null || "".equals(name.trim())){
            name = Global.getTime() + ".amr";
//            name = Global.getTime() + ".3gp";
        }
        return name;
    }

    public void setName(String name) {
        if(name == null || "".equals(name.trim())){
            this.name = Global.getTime() + ".amr";
        }else{
            this.name = name;
        }
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public File getRecordFile() {
        return recordFile;
    }

    public void setRecordFile(File recordFile) {
        this.recordFile = recordFile;
    }

    private int _id;
    private String name;
    private Date createTime;
    private long length;
    private int type = Global.TYPE_AWR;
    private File recordFile;

    ///////////////////////////////////
    //录制的秒钟
    private int second;
    //录制的分钟
    private int minute;
    private Timer timer;

    //临时文件
    private File tempFile;

    // 记录需要合成的几段语音文件
    private ArrayList<String> list;

    //是否处于暂时状态
    private boolean isPause;

    private MediaPlayer mPlayer = null;
    private MediaRecorder mRecorder = null;

    public String getRecordTime(){
        return minute + ":" + second;
    }

    public void startRecord(){
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                second++;
                if (second >= 60) {
                    second = 0;
                    minute++;
                }
            }
        };
        timer.schedule(timerTask, 1000, 1000);

        //如果Global.PATH不存在，则创建目录
        File rootLocation = new File(Global.PATH);
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
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
        tempFile = new File(Global.PATH + Global.getTime() + ".amr");
        mRecorder.setOutputFile(tempFile.getAbsolutePath());

        //设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (Exception e) {
            System.out.println("prepare() or start() failed");
        }
        isPause = false;
    }

    public void stopRecord(){
        //如果没有暂停，即正在录制，则停止此段录音，并add list
        if(!isPause){
            timer.cancel();
            if(mRecorder != null){
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
            }
            list.add(tempFile.getName());
            isPause = false;
        }
        getInputCollection();
        setLength(recordFile.length());
        setCreateTime(new Date());
    }

    /**
     * 暂停录音
     */
    public void onPause() {
        isPause = true;
        timer.cancel();
        if(mRecorder != null){
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
        list.add(tempFile.getName());
    }

    /**
     * 合并临时文件
     */
    public void getInputCollection() {
        // 创建音频文件,合并的文件放这里
        recordFile = new File(Global.PATH + getName());
        FileOutputStream fileOutputStream = null;

        if (!recordFile.exists()) {
            try {
                recordFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            fileOutputStream = new FileOutputStream(recordFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
        // list里面为暂停录音 所产生的 几段录音文件的名字，中间几段文件的减去前面的6个字节头文件
        for (int i = 0; i < list.size(); i++) {
            File file = new File(Global.PATH + (String) list.get(i));
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] myByte = new byte[fileInputStream.available()];
                // 文件长度
                int length = myByte.length;
                // 头文件
                if (i == 0) {
                    while (fileInputStream.read(myByte) != -1) {
                        fileOutputStream.write(myByte, 0, length);
                    }
                }
                // 之后的文件，去掉头文件就可以了
                else {
                    while (fileInputStream.read(myByte) != -1) {
                        fileOutputStream.write(myByte, 6, length - 6);
                    }
                }
                fileOutputStream.flush();
                fileInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 结束后关闭流
        try {
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 合成一个文件后，删除之前暂停录音所保存的零碎合成文件
        deleteListRecord();
    }

    /**
     * 删除录音片段
     */
    private void deleteListRecord() {
        for (int i = 0; i < list.size(); i++) {
            File file = new File(Global.PATH + (String) list.get(i));
            if (file.exists()) {
                file.delete();
            }
        }
        list = null;
    }

    //标记是否已经上传
//    private int state;
}
