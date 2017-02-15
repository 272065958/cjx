package com.model.cjx.bean;

/**
 * Created by cjx on 2016/7/29.
 */
public abstract class TreeBean {
    public int hasChild;

    public boolean isChild() {
        return hasChild == 0;
    }

    public abstract String getName();

    public abstract String getId();
}
