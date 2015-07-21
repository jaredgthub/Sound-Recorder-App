package com.alpha.sound_recorder_app.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alpha.sound_recorder_app.R;
import com.alpha.sound_recorder_app.model.User;
import com.baidu.api.AsyncBaiduRunner;
import com.baidu.api.Baidu;
import com.baidu.api.BaiduDialog;
import com.baidu.api.BaiduDialogError;
import com.baidu.api.BaiduException;
import com.baidu.frontia.api.FrontiaAuthorization;
import com.baidu.frontia.api.FrontiaSocialShare;
import com.baidu.frontia.api.FrontiaSocialShareContent;
import com.google.gson.Gson;

import java.io.IOException;

public class ShareActivity extends Activity {

    private TextView mTvAccessToken = null;
    private TextView mTvGetUserInfo = null;
    private Baidu baidu = null;

    private FrontiaSocialShare mSocialShare;


    private FrontiaSocialShareContent mImageContent = new FrontiaSocialShareContent();

    private FrontiaAuthorization mAuthorization;
    private final static String Scope_Basic = "basic";

    private final static String Scope_Netdisk = "netdisk";

    private Gson mGson = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        mTvAccessToken = (TextView) findViewById(R.id.tv_access_token);
        mTvGetUserInfo = (TextView) findViewById(R.id.tv_user_info);
        mGson = new Gson();
    }

    public void clickOAuthBtn(View view){
        baidu = new Baidu("wBk3HUHSnGPzGw9V43B2UTWz",this);
        baidu.authorize(this, true, true, new BaiduDialog.BaiduDialogListener() {
            @Override
            public void onComplete(Bundle bundle) {
                refreshUI(baidu.getAccessToken());
            }

            @Override
            public void onBaiduException(BaiduException e) {
                refreshUI("exception");
            }

            @Override
            public void onError(BaiduDialogError baiduDialogError) {
                refreshUI("error");
            }

            @Override
            public void onCancel() {
                refreshUI("cancel");
            }
        });
    }

    public void clickGetUserInfo(View view){
        String token = baidu.getAccessToken();
        if(TextUtils.isEmpty(token)){
            Toast.makeText(this,"Token is null",Toast.LENGTH_SHORT).show();
        }else{
            AsyncBaiduRunner baiduRunner = new AsyncBaiduRunner(baidu);
            String url = "https://openapi.baidu.com/rest/2.0/passport/users/getInfo";
            baiduRunner.request(url, null, "GET", new AsyncBaiduRunner.RequestListener() {
                @Override
                public void onComplete(String s) {
                    User user = mGson.fromJson(s,User.class);
                    refreshUserInfo(user.toString());
                }

                @Override
                public void onIOException(IOException e) {
                    refreshUserInfo("onIOException");
                }

                @Override
                public void onBaiduException(BaiduException e) {
                    refreshUserInfo("onBaiduException");
                }
            });

            /*new Thread(){
                public void run() {
                    String url = "https://openapi.baidu.com/rest/2.0/passport/users/getInfo";
                    try {
                        final String jsonText = baidu.request(url,null,"GET");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTvGetUserInfo.setText(jsonText);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (BaiduException e) {
                        e.printStackTrace();
                    }
                }
            }.start();*/

        }
    }

    private void refreshUI(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTvAccessToken.setText(msg);
            }
        });
    }
    private void refreshUserInfo(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTvGetUserInfo.setText(msg);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
