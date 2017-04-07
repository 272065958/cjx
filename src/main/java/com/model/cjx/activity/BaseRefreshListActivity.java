package com.model.cjx.activity;

import android.support.v4.widget.SwipeRefreshLayout;

import com.model.cjx.R;

/**
 * Created by cjx on 2016/10/28.
 * 列表基类，可以设置是否加载下一页和下拉刷新功能
 */
public abstract class BaseRefreshListActivity extends BaseListActivity {
    protected SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreateView() {
        setContentView(R.layout.activity_refresh_list_view);
        initListView();
        loadData();
    }

    @Override
    protected void initListView() {
        super.initListView();
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.cjx_colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    // 隐藏加载控件
    protected void hideLoadView() {
        super.hideLoadView();
        if (refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
    }

}
