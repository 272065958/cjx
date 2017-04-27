package com.model.cjx.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;

import com.model.cjx.R;
import com.model.cjx.adapter.MyBaseAdapter;
import com.model.cjx.component.LoadListView;

import java.util.ArrayList;

/**
 * Created by cjx on 2016/10/28.
 * 列表基类，可以设置是否加载下一页功能
 */
public abstract class BaseListActivity extends BaseActivity {
    protected boolean openLoadMore = true;

    protected LoadListView listView;
    protected View loadView, emptyView, loadNextView;

    LoadListView.FooterLoadListener footerLoadListener;

    int page, limit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreateView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_LOGIN:
                    loadData();
                    break;
            }
        }
    }

    // 初始化界面
    protected void onCreateView(){
        setContentView(R.layout.activity_list_view);
        initListView();
        loadData();
    }

    // 刷新界面
    protected void refresh(){
        if(openLoadMore){
            page = 1;
            hideLoadNextView();
        }
    }

    protected void initListView() {
        loadView = findViewById(R.id.loading_view);
        listView = (LoadListView) findViewById(R.id.list_view);
        if(openLoadMore){
            page = 1;
            limit = 15;
        }
        loadView = findViewById(R.id.loading_view);
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
        hideLoadNextView();
    }

    // 隐藏加载下一页的界面
    private void hideLoadNextView(){
        if(loadNextView != null && loadNextView.getVisibility() == View.VISIBLE){
            loadNextView.setVisibility(View.GONE);
            listView.setFooterLoadState(false);
        }
    }

    // 加载数据完成后调用
    protected void onLoadResult(ArrayList list){
        hideLoadView();
        displayData(list);
    }

    MyBaseAdapter adapter;

    // 显示数据
    protected void displayData(ArrayList list) {
        if (adapter == null) {
            adapter = getMyBaseAdapter(list);
            listView.setAdapter(adapter);
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
                                loadNextView = ((ViewStub)findViewById(R.id.loading_next_page)).inflate();
                            }else{
                                loadNextView.setVisibility(View.VISIBLE);
                            }
                            page++;
                            loadData();
                        }
                    };
                }
                listView.setFooterLoadListener(footerLoadListener);
            }
            listView.setFooterLoadState(false);
        }
        if (adapter.getCount() == 0) {
            if(emptyView == null){
                emptyView = ((ViewStub)findViewById(R.id.empty_view)).inflate();
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
