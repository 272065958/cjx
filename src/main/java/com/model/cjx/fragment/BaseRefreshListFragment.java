package com.model.cjx.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.AdapterView;

import com.model.cjx.R;

/**
 * Created by cjx on 2017/2/20.
 */
public abstract class BaseRefreshListFragment extends BaseListFragment {
    protected SwipeRefreshLayout refreshLayout;

    protected void initListView(AdapterView.OnItemClickListener itemClickListener, boolean refresh, boolean loadnext) {
        initListView(itemClickListener, loadnext);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        if(refresh){
            refreshLayout.setColorSchemeResources(new int[]{R.color.cjx_colorPrimary});
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refresh();
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
