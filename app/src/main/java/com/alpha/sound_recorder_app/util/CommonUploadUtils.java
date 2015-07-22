package com.alpha.sound_recorder_app.util;

import java.io.File;
import java.io.IOException;

import org.json.JSONObject;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.alpha.sound_recorder_app.util.QiNiuConfig;

public class CommonUploadUtils {

    private UploadManager uploadManager;

    File f;

    private Handler handler;

    String expectKey;

    public CommonUploadUtils(Handler handler) {
        uploadManager = new UploadManager();
        this.handler = handler;
    }

    public void runUpload(String name) {
        f = new File(Global.PATH + name);
        new MyUploadRunnalbe().run();
    }

    class MyUploadRunnalbe implements Runnable {
        public void run() {
            //上传文件的名字
            expectKey = f.getName();
            uploadManager.put(f, expectKey, QiNiuConfig.token, new UpCompletionHandler() {
                public void complete(String k, ResponseInfo rinfo, JSONObject response) {
                    Message msg = new Message();
                    msg.obj = response;
                    handler.handleMessage(msg);
                }
            }, null);
        }
    }
}
