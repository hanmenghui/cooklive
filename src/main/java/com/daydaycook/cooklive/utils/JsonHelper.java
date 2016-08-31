package com.daydaycook.cooklive.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 将对象转换为json 以及反转
 * Created by creekhan on 7/7/16.
 */
public final class JsonHelper {

    private static final GsonBuilder builder = new GsonBuilder();
    private static final Gson gson = builder.create();

    private static final JsonParser parser = new JsonParser();

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    public static JsonObject parse(String jsonStr) {
        return parser.parse(jsonStr).getAsJsonObject();
    }

    public static <T> T parese(String jsonStr, Class<T> cls) {
        return gson.fromJson(jsonStr, cls);
    }

}
