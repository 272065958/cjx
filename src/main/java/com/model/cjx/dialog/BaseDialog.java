package com.model.cjx.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by cjx on 2016/8/3.
 */
public class BaseDialog extends TagDialog {

    public BaseDialog(Context context, int width){
        super(context);
        if(context instanceof Activity){
            Window dw = getWindow();
            WindowManager.LayoutParams wl = dw.getAttributes();
            wl.width = width;
            dw.setAttributes(wl);
        }
    }
}
