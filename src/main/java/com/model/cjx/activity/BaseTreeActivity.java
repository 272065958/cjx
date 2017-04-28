package com.model.cjx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ListView;

import com.model.cjx.R;
import com.model.cjx.adapter.MyBaseAdapter;
import com.model.cjx.bean.TreeBean;
import com.model.cjx.dialog.TipDialog;
import java.util.ArrayList;

/**
 * Created by cjx on 2016/8/17.
 * 在intent传入默认布局文件"view", 标题"title", 默认第一个tab的名字"tab_name"
 */
public abstract class BaseTreeActivity extends BaseActivity implements TabLayout.OnTabSelectedListener,
        AdapterView.OnItemClickListener {
    protected SparseArray<ArrayList> treeList;
    protected SparseArray<String> idList;

    protected ListView listView;
    protected View loadView;
    protected View emptyView;
    protected TabLayout tabLayout;
    protected Intent currentIntent;

    TipDialog exitDialog;
    protected MyBaseAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentIntent = getIntent();
        setContentView(currentIntent.getIntExtra("view", R.layout.activity_tree_select));
        setToolBar(true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tabLayout != null && tabLayout.getTabCount() > 1) {
                    showExitDialog();
                } else {
                    finish();
                }
            }
        }, currentIntent.getStringExtra("title"));
        initView();
    }

    @Override
    public void onBackPressed() {
        if (tabLayout != null && tabLayout.getTabCount() > 1) {
            int position = tabLayout.getTabCount() - 1;
            tabLayout.removeTabAt(position);
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

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    public void setThemeColor(int color){
        tabLayout.setSelectedTabIndicatorColor(color);
        tabLayout.setTabTextColors(tabLayout.getTabTextColors().getDefaultColor(), color);
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
        listView.setDividerHeight(getResources().getDimensionPixelOffset(R.dimen.divider_height));
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        if (tabLayout != null) {
            tabLayout.addOnTabSelectedListener(this);
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
        if (emptyView != null && emptyView.getVisibility() == View.VISIBLE) {
            emptyView.setVisibility(View.GONE);
        }
        loadList(id);
    }

    // 点击导航栏后更新页面数据
    protected void navigationChange(int position) {
        int size = idList.size();
        if (size > position) {
            loadView.setVisibility(View.GONE);
            if (emptyView != null && emptyView.getVisibility() == View.VISIBLE) {
                emptyView.setVisibility(View.GONE);
            }
            listView.setVisibility(View.VISIBLE);
            listView.setTag(idList.get(position));
            adapter.notifyDataSetChanged(treeList.get(position));
            for (int i = position + 1; i < size; i++) {
                idList.delete(i);
                treeList.delete(i);
            }
        }
    }

    // 显示未提交退出提示对话框
    private void showExitDialog() {
        if (exitDialog == null) {
            exitDialog = new TipDialog(this);
            exitDialog.setText(R.string.tip_title, R.string.activity_exit, R.string.button_sure, R.string.button_cancel).setTipComfirmListener(new TipDialog.ComfirmListener() {
                @Override
                public void comfirm() {
                    exitDialog.dismiss();
                    finish();
                }

                @Override
                public void cancel() {
                    exitDialog.dismiss();
                }
            });
        }
        exitDialog.show();
    }

    // 获取缓存的列表数据
    protected abstract void loadList(String id);

    protected abstract void returnPosition(TreeBean tb);

    // 获取显示列表的适配器
    protected abstract MyBaseAdapter getMyBaseAdapter(ArrayList list);

    protected void hideLoadView() {
        if (loadView.getVisibility() == View.VISIBLE) {
            loadView.setVisibility(View.GONE);
        }
    }

    // 加载数据完成后调用
    protected void onLoadResult(ArrayList list, String id) {
        hideLoadView();
        displayData(list, id);
    }

    // 显示数据
    protected void displayData(ArrayList list, String id) {
        int position = tabLayout.getTabCount() - 1;
        treeList.append(position, list);
        idList.append(position, id);
        if (adapter == null) {
            adapter = getMyBaseAdapter(list);
            listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged(list);
        }
        if (adapter.getCount() == 0) {
            if (emptyView == null) {
                ViewStub viewStub = (ViewStub) findViewById(R.id.empty_view);
                if (viewStub != null) {
                    emptyView = viewStub.inflate();
                }
            } else {
                emptyView.setVisibility(View.VISIBLE);
            }
        } else {
            if(emptyView != null && emptyView.getVisibility() == View.VISIBLE){
                emptyView.setVisibility(View.GONE);
            }
            listView.setVisibility(View.VISIBLE);
        }
    }
}
