package com.model.cjx;

import android.app.Application;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.model.cjx.activity.BaseActivity;

/**
 * Created by cjx on 2017/1/13.
 */
public abstract class MyApplication extends Application{

    public final static String PREFERENCE_ACCOUNT = "account";
    public final static String PREFERENCE_PASSWORD = "password";
    
    private static MyApplication instance;
    private int SCREEN_WIDTH = 0, SCREEN_HEIGHT = 0;
    public String token;
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
}
