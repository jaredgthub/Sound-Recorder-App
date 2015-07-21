package com.alpha.sound_recorder_app.util;

import org.json.JSONException;

import com.qiniu.api.auth.digest.Mac;

import com.qiniu.api.auth.AuthException;
import com.qiniu.api.rs.PutPolicy;

/**
 * Created by huangshihe on 2015/7/21.
 */
public final class QiNiuConfig {
    public static final String token = getToken();
    public static final String QINIU_AK = "DG4wg82krO0uJkEKDz3AIqPKKlTAcQDbyoM43_qN";
    public static final String QINIU_SK = "zJdB1dObu3vadQix6CMRL9fyWn35Z2JVaJXm9lkI";
    public static final String QINIU_BUCKNAME = "forxyz";

    public static String getToken() {
        Mac mac = new Mac(QiNiuConfig.QINIU_AK, QiNiuConfig.QINIU_SK);
        PutPolicy putPolicy = new PutPolicy(QiNiuConfig.QINIU_BUCKNAME);
        putPolicy.returnBody = "{\"name\": $(fname),\"size\": \"$(fsize)\",\"w\": \"$(imageInfo.width)\",\"h\": \"$(imageInfo.height)\",\"key\":$(etag)}";
        try {
            String uptoken = putPolicy.token(mac);
            System.out.println("debug:uptoken = " + uptoken);
            return uptoken;
        } catch (AuthException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
