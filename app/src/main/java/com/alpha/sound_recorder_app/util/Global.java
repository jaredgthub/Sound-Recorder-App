package com.alpha.sound_recorder_app.util;

import android.os.Environment;

import java.util.Calendar;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

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

    public static byte[] hmacSHA1Encrypt(String encryptText, String encryptKey)
            throws Exception {
        byte[] data = encryptKey.getBytes("UTF-8");
        // 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKey secretKey = new SecretKeySpec(data, "HmacSHA1");
        // 生成一个指定 Mac 算法 的 Mac 对象
        Mac mac = Mac.getInstance("HmacSHA1");
        // 用给定密钥初始化 Mac 对象
        mac.init(secretKey);
        byte[] text = encryptText.getBytes("UTF-8");
        // 完成 Mac 操作
        return mac.doFinal(text);
    }
}
