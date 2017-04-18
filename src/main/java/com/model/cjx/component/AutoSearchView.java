package com.model.cjx.component;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.model.cjx.R;

/**
 * Created by cjx on 2016/7/28.
 */
public class AutoSearchView extends RelativeLayout implements View.OnClickListener{

    EditText textView;
    View clearView;
    OnQueryTextListener listener;
    public AutoSearchView(Context context) {
        super(context);
        initView(context);
    }

    public AutoSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.auto_search_view, this, true);

        textView = (EditText) findViewById(R.id.search_text);
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    clearView.setVisibility(View.GONE);
                } else {
                    clearView.setVisibility(View.VISIBLE);
                }
                if (listener != null) {
                    listener.onQueryTextChange(s.toString());
                }
            }
        });
        clearView = findViewById(R.id.search_clear);
        clearView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        textView.setText(null);
    }

    public void setSearchHint(int hint){
        textView.setHint(hint);
    }

    public void setText(String text){
        textView.setText(text);
    }

    public void setOnQueryTextListener(OnQueryTextListener listener){
        this.listener = listener;
    }

    public interface OnQueryTextListener{
        void onQueryTextChange(String text);
    }
}
