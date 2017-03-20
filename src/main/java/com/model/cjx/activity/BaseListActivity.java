package com.model.cjx.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;

import com.model.cjx.R;
import com.model.cjx.adapter.MyBaseAdapter;
import com.model.cjx.bean.ResponseBean;
import com.model.cjx.component.LoadListView;
import com.model.cjx.http.MyCallbackInterface;
import com.model.cjx.util.JsonParser;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by cjx on 2016/10/28.
 * 列表基类，可以设置是否加载下一页功能
 */
public abstract class BaseListActivity extends BaseActivity {
    protected boolean openLoadMore = true;

    protected LoadListView listView;
    protected View loadView, emptyView, loadNextView;

    AdapterView.OnItemClickListener itemClickListener;
    LoadListView.FooterLoadListener footerLoadListener;

    int page, limit;

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

    // 刷新界面
    protected void refresh(){
        if(openLoadMore){
            page = 1;
            if(loadNextView != null && loadNextView.getVisibility() == View.VISIBLE){
                loadNextView.setVisibility(View.GONE);
            }
        }
    }

    protected void initListView(AdapterView.OnItemClickListener itemClickListener, boolean openLoadMore) {
        loadView = findViewById(R.id.loading_view);
        listView = (LoadListView) findViewById(R.id.list_view);
        this.openLoadMore = openLoadMore;
        if(openLoadMore){
            page = 1;
            limit = 15;
        }
        this.itemClickListener = itemClickListener;
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

    // 获取一个默认的加载数据回调
    protected MyCallbackInterface getMyCallbackInterface(Type type){
        return new BaseCallInterface(type);
    }

    class BaseCallInterface implements MyCallbackInterface {
        Type type;
        public BaseCallInterface(Type type){
            this.type = type;
        }

        @Override
        public Object parser(ResponseBean response) {
            return JsonParser.getInstance().fromJson(response.datum, type);
        }

        @Override
        public void success(Object result) {
            onLoadResult((ArrayList)result);
        }

        @Override
        public void error() {
            hideLoadView();
        }
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
                                loadNextView = ((ViewStub)findViewById(R.id.loading_next_page)).inflate();
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
