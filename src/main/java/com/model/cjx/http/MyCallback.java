package com.model.cjx.http;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.model.cjx.MyApplication;
import com.model.cjx.R;
import com.model.cjx.activity.BaseActivity;
import com.model.cjx.bean.Code;
import com.model.cjx.bean.ResponseBean;
import com.model.cjx.util.JsonParser;
import com.model.cjx.util.Tools;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by cjx on 2016/7/18.
 */
public class MyCallback implements Callback {

    BaseActivity activity;
    MyCallbackInterface callbackInterface;
    Request request;

    public MyCallback(BaseActivity activity, MyCallbackInterface callbackInterface, Request request) {
        this.activity = activity;
        this.callbackInterface = callbackInterface;
        this.request = request;
    }

    @Override
    public void onFailure(Call call, final IOException e) {
        if (activity == null) {
            return;
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callbackInterface.error();
                if (e instanceof SocketTimeoutException) {
                    activity.showToast(activity.getString(R.string.http_error));
                } else {
                    activity.showToast(activity.getString(R.string.http_steam_exception));
                }
            }
        });
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        final String body = response.body().string();
        final ResponseBean r = JsonParser.getInstance().getDatumResponse(body);
        if (r == null) {
            // 保存异常信息到文件
            Tools.saveToFile(activity, "http_response", body);
        }
        if (activity == null) {
            return;
        }
        if (r == null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callbackInterface.error();
                    activity.showToast(activity.getString(R.string.http_exception));
                }
            });
        } else {
            if (r.code == Code.SUCCESS) {
                final Object obj = callbackInterface.parser(r); // 异步解析数据
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callbackInterface.success(obj);
                    }
                });
            } else { // 提示错误信息
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (r.code) {
                            case Code.TOKEN_INVALID:
                                ((MyApplication) activity.getApplication()).setUser(null);
                                HttpUtils.getInstance().clearCookie();
                                SharedPreferences sharedPreferences = activity.getSharedPreferences(
                                        activity.getString(R.string.cjx_preference), Activity.MODE_PRIVATE);
                                String oldAcc = sharedPreferences.getString(MyApplication.PREFERENCE_ACCOUNT, null);

                                if (!TextUtils.isEmpty(oldAcc)) {
                                    String oldPwd = sharedPreferences.getString(MyApplication.PREFERENCE_ACCOUNT, null);
                                    autoLogin(oldAcc, oldPwd, request);
                                } else {
                                    callbackInterface.error();
                                    activity.showToast(r.message);
                                    MyApplication.getInstance().startLogin();
                                }
                                break;
                            default:
                                callbackInterface.error();
                                activity.showToast(r.message);
                                break;
                        }
                    }
                });
            }
        }

    }

    private void autoLogin(String acc, String pwd, Request request) {
        CustomCallback myCallback = new CustomCallback(this, request);
        MyApplication.getInstance().autoLogin(myCallback, acc, pwd);
    }

    public class CustomCallback implements Callback {
        Callback callback;
        Request request;

        public CustomCallback(Callback callback, Request request) {
            this.callback = callback;
            this.request = request;
        }

        @Override
        public void onFailure(Call call, IOException e) {
            callback.onFailure(call, e);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            Log.e("TAG", "auto login response");
            if (activity == null || activity.isFinishing()) {
                return;
            }
            final ResponseBean r = JsonParser.getInstance().getDatumResponse(response.body().string());
            if (r.code == Code.SUCCESS) {
                HttpUtils.getInstance().enqueue(callback, request);
                MyApplication app = (MyApplication) activity.getApplication();
                app.setUser(r.datum);
                activity.sendBroadcast(new Intent(MyApplication.ACTION_LOGIN)); //发送登录成功广播
            } else {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callbackInterface.error();
                        activity.showToast(r.message);
                        MyApplication.getInstance().startLogin();
                    }
                });
            }
        }
    }
}
