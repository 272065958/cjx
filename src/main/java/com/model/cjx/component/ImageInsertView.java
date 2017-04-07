package com.model.cjx.component;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.model.cjx.MyApplication;
import com.model.cjx.R;
import com.model.cjx.activity.BaseActivity;
import com.model.cjx.activity.ImagesActivity;
import com.model.cjx.adapter.MyBaseAdapter;

import java.util.ArrayList;

/**
 * Created by cjx on 2016/9/29.
 * 显示选中图片的控件
 */
public class ImageInsertView extends GridView implements AdapterView.OnItemClickListener {
    OnClickListener addImageListener;
    boolean showDel = false;
    ImageAdapter adapter;

    public ImageInsertView(Context context) {
        super(context);
        init();
    }

    public ImageInsertView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            addImageListener.onClick(null);
        } else {
            Intent intent = new Intent(getContext(), ImagesActivity.class);
            intent.putExtra("photo", getImageArray());
            intent.putExtra("page", showDel ? position - 1 : position);
            getContext().startActivity(intent);
        }
    }

    private void init() {
        setOnItemClickListener(this);
    }

    private void initAdapter() {
        ArrayList<String> list = new ArrayList<>();
        if (showDel) {
            list.add(null);
        }
        adapter = new ImageAdapter(list, (BaseActivity) getContext());
        setAdapter(adapter);
    }

    // 调用这个方法后,每增加一个图片,就会自动添加一个点击选择图片的view
    public void setAddImageListener(OnClickListener addImageListener) {
        if (addImageListener != null && adapter == null) {
            showDel = true;
            this.addImageListener = addImageListener;
            initAdapter();
        }
    }

    // 添加一张图片到view
    public void addImage(String path) {
        if (adapter == null) {
            initAdapter();
        }
        adapter.addImage(path);
    }

    public void addImages(ArrayList<String> paths) {
        if (adapter == null) {
            initAdapter();
        }
        adapter.addImages(paths);
    }

    public ArrayList<String> getImages() {
        ArrayList<String> path = new ArrayList<>();
        for(int i=1; i<adapter.count; i++){
            path.add((String)adapter.getItem(i));
        }
        return path;
    }

    public String[] getImageArray() {
        ArrayList<String> paths = adapter.list;
        int length = paths.size() - 1;
        String[] images = new String[length];
        for (int i = 0; i < length; i++) {
            images[i] = paths.get(i + 1);
        }
        return images;
    }

    class ImageAdapter extends MyBaseAdapter {
        int itemSize;

        ImageAdapter(ArrayList list, BaseActivity activity) {
            super(list, activity);
            itemSize = (int) ((MyApplication.getInstance().getScreen_width() -
                    5 * getResources().getDimensionPixelOffset(R.dimen.auto_margin)) / 4f);
        }

        @Override
        protected View createView(Context context) {
            return View.inflate(getContext(), R.layout.select_image_view, null);
        }

        @Override
        protected MyViewHolder bindViewHolder(View view) {
            return new ViewHolder(view);
        }

        @Override
        protected void bindData(int position, MyViewHolder holder) {
            String path = (String) getItem(position);
            ViewHolder ho = (ViewHolder) holder;
            if(showDel){
                if(position == 0){
                    ho.delView.setVisibility(GONE);
                    ho.imageView.setImageResource(R.drawable.add_image_icon);
                }else{
                    Glide.with(getContext()).load(path).into(ho.imageView);
                    ho.delView.setTag(position);
                    ho.delView.setVisibility(VISIBLE);
                }
            }else{
                Glide.with(getContext()).load(path).into(ho.imageView);
            }
        }

        void addImage(String path) {
            list.add(path);
            notifyDataSetChanged(list);
        }

        void addImages(ArrayList<String> paths) {
            list.addAll(paths);
            notifyDataSetChanged(list);
        }

        class ViewHolder extends MyViewHolder implements OnClickListener {
            ImageView imageView, delView;

            ViewHolder(View v) {
                super(v);
                imageView = (ImageView) v.findViewById(R.id.image_content);
                ViewGroup.LayoutParams lp = imageView.getLayoutParams();
                lp.width = itemSize;
                lp.height = itemSize;
                if (showDel) {
                    delView = (ImageView) v.findViewById(R.id.delete_image);
                    delView.setVisibility(VISIBLE);
                    delView.setImageResource(R.drawable.delete_image_icon);
                    delView.setOnClickListener(this);
                }
            }

            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                list.remove(position);
                notifyDataSetChanged(list);
            }
        }
    }
}
