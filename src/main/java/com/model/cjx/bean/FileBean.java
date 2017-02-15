package com.model.cjx.bean;
/**
 * Created by cjx on 2016/6/2.
 */
public class FileBean {
    String path;
    String[] childImg;
    int childLength;
    String name;
    String firstimage;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstimage() {
        return firstimage;
    }

    public void setFirstimage(String firstimage) {
        this.firstimage = firstimage;
    }

    public String[] getChildImg() {
        return childImg;
    }

    public void setChildImg(String[] childImg) {
        this.childImg = childImg;
        this.childLength = childImg == null ? 0 : childImg.length;
    }
}
