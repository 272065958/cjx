package com.model.cjx.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

    public <T> T fromJson(String json, Type typeOfT) {
        try {
            return gson.fromJson(json, typeOfT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> T fromJson(String key, JSONObject jsonObject, Type typeOfT) {
        if (jsonObject.has(key)) {
            String value = null;
            try {
                value = jsonObject.getString(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(value)) {
                return null;
            }
            return gson.fromJson(value, typeOfT);
        } else {
            return null;
        }
    }

    /**
     * 将对象转换成字符串
     *
     * @param object 待转换的对象
     * @return
     */
    public String toJson(Object object) {
        return gson.toJson(object);
    }
}
