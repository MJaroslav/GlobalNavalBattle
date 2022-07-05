package com.github.mjaroslav.globalnavalbattle.common.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JsonUtils {
    private static final Gson GSON = new GsonBuilder().create();
    private static final Gson GSON_PRETTY = new GsonBuilder().setPrettyPrinting().create();
    private static final JsonParser PARSER = new JsonParser();

    public static JsonElement toJsonElement(String string) {
        return PARSER.parse(string);
    }

    public static JsonElement toJsonElement(Object value) {
        return PARSER.parse(toString(value, false));
    }

    public static String toString(JsonElement json, boolean pretty) {
        return pretty ? GSON_PRETTY.toJson(json) : GSON.toJson(json);
    }

    public static String toString(Object value, boolean pretty) {
        return pretty ? GSON_PRETTY.toJson(value) : GSON.toJson(value);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    public static <T> T fromJson(JsonElement json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }
}
