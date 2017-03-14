package com.model.cjx.util;

import com.google.gson.Gson;
import com.model.cjx.bean.ResponseBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

/**
 * Created by cjx on 2016/7/13.
 */
public class JsonParser {
    private static JsonParser instance;
    private Gson gson;
    private JsonParser() {
        gson = new Gson();
    }

    public static JsonParser getInstance() {
        if (instance == null) {
            synchronized (JsonParser.class) {
                if (instance == null) {
                    instance = new JsonParser();
                }
            }
        }
        return instance;
    }

    /**
     * 解析服务器返回数据
     */
    public ResponseBean getDatumResponse(String response){
        if(response == null || response.length() == 0){
            return null;
        }
        ResponseBean rb = null;
        try {
            JSONObject obj = new JSONObject(response);
            rb = new ResponseBean();
            if(obj.has("code")){
                rb.code = obj.getInt("code");
            }
            if(obj.has("message")){
                rb.message = obj.getString("message");
            }
            if(obj.has("datum")){
                rb.datum = obj.getString("datum");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rb;
    }

    public <T> T fromJson(String json, Type typeOfT){
        try{
            return gson.fromJson(json, typeOfT);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将对象转换成字符串
     * @param object 待转换的对象
     * @return
     */
    public String toJson(Object object){
        return gson.toJson(object);
    }
}
