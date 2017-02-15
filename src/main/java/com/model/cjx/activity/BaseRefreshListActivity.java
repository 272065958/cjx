package com.model.cjx.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.AdapterView;

import com.model.cjx.R;

/**
 * Created by cjx on 2016/10/28.
 */
public abstract class BaseRefreshListActivity extends BaseListActivity {
    protected SwipeRefreshLayout refreshLayout;

    protected void initListView(AdapterView.OnItemClickListener itemClickListener, boolean refresh, boolean loadnext) {
        initListView(itemClickListener, loadnext);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        if(refresh){
            refreshLayout.setColorSchemeResources(new int[]{R.color.cjx_colorPrimary});
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadData();
                }
            });
        }else{
            refreshLayout.setEnabled(false);
        }
    }

    // 隐藏加载控件
    protected void hideLoadView() {
        super.hideLoadView();
        if (refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
    }

}
