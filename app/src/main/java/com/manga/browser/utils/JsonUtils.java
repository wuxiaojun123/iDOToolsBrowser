package com.manga.browser.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuxiaojun on 16-12-12.
 */

public class JsonUtils {

    /**
     * 将返回结果转化成对象
     *
     * @param s
     * @param t
     * @return
     */
    public static <T> Object toObject(String s, Class<T> t) {
        if (s != null) {
            Gson gson = new Gson();
            return gson.fromJson(s, t);
        }
        return null;
    }

    /**
     * 返回集合对象
     *
     * @return
     */
    public static <T> List<T> fromJsonArray(String json, Class<T> clazz) throws Exception {
        List<T> lst = new ArrayList<T>();
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        for (final JsonElement elem : array) {
            lst.add(new Gson().fromJson(elem, clazz));
        }
        return lst;
    }

    /***
     * 返回对象
     * @param list
     * @return
     */
    public static <T> String toJsonFromList(List<T> list) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<T>>() {
        }.getType(); // 指定集合对象属性
        String json = gson.toJson(list, type);
        return json;
    }

}
