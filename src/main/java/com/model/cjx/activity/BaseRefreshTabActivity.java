package com.model.cjx.activity;

import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.model.cjx.R;
import com.model.cjx.component.LoadListView;

/**
 * Created by cjx on 2016/6/1.
 * 用于左右滑动的tab页面基类
 */
public abstract class BaseRefreshTabActivity extends BaseTabListActivity {

    protected SwipeRefreshLayout[] refreshLayouts;

    // 获取显示的界面
    @Override
    protected View initPagerView(int i) {
        View v = View.inflate(this, R.layout.item_refresh_list_view, null);
        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeResources(new int[]{R.color.cjx_colorPrimary});
        refreshLayout.setOnRefreshListener(new MyRefreshListener(i));
        refreshLayouts[i] = refreshLayout;

        LoadListView listView = (LoadListView) v.findViewById(R.id.list_view);
        listView.setDivider(ContextCompat.getDrawable(this, R.drawable.listview_divider));
        listView.setDividerHeight(getResources().getDimensionPixelOffset(R.dimen.auto_margin));
        listView.setOnItemClickListener(this);
        listView.setTag(true);
        listViews[i] = listView;

        loadViews[i] = v.findViewById(R.id.loading_view);
        emptyViews[i] = v.findViewById(R.id.empty_view);
        if (openLoadMore) {
            listView.setTag(R.id.tag_type, i);
            listView.setFooterLoadListener(footerLoadListener);
            loadNextViews[i] = v.findViewById(R.id.loading_next_page);
            page[i] = 1;
            limit[i] = 15;
        }
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
