package com.model.cjx.util;

import com.google.gson.Gson;
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
