package com.model.cjx.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.model.cjx.R;

/**
 * Created by cjx on 2016/6/1.
 * 用于左右滑动的tab页面基类
 */
public abstract class BaseTabRefreshActivity extends BaseTabListActivity {

    protected SwipeRefreshLayout[] refreshLayouts;

    // 获取显示的界面
    @Override
    protected View initPagerView(int i) {
        View v = View.inflate(this, R.layout.item_refresh_list_view, null);
        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.cjx_colorPrimary);
        refreshLayout.setOnRefreshListener(new MyRefreshListener(i));
        refreshLayouts[i] = refreshLayout;

        initListView(v, i);
        return v;
    }

    // 初始化界面
    @Override
    protected View[] initItemView(int count) {
        refreshLayouts = new SwipeRefreshLayout[count];
        return super.initItemView(count);
    }

    // 重新刷新当前界面
    protected void executeLoad(int position) {
        if (!refreshLayouts[position].isRefreshing()) {
            loadViews[position].setVisibility(View.VISIBLE);
        }
        super.executeLoad(position);
    }

    // 隐藏加载控件
    protected void hideLoadView(int position) {
        SwipeRefreshLayout refreshLayout = refreshLayouts[position];
        if (refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
        super.hideLoadView(position);
    }

    class MyRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        int currentPosition;

        MyRefreshListener(int position) {
            currentPosition = position;
        }

        @Override
        public void onRefresh() {
            executeLoad(currentPosition);
        }
    }
}
