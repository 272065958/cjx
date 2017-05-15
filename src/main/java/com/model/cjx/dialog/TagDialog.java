package com.model.cjx.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.SparseArray;

import com.model.cjx.R;

/**
 * Created by cjx on 2016/8/3.
 */
class TagDialog extends Dialog {

    private SparseArray<Object> tags;

    TagDialog(Context context) {
        super(context, R.style.loading_dialog);
    }

    private Object tag;

    /**
     * 设置一个标签
     */
    public void setTag(Object tag) {
        this.tag = tag;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(int key, Object tag) {
        if (tags == null) {
            tags = new SparseArray<>();
        }
        tags.put(key, tag);
    }

    public Object getTag(int key) {
        if (tags != null) {
            return tags.get(key);
        }
        return null;
    }
}
