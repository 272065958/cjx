package com.model.cjx.activity;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;

import com.model.cjx.R;
import com.model.cjx.adapter.MyBaseAdapter;
import com.model.cjx.component.LoadListView;

import java.util.ArrayList;

/**
 * Created by cjx on 2017/3/10.
 * 用于左右滑动的tab页面基类
 */
public abstract class BaseTabListActivity extends BaseTabActivity implements AdapterView.OnItemClickListener {

    protected boolean openLoadMore = true;

    MyFooterLoadListener footerLoadListener = null;
    protected LoadListView[] listViews;
    protected View[] loadViews;
    protected View[] emptyViews;
    protected View[] loadNextViews;
    protected MyBaseAdapter[] adapters;
    protected int[] page;
    protected int[] limit;

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
    protected void onDestroy() {
        if (listViews != null) {
            for (LoadListView listView : listViews) {
                if (listView != null && listView.getAdapter() != null) {
                    listView.setAdapter(null);
                }
            }
        }
        if (adapters != null) {
            for (MyBaseAdapter adapter : adapters) {
                if (adapter != null) {
                    adapter.onDestroy();
                }
            }
        }
        super.onDestroy();
    }

    // 当前显示页
    @Override
    protected void onViewShow(int position) {
        if (listViews[position].getTag() != null) {
            listViews[position].setTag(null);
            executeLoad(position);
        }
    }

    // 获取显示的界面
    @Override
    protected View initPagerView(int i) {
        View v = View.inflate(this, R.layout.item_list_view, null);
        initListView(v, i);
        return v;
    }

    protected void initListView(View v, int i){
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
            loadNextViews[i] = v.findViewById(R.id.loading_next_page);
            page[i] = 1;
            limit[i] = 15;
        }
    }

    // 初始化界面
    @Override
    protected View[] initItemView(int count) {
        listViews = new LoadListView[count];
        loadViews = new View[count];
        if (openLoadMore) {
            page = new int[count];
            limit = new int[count];
            loadNextViews = new View[count];
        }
        emptyViews = new View[count];
        adapters = new MyBaseAdapter[count];
        return super.initItemView(count);
    }

    // 收到刷新广播
    @Override
    protected void onBroadcastReceive(Intent intent) {
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

    // 重新刷新当前界面
    protected void executeLoad(int position) {
        View loadView = loadViews[position];
        if (loadView.getVisibility() == View.GONE) {
            loadViews[position].setVisibility(View.VISIBLE);
        }
        if (openLoadMore) {
            page[position] = 1;
            hideLoadNextView(position);
        }
        loadData(position);
    }

    // 隐藏加载控件
    protected void hideLoadView(int position) {
        View loadView = loadViews[position];
        if (loadView.getVisibility() == View.VISIBLE) {
            loadView.setVisibility(View.GONE);
        }
        if (openLoadMore) {
            hideLoadNextView(position);
        }
    }

    // 隐藏加载下一页的界面
    private void hideLoadNextView(int position){
        View loadNextView = loadNextViews[position];
        if (!(loadNextView instanceof ViewStub) && loadNextView.getVisibility() == View.VISIBLE) {
            loadNextView.setVisibility(View.GONE);
        }
    }

    // 加载数据完成后调用
    protected void onLoadResult(int position, ArrayList list) {
        hideLoadView(position);
        displayData(position, list);
    }

    // 显示数据列表
    protected void displayData(int position, ArrayList list) {
        MyBaseAdapter adapter = adapters[position];
        LoadListView listView = listViews[position];
        if (adapter == null) {
            adapter = getMyBaseAdapter(position, list);
            listView.setVisibility(View.VISIBLE);
            listView.setAdapter(adapter);
            adapters[position] = adapter;
        } else {
            if (!openLoadMore || page[position] == 1) {
                adapter.notifyDataSetChanged(list);
            } else {
                ArrayList oldData = adapter.list;
                oldData.addAll(list);
                adapter.notifyDataSetChanged(oldData);
            }
        }
        if (openLoadMore) {
            listView.setFooterLoadState(false);
            if (list == null || list.size() < limit[position]) { // 不再加载下一页
                listView.setFooterLoadListener(null);
            } else if (page[position] == 1) {
                if (footerLoadListener == null) {
                    footerLoadListener = new MyFooterLoadListener();
                }
                listView.setFooterLoadListener(footerLoadListener);
            }
        }

        if (adapter.getCount() == 0) {
            if (emptyViews[position] instanceof ViewStub) {
                emptyViews[position] = ((ViewStub) emptyViews[position]).inflate();
            } else {
                emptyViews[position].setVisibility(View.VISIBLE);
            }

        } else {
            if (!(emptyViews[position] instanceof ViewStub)) {
                emptyViews[position].setVisibility(View.GONE);
            }
        }
    }

    // 加载数据列表
    protected abstract void loadData(int position);

    protected abstract MyBaseAdapter getMyBaseAdapter(int position, ArrayList list);

    class MyFooterLoadListener implements LoadListView.FooterLoadListener {
        @Override
        public void loadMore(LoadListView view) {
            int position = (int) view.getTag(R.id.tag_type);
            View moreView = loadNextViews[position];
            if (moreView instanceof ViewStub) {
                loadNextViews[position] = ((ViewStub) moreView).inflate();
            } else {
                moreView.setVisibility(View.VISIBLE);
            }
            page[position]++;
            loadData(position);
        }
    }

}
