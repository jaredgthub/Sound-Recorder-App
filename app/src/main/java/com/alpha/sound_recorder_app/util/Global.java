package com.alpha.sound_recorder_app.util;

import android.os.Environment;

import com.alpha.sound_recorder_app.model.Record;

import java.util.Calendar;

/**
 * Created by huangshihe on 2015/7/16.
 */
public class Global {

    /**
     *
     */
    public static final String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/alpha/records/";
//    public static final String PATH = "/";

    public static final int TYPE_AWR = 1;

    public static final int TYPE_WAV = 0;


    public static String getTime(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int days = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);
        int seconds = cal.get(Calendar.SECOND);
        int MI = cal.get(Calendar.MILLISECOND);
        return "" + year + month + days + hour + minutes + seconds + MI;
    }

    public static String getSuffix(Record record){
        String name = record.getName();
        String suffix = name.substring(name.lastIndexOf("."), name.length());
        return suffix;
    }

    public static String getSuffix(int type){
        String suffix;
        switch (type){
            case Global.TYPE_WAV:suffix = "wav";break;
            case Global.TYPE_AWR:suffix = "awr";break;
            default:suffix = "awr";
        }
        return suffix;
    }

    public static String getSuffix(String name){
        return name.substring(name.lastIndexOf("."), name.length());
    }

    public static String getFileNameWithoutSuffix(String name){
        return name.substring(0,name.lastIndexOf("."));
    }

}
