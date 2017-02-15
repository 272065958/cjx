package com.model.cjx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.model.cjx.R;
import com.model.cjx.adapter.MyBaseAdapter;
import com.model.cjx.bean.ResponseBean;
import com.model.cjx.bean.TreeBean;
import com.model.cjx.http.MyCallbackInterface;
import com.model.cjx.util.JsonParser;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by cjx on 2016/8/17.
 * 在intent传入默认布局文件"view", 标题"title", 默认第一个tab的名字"tab_name"
 */
public abstract class BaseTreeActivity extends BaseActivity implements TabLayout.OnTabSelectedListener,
        AdapterView.OnItemClickListener {
    protected SparseArray<ArrayList<?>> treeList;
    protected SparseArray<String> idList;

    protected ListView listView;
    protected View loadView;
    protected View emptyView;
    protected TabLayout tabLayout;
    protected Intent currentIntent;

    protected MyBaseAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentIntent = getIntent();
        setContentView(currentIntent.getIntExtra("view", R.layout.activity_tree_select));
        setToolBar(true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, currentIntent.getStringExtra("title"));
        initView();
    }

    @Override
    public void onBackPressed() {
        if (tabLayout != null && tabLayout.getTabCount() > 1) {
            int position = tabLayout.getTabCount() - 1;
            tabLayout.removeTabAt(position);
            navigationChange(position - 1);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (tab.getTag() != null || adapter == null) {
            return;
        }
        int position = tab.getPosition();
        int count = tabLayout.getTabCount();
        for (int i = count - 1; i > position; i--) {
            tabLayout.removeTabAt(i);
        }
        navigationChange(position);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        Intent intent = getIntent();
        treeList = new SparseArray<>();
        idList = new SparseArray<>();
        loadView = findViewById(R.id.loading_view);
        listView = (ListView) findViewById(R.id.list_view);
        listView.setOnItemClickListener(this);
        emptyView = findViewById(R.id.empty_view);
        listView.setDividerHeight(getResources().getDimensionPixelOffset(R.dimen.divider_height));
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        if (tabLayout != null) {
            tabLayout.setOnTabSelectedListener(this);
            tabLayout.addTab(tabLayout.newTab().setText(intent.getStringExtra("tab_name")));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TreeBean tb = (TreeBean) adapter.getItem(position);
        if (tb.isChild()) {
            returnPosition(tb);
        } else {
            TabLayout.Tab tab = tabLayout.newTab().setText(tb.getName());
            tabLayout.addTab(tab, true);
            loadChildTree(tb.getId());
        }
    }

    // 获取树列表
    protected void loadChildTree(String id) {
        listView.setTag(id); // 设置当前listview要显示的列表标识
        listView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);

        loadList(id);
    }

    // 点击导航栏后更新页面数据
    protected void navigationChange(int position) {
        loadView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        listView.setTag(idList.get(position));
        adapter.notifyDataSetChanged(treeList.get(position));
    }

    // 获取缓存的列表数据
    protected abstract void loadList(String id);

    protected abstract void returnPosition(TreeBean tb);

    protected MyCallbackInterface getMyCallbackInterface(String id, Type type) {
        return new MyLoadCallback(id, type);
    }

    // 获取显示列表的适配器
    protected abstract MyBaseAdapter getMyBaseAdapter(ArrayList<?> list);

    protected void hideLoadView() {
        if (loadView.getVisibility() == View.VISIBLE) {
            loadView.setVisibility(View.GONE);
        }
    }

    // 加载数据完成后调用
    protected void onLoadResult(ArrayList<?> list, String id) {
        hideLoadView();
        displayData(list, id);
    }

    class MyLoadCallback implements MyCallbackInterface {
        String id;
        Type type;

        public MyLoadCallback(String id, Type type) {
            this.id = id;
            this.type = type;
        }

        @Override
        public void success(ResponseBean response) {
            ArrayList<?> list = JsonParser.getInstance().fromJson(response.datum, type);
            onLoadResult(list, id);
        }

        @Override
        public void error() {
            hideLoadView();
        }
    }

    // 显示数据
    protected void displayData(ArrayList<?> list, String id) {
        int position = tabLayout.getTabCount() - 1;
        treeList.append(position, list);
        idList.append(position, id);
        if (adapter == null) {
            adapter = getMyBaseAdapter(list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
        } else {
            adapter.notifyDataSetChanged(list);
        }
        if (adapter.getCount() == 0) {
            findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.empty_view).setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }
}
