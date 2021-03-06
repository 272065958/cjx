package com.model.cjx.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.model.cjx.MyApplication;
import com.model.cjx.R;
import com.model.cjx.dialog.LoadDialog;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by cjx on 2016/6/1.
 */
public class BaseActivity extends AppCompatActivity {
    protected TextView toolbarTitle;
    protected Toolbar toolbar;
    public LoadDialog loadDialog;
    BroadcastReceiver refreshReceiver;
    @Override
    public void setContentView(View view) {
        setStatuuBar();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view.setFitsSystemWindows(true);
        }
        super.setContentView(view);
    }

    @Override
    public void setContentView(int layoutResID) {
        setStatuuBar();
        super.setContentView(layoutResID);
    }

    private void setStatuuBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }
    }

    /**
     * 设置toolbar
     * @param showBack     是否显示返回按钮
     * @param backListener 返回按钮监听
     * @param titleRes     标题的资源id
     */
    public void setToolBar(boolean showBack, View.OnClickListener backListener, Object titleRes) {
        MyApplication app = MyApplication.getInstance();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundResource(app.getToolbarBg());
        if (toolbar != null) {
            if (showBack) {
                toolbar.setNavigationIcon(app.getBackRes());
                setSupportActionBar(toolbar);
                if (backListener == null) {
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                } else {
                    toolbar.setNavigationOnClickListener(backListener);
                }

            } else {
                setSupportActionBar(toolbar);
            }
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
            if (toolbarTitle != null && titleRes != null) {
                if(titleRes instanceof String){
                    toolbarTitle.setText((String)titleRes);
                }else{
                    toolbarTitle.setText((int)titleRes);
                }
            }
        }
    }

    /**
     * 设置toolbar的title
     *
     * @param title 标题
     */
    public void setToolbarTitle(String title) {
        if (toolbarTitle != null) {
            toolbarTitle.setText(title);
        }
    }

    /**
     * 设置toolbar的title
     *
     * @param titleRes 标题资源
     */
    public void setToolbarTitle(int titleRes) {
        if (toolbarTitle != null) {
            toolbarTitle.setText(titleRes);
        }
    }

    /**
     * 获取标题
     */
    public String getToolbarTitle() {
        if (toolbarTitle != null) {
            return toolbarTitle.getText().toString();
        }
        return null;
    }

    /**
     * 显示加载对话框
     */
    public void showLoadDislog() {
        if (loadDialog == null) {
            loadDialog = new LoadDialog(this);
        }
        loadDialog.show();
    }

    /**
     * 显示加载对话框
     */
    public void showLoadDislog(DialogInterface.OnCancelListener listener) {
        if (loadDialog == null) {
            loadDialog = new LoadDialog(this);
        }
        loadDialog.setOnCancelListener(listener);
        loadDialog.show();
    }

    // 设置加载匡的文字提示
    public void setLoadTip(String tip) {
        if (loadDialog != null) {
            loadDialog.setTip(tip);
            if(!loadDialog.isShowing()){
                loadDialog.show();
            }
        } else {
            showLoadDislog(tip);
        }
    }

    public void showLoadDislog(String tip) {
        if (loadDialog == null) {
            loadDialog = new LoadDialog(this, tip);
        }
        if(!loadDialog.isShowing()){
            loadDialog.show();
        }
    }

    /**
     * 隐藏加载对话框
     */
    public void dismissLoadDialog() {
        if (loadDialog != null && loadDialog.isShowing()) {
            loadDialog.dismiss();
        }
    }

    Timer timer;
    InputMethodManager imm;

    // 显示键盘
    public void showInput() {
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (imm == null) {
                    imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                }
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
            }

        }, 200);//这里的时间大概是自己测试的
    }

    /**
     * @return -1=没有网络
     */
    public int checkNetEnvironment() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) { // 打开了网络
            return info.getType();
        } else {
            return -1;
        }
    }

    // 注册广播
    protected void registerReceiver(IntentFilter filter){
        if(refreshReceiver != null){
            return ;
        }
        refreshReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onBroadcastReceive(intent);
            }
        };
        registerReceiver(refreshReceiver, filter);
    }

    // 收到广播回调
    protected void onBroadcastReceive(Intent intent){

    }

    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            imm = null;
        }
        unregisterMyReceiver();
        super.onDestroy();
    }

    public void unregisterMyReceiver(){
        if(refreshReceiver != null){
            unregisterReceiver(refreshReceiver);
            refreshReceiver = null;
        }
    }
}
