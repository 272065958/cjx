package com.model.cjx.http;


import com.model.cjx.bean.ResponseBean;

/**
 * Created by cjx on 2016/7/18.
 */
public interface MyCallbackInterface {
    void success(ResponseBean response);
    void error();
}
