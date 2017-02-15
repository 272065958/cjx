package com.model.cjx.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.model.cjx.R;
import com.model.cjx.adapter.MyBaseAdapter;

import java.util.ArrayList;

/**
 * Created by cjx on 2017/1/25.
 */
public class ItemSelectDialog extends CustomDialog {
    Activity context;
    ListView listView;
    int itemHeight;
    public ItemSelectDialog(Activity context, boolean showCancel) {
        super(context);
        this.context = context;
        itemHeight = context.getResources().getDimensionPixelOffset(R.dimen.item_height);
        ViewGroup view = (ViewGroup) View.inflate(context, R.layout.dialog_item_select, null);
        setContentView(view);
        listView = (ListView) findViewById(R.id.list_view);
        if(showCancel){
            View.inflate(context, R.layout.divider_view, view);
            TextView cancelView = (TextView) View.inflate(context, R.layout.item_select, null);
            cancelView.setText(R.string.button_cancel);
            cancelView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            view.addView(cancelView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight));
        }
    }

    public ItemSelectDialog bindClickListener(AdapterView.OnItemClickListener listener){
        listView.setOnItemClickListener(listener);
        return this;
    }

    public ItemSelectDialog initData(ArrayList<String> list){
        LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) listView.getLayoutParams();
        if(list == null || list.size() < 6){
            llp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        }else{
            llp.height = (int) (5.8f * itemHeight);
        }
        if(listView.getAdapter() == null){
            listView.setAdapter(new SelectAdapter(list, context));
        }else{
            ((SelectAdapter)listView.getAdapter()).notifyDataSetChanged(list);
        }
        return this;
    }

    class SelectAdapter extends MyBaseAdapter{
        public SelectAdapter(ArrayList<?> list, Activity context) {
            super(list, context);
        }

        @Override
        protected View createView(Context context) {
            View view = View.inflate(context, R.layout.item_select, null);
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight);
            view.setLayoutParams(lp);
            return view;
        }

        @Override
        protected MyViewHolder bindViewHolder(View view) {
            return new ViewHolder(view);
        }

        @Override
        protected void bindData(int position, MyViewHolder holder) {
            ViewHolder ho = (ViewHolder) holder;
            String s = (String) getItem(position);
            TextView textView = (TextView) ho.getView();
            textView.setText(s);
        }

        class ViewHolder extends MyViewHolder {
            public ViewHolder(View view) {
                super(view);
            }
        }
    }
}
