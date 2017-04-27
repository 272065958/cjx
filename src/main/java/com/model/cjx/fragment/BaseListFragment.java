package com.model.cjx.fragment;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;

import com.model.cjx.R;
import com.model.cjx.adapter.MyBaseAdapter;
import com.model.cjx.component.LoadListView;
import java.util.ArrayList;

/**
 * Created by cjx on 2017/2/20.
 */
public abstract class BaseListFragment extends BaseFragment {
    protected boolean openLoadMore = true;

    protected LoadListView listView;
    protected View loadView, emptyView, loadNextView;

    AdapterView.OnItemClickListener itemClickListener;
    LoadListView.FooterLoadListener footerLoadListener;

    int page, limit;

    protected void initListView(AdapterView.OnItemClickListener itemClickListener, boolean openLoadMore) {
        loadView = view.findViewById(R.id.loading_view);
        listView = (LoadListView) view.findViewById(R.id.list_view);
        this.openLoadMore = openLoadMore;
        if(openLoadMore){
            page = 1;
            limit = 15;
        }
        this.itemClickListener = itemClickListener;
        loadView = view.findViewById(R.id.loading_view);
    }

    // 刷新界面
    protected void refresh(){
        if(openLoadMore){
            page = 1;
            if(loadNextView != null && loadNextView.getVisibility() == View.VISIBLE){
                loadNextView.setVisibility(View.GONE);
            }
        }
    }

    // 设置listView的分割线
    protected void setListViweDivider(Drawable divider, int dividerHeight) {
        if (listView != null) {
            listView.setDivider(divider);
            listView.setDividerHeight(dividerHeight);
        }
    }

    // 隐藏加载控件
    protected void hideLoadView() {
        if (loadView.getVisibility() == View.VISIBLE) {
            loadView.setVisibility(View.GONE);
        }
        if(loadNextView != null && loadNextView.getVisibility() == View.VISIBLE){
            loadNextView.setVisibility(View.GONE);
            listView.setFooterLoadState(false);
        }
    }

    // 加载数据完成后调用
    protected void onLoadResult(ArrayList<?> list){
        hideLoadView();
        displayData(list);
    }


    MyBaseAdapter adapter;

    // 显示数据
    protected void displayData(ArrayList list) {
        if (adapter == null) {
            adapter = getMyBaseAdapter(list);
            listView.setAdapter(adapter);
            if(itemClickListener != null){
                listView.setOnItemClickListener(itemClickListener);
            }
        } else {
            if (!openLoadMore || page == 1) {
                adapter.notifyDataSetChanged(list);
            } else {
                ArrayList oldData = adapter.list;
                oldData.addAll(list);
                adapter.notifyDataSetChanged(oldData);
            }
        }
        if(openLoadMore){
            if (list == null || list.size() < limit) { // 不再加载下一页
                listView.setFooterLoadListener(null);
            }else if(page == 1){
                if(footerLoadListener == null){
                    footerLoadListener = new LoadListView.FooterLoadListener(){
                        @Override
                        public void loadMore(LoadListView view) {
                            if(loadNextView == null){
                                loadNextView = ((ViewStub)view.findViewById(R.id.loading_next_page)).inflate();
                            }else{
                                loadNextView.setVisibility(View.VISIBLE);
                            }
                            page++;
                            loadData();
                        }
                    };
                }
                listView.setFooterLoadState(false);
                listView.setFooterLoadListener(footerLoadListener);
            }
        }
        if (adapter.getCount() == 0) {
            if(emptyView == null){
                emptyView = ((ViewStub)view.findViewById(R.id.empty_view)).inflate();
            }else{
                emptyView.setVisibility(View.VISIBLE);
            }
        } else {
            if(emptyView != null && emptyView.getVisibility() == View.VISIBLE){
                emptyView.setVisibility(View.GONE);
            }
        }
    }

    protected abstract MyBaseAdapter getMyBaseAdapter(ArrayList list);

    // 加载数据
    protected abstract void loadData();
}
