package com.model.cjx.fragment;

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
 * Created by cjx on 2017/2/20.
 */
public abstract class BaseListFragment extends BaseFragment {
    protected LoadListView listView;
    protected View loadView, emptyView, loadNextView;

    AdapterView.OnItemClickListener itemClickListener;

    int page = 1;

    protected void initListView(AdapterView.OnItemClickListener itemClickListener, boolean loadnext) {
        loadView = view.findViewById(R.id.loading_view);
        listView = (LoadListView) view.findViewById(R.id.list_view);
        if(loadnext){
            listView.setFooterLoadListener(new LoadListView.FooterLoadListener() {
                @Override
                public void loadMore() {
                    if (loadNextView == null) {
                        ViewStub stub = (ViewStub) view.findViewById(R.id.loading_next_page);
                        loadNextView = stub.inflate();
                    } else {
                        loadNextView.setVisibility(View.VISIBLE);
                    }
                    page++;
                    loadData();
                }
            });
        }
        this.itemClickListener = itemClickListener;
        loadView = view.findViewById(R.id.loading_view);
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

    // 获取一个默认的加载数据回调
    protected MyCallbackInterface getMyCallbackInterface(Type type){
        return new BaseCallInterface(type);
    }

    class BaseCallInterface implements MyCallbackInterface {
        Type type;
        BaseCallInterface(Type type){
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
            adapter.notifyDataSetChanged(list);
        }
        if (adapter.getCount() == 0) {
            if(emptyView == null){
                emptyView = ((ViewStub)view.findViewById(R.id.empty_view)).inflate();
            }
            emptyView.setVisibility(View.VISIBLE);
        } else {
            if(emptyView != null){
                emptyView.setVisibility(View.GONE);
            }
        }
    }

    protected abstract MyBaseAdapter getMyBaseAdapter(ArrayList list);

    // 加载数据
    protected abstract void loadData();
}
