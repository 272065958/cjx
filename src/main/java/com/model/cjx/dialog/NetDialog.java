package com.model.cjx.dialog;

import android.content.ComponentName;
import android.content.Intent;

import com.model.cjx.R;
import com.model.cjx.activity.BaseActivity;

/**
 * Created by cjx on 17-5-26.
 */

public class NetDialog extends TipDialog implements TipDialog.ComfirmListener {
    private BaseActivity activity;

    public NetDialog(BaseActivity context) {
        super(context, false);
        activity = context;

        setText(activity.getString(R.string.tip_title), activity.getString(R.string.http_connect_false),
                activity.getString(R.string.button_sure), activity.getString(R.string.button_cancel));

        setTipComfirmListener(this);
    }

    // 判断当前activity是否还存活
    public boolean isOutActivity() {
        return activity == null || activity.isFinishing();
    }

    @Override
    public void comfirm() {
        // TODO Auto-generated method stub
        Intent intent = null;
        //判断手机系统的版本  即API大于10 就是3.0或以上版本
        if (android.os.Build.VERSION.SDK_INT > 10) {
            intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
        } else {
            intent = new Intent();
            ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
            intent.setComponent(component);
            intent.setAction("android.intent.action.VIEW");
        }
        activity.startActivity(intent);
    }

    @Override
    public void cancel() {

    }
}
