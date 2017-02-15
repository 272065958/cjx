package com.model.cjx.activity;

import android.os.Bundle;
import android.widget.AdapterView;

import com.model.cjx.adapter.MyBaseAdapter;
import com.model.cjx.adapter.TreeAdapter;

import java.util.ArrayList;

/**
 * Created by cjx on 2016/8/17.
 */
public abstract class BaseTreeSelectActivity extends BaseTreeActivity implements AdapterView.OnItemLongClickListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listView.setOnItemLongClickListener(this);
    }

    @Override
    protected MyBaseAdapter getMyBaseAdapter(ArrayList<?> list) {
        return new TreeAdapter(list, this);
    }

}
