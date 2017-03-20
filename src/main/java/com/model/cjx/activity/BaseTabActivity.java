package com.model.cjx.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.model.cjx.R;
import com.model.cjx.adapter.MyPagerAdapter;

/**
 * Created by cjx on 2016/6/1.
 * 用于左右滑动的tab页面基类
 */
public abstract class BaseTabActivity extends BaseActivity implements TabLayout.OnTabSelectedListener {

    protected TabLayout tabLayout;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_list);

        initView();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int position = tab.getPosition();
        onViewShow(position);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    protected void onDestroy() {
        if(viewPager.getAdapter() != null){
            viewPager.setAdapter(null);
        }
        super.onDestroy();
    }

    // 初始化界面
    protected void initView() {
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        String[] titles = initTitle();
        int pageCount = titles.length;
        MyPagerAdapter adapter = new MyPagerAdapter(initItemView(pageCount), titles);
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setOnTabSelectedListener(this);
        tabLayout.setupWithViewPager(viewPager);
    }

    // 初始化每页的界面
    protected View[] initItemView(int count){
        View[] views = new View[count];
        for (int i = 0; i < count; i++) {
            views[i] = initPagerView(i);
        }
        return views;
    }

    // 设置tab的文字
    protected void setTabText(int position, String text){
        tabLayout.getTabAt(position).setText(text);
    }

    // 初始化标题
    protected abstract String[] initTitle();

    // 获取显示的界面
    protected abstract View initPagerView(int position);

    // 当前显示页
    protected abstract void onViewShow(int position);
}
