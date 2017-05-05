package com.model.cjx.component;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

import com.model.cjx.R;
import com.model.cjx.util.Tools;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by cjx on 2016-11-26.
 * 获取验证码的view
 */
public class GetCodeView extends AppCompatButton {

    final int timeOut = 60;
    int currentTime;
    Timer timer;
    EditText phoneView;
    boolean isTimerStart = false;
    public GetCodeView(Context context) {
        super(context);
        init();
    }

    public GetCodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        currentTime = timeOut;
        Log.e("TAG", "init time = "+currentTime);
    }

    public void bind(EditText phoneView){
        this.phoneView = phoneView;
    }

    public void startTimer() {
        setClickable(false);
        setSelected(false);
        isTimerStart = true;
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                post(new Runnable() {
                    @Override
                    public void run() {
                        if (currentTime > 0) {
                            currentTime--;
                            setText(currentTime + " s");
                        } else {
                            cancel();
                            isTimerStart = false;
                            if(phoneView != null && Tools.isPhone(phoneView.getText().toString())){
                                setSelected(true);
                                setClickable(true);
                            }
                            timer = null;
                            currentTime = timeOut;
                            setText(R.string.button_get_code);
                        }
                    }
                });
            }
        }, 0, 1000);
    }

    // 终止定时器
    public void stopTimer(){
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        isTimerStart = false;
        currentTime = timeOut;
    }

    public void setTime(long prevTime) {
        currentTime = (int) Math.ceil((prevTime - System.currentTimeMillis()) / 1000);
        setText(currentTime + " s");
        startTimer();
    }

    public boolean isTimerStart() {
        return isTimerStart;
    }
}
