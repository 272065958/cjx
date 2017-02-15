package com.model.cjx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.model.cjx.R;
import com.model.cjx.adapter.MyBaseAdapter;
import com.model.cjx.adapter.MyPagerAdapter;
import com.model.cjx.bean.ResponseBean;
import com.model.cjx.component.LoadListView;
import com.model.cjx.http.MyCallbackInterface;
import com.model.cjx.util.JsonParser;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by cjx on 2016/6/1.
 * 用于左右滑动的tab页面基类
 */
public abstract class BaseTabActivity extends BaseActivity implements AdapterView.OnItemClickListener,
        TabLayout.OnTabSelectedListener {

    protected LoadListView[] listViews;
    protected SwipeRefreshLayout[] refreshLayouts;
    protected View[] loadViews;
    protected View[] emptyView;
    protected MyBaseAdapter[] adapters;
    protected TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_list);

        initPagerView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_LOGIN:
                    executeLoad(tabLayout.getSelectedTabPosition());
                    break;
            }
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int position = tab.getPosition();
        if (listViews[position].getTag() != null) {
            listViews[position].setTag(null);
            executeLoad(position);
        }

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    // 初始化界面
    private void initPagerView() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        String[] titles = initTitle();
        int pageCount = titles.length;
        listViews = new LoadListView[pageCount];
        loadViews = new View[pageCount];
        refreshLayouts = new SwipeRefreshLayout[pageCount];
        emptyView = new View[pageCount];
        adapters = new MyBaseAdapter[pageCount];
        View views[] = new View[pageCount];
        for (int i = 0; i < pageCount; i++) {
            View v = View.inflate(this, R.layout.item_refresh_list_view, null);
            SwipeRefreshLayout.OnRefreshListener onRefreshListener = getRefreshListener(i);
            SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.refresh_layout);
            if(onRefreshListener == null){
                refreshLayout.setEnabled(false);
            }else{
                refreshLayout.setColorSchemeResources(new int[]{R.color.cjx_colorPrimary});
                refreshLayout.setOnRefreshListener(onRefreshListener);
            }
            LoadListView listView = (LoadListView) v.findViewById(R.id.list_view);
            listView.setDivider(ContextCompat.getDrawable(this, R.drawable.listview_divider));
            listView.setDividerHeight(getResources().getDimensionPixelOffset(R.dimen.auto_margin));
            listView.setOnItemClickListener(this);
            views[i] = v;
            listView.setTag(true);
            refreshLayouts[i] = refreshLayout;
            listViews[i] = listView;
            loadViews[i] = v.findViewById(R.id.loading_view);
            emptyView[i] = v.findViewById(R.id.empty_view);
        }
        MyPagerAdapter adapter = new MyPagerAdapter(views, titles);
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setOnTabSelectedListener(this);
        tabLayout.setupWithViewPager(viewPager);
    }

    // 收到刷新广播
    @Override
    protected void onBroadcastReceive(Intent intent){
        super.onBroadcastReceive(intent);
        refresh();
    }

    // 整体刷新
    protected void refresh() {
        int position = tabLayout.getSelectedTabPosition();
        int count = tabLayout.getTabCount();
        for (int i = 0; i < count; i++) {
            if (i != position) {
                listViews[i].setTag(true); // 标记当前所有界面都要刷新
            }
        }
        executeLoad(position);
    }

    protected void executeLoad(int position){
        if(!refreshLayouts[position].isRefreshing()){
            loadViews[position].setVisibility(View.VISIBLE);
        }
        loadData(position);
    }

    protected void hideLoadView(int position){
        SwipeRefreshLayout refreshLayout = refreshLayouts[position];
        View loadView = loadViews[position];
        if(loadView.getVisibility() == View.VISIBLE){
            loadView.setVisibility(View.GONE);
        }
        if(refreshLayout.isRefreshing()){
            refreshLayout.setRefreshing(false);
        }
    }

    // 加载数据列表
    protected abstract void loadData(int position);

    protected abstract MyBaseAdapter getMyBaseAdapter(int position, ArrayList<?> list);

    // 返回null表示不可下拉刷新
    protected abstract SwipeRefreshLayout.OnRefreshListener getRefreshListener(int position);

    // 加载数据完成后调用
    protected void onLoadResult(int position, ArrayList<?> list){
        hideLoadView(position);
        displayData(position, list);
    }

    // 获取默认的 MyCallbackInterface
    protected MyCallbackInterface getMyCallbackInterface(int position, Type type){
        return new TabCallInterface(position, type);
    }

    // 获取默认的 getMyRefreshListener
    protected MyRefreshListener getMyRefreshListener(int position){
        return new MyRefreshListener(position);
    }

    // 显示数据列表
    protected void displayData(int position, ArrayList<?> list) {
        MyBaseAdapter adapter = adapters[position];
        if (adapter == null) {
            adapter = getMyBaseAdapter(position, list);
            ListView listView = listViews[position];
            listView.setVisibility(View.VISIBLE);
            listView.setAdapter(adapter);
            adapters[position] = adapter;
        } else {
            adapter.notifyDataSetChanged(list);
        }
        if (adapter.getCount() == 0) {
            emptyView[position].setVisibility(View.VISIBLE);
        } else {
            emptyView[position].setVisibility(View.GONE);
        }
    }

    // 初始化标题
    protected abstract String[] initTitle();

    class MyRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        int currentPosition;

        public MyRefreshListener(int position) {
            currentPosition = position;
        }

        @Override
        public void onRefresh() {
            executeLoad(currentPosition);
        }
    }

    class TabCallInterface implements MyCallbackInterface {
        int position;
        Type type;
        public TabCallInterface(int position, Type type){
            this.position = position;
            this.type = type;
        }

        @Override
        public void success(ResponseBean response) {
            ArrayList<?> list = JsonParser.getInstance().fromJson(response.datum, type);
            onLoadResult(position, list);
        }

        @Override
        public void error() {
            onLoadResult(position, null);
        }
    }
}
