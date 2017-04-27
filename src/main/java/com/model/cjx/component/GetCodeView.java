package com.model.cjx.component;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import com.model.cjx.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by cjx on 2016-11-26.
 * 获取验证码的view
 */
public class GetCodeView extends AppCompatButton {

    final int timeOut = 60;
    int currentTime = timeOut;
    Timer timer;
    public GetCodeView(Context context) {
        super(context);
        init();
    }

    public GetCodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){

    }

    public void startTimer() {
        setClickable(false);
        setSelected(false);
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                post(new Runnable() {
                    @Override
                    public void run() {
                        if (timeOut > 0) {
                            currentTime--;
                            setText(timeOut + " s");
                        } else {
                            cancel();
                            timer = null;
                            currentTime = timeOut;
                            setText(R.string.button_get_code);
                            setSelected(true);
                            setClickable(true);
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
    }
}
