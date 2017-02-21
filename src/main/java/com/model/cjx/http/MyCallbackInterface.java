package com.model.cjx.http;


import com.model.cjx.bean.ResponseBean;

/**
 * Created by cjx on 2016/7/18.
 */
public interface MyCallbackInterface {
    Object parser(ResponseBean response);
    void success(Object result);
    void error();
}
