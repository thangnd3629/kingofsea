package com.supergroup.core.utils;

import com.google.gson.GsonBuilder;

public class JsonUtils {

    public static String objectToJson(Object object) {
        var gson = new GsonBuilder().create();
        return gson.toJson(object);
    }
}
