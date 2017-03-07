package com.xdja.usbdemo.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

public class JSONUtil {

    public static String getJSONStr(Object obj) {
        if (obj != null) {
            Gson gson = new Gson();
            String result = gson.toJson(obj);
            return result;
        }
        return null;
    }

    public static Object getObjectFromJson(String json, Class<?> clazz){
        return new GsonBuilder().create().fromJson(json, clazz);
    }

    public static Object getObjectFromJson(String json, Type type){
        return new GsonBuilder().create().fromJson(json, type);
    }
}
