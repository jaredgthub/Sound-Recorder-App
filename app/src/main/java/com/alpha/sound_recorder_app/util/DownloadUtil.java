package com.alpha.sound_recorder_app.util;

import com.qiniu.util.Auth;

/**
 * Created by huangshihe on 2015/7/22.
 */
public class DownloadUtil {

    public static String getUrl(String fileName){
        Auth auth = Auth.create(QiNiuConfig.QINIU_AK, QiNiuConfig.QINIU_SK);
//        String url = "http://forxyz.qiniudn.com/1.jpg";
        String url = "http://forxyz.qiniudn.com/"+fileName;
        //指定时长
        return auth.privateDownloadUrl(url, 3600 * 24 * 3);
    }
}
