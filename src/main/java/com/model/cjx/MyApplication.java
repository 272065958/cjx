package com.model.cjx;

import android.app.Application;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.model.cjx.activity.BaseActivity;
import com.model.cjx.dialog.NetDialog;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Created by cjx on 2017/1/13.
 */
public abstract class MyApplication extends Application{

    private static MyApplication instance;
    private int SCREEN_WIDTH = 0, SCREEN_HEIGHT = 0;
    public String token;

    HashMap<String, WeakReference<NetDialog>> netDialogs;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

//        CrashHandler catchHandler = CrashHandler.getInstance();
//        catchHandler.init(getApplicationContext());
    }

    public static MyApplication getInstance(){
        return instance;
    }

    /*获取当前手机宽度*/
    public int getScreen_width() {
        if (SCREEN_WIDTH == 0) {
            measureScreen();
        }
        return SCREEN_WIDTH;
    }

    /**
     * 获取当前手机高度
     */
    public int getScreen_height() {
        if (SCREEN_HEIGHT <= 0) {
            measureScreen();
        }
        return SCREEN_HEIGHT;
    }

    /**
     * 获取屏幕尺寸
     */
    private void measureScreen() {
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        SCREEN_WIDTH = displayMetrics.widthPixels;
        SCREEN_HEIGHT = displayMetrics.heightPixels;
    }

    public int getBackRes() {
        return R.drawable.white_back;
    }

    public int getToolbarBg(){
        return R.color.cjx_title_bg;
    }

    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public abstract void setUser(Object data);

    public abstract void startLogin(BaseActivity activity);

    public abstract boolean isLogin();

    public void showNetConnectDialog(BaseActivity activity) {
        if (activity == null) {
            Toast.makeText(this, "操作异常,请关闭应用再重试", Toast.LENGTH_SHORT).show();
            return;
        }
        String name = activity.getLocalClassName();
        NetDialog netDialog = null;
        if (netDialogs == null) {
            netDialogs = new HashMap<>();
        }
        if (netDialogs.containsKey(name)) {
            netDialog = netDialogs.get(name).get();
        }
        if (netDialog == null || netDialog.isOutActivity()) {
            netDialog = new NetDialog(activity);
            netDialogs.put(name, new WeakReference<>(netDialog));
        }
        netDialog.show();
    }
}
