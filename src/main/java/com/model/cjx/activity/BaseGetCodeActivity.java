package com.model.cjx.activity;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.model.cjx.R;
import com.model.cjx.component.GetCodeView;

/**
 * Created by cjx on 2016/12/12.
 */
public abstract class BaseGetCodeActivity extends BaseActivity {
    protected EditText phoneView, codeView;
    protected GetCodeView getCodeView;
    protected String type;

    @Override
    protected void onDestroy() {
        getCodeView.stopTimer();
        super.onDestroy();
    }

    // 获取验证码
    protected void getCode() {
        String phone = phoneView.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, getString(R.string.register_phone_hint), Toast.LENGTH_SHORT).show();
            return;
        }
        showLoadDislog();
        requestCode(phone);
    }

    // 检查验证码是否正确
    protected boolean checkCode(){
        String trueCode = (String) codeView.getTag();
        if(TextUtils.isEmpty(trueCode)){
            Toast.makeText(this, getString(R.string.register_true_code_null_hint), Toast.LENGTH_SHORT).show();
            return false;
        }
        String code = codeView.getText().toString();
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, getString(R.string.register_code_null_hint), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!code.equals(trueCode)) {
            Toast.makeText(this, getString(R.string.register_code_error_hint), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    protected abstract void requestCode(String phone);
}
