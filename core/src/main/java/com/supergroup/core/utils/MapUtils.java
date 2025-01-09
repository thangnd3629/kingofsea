package com.supergroup.core.utils;

import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;

public class MapUtils {

    public static String toString(Map<?, ?> map) {
        return Joiner.on(",").withKeyValueSeparator("=").join(map);
    }

    public static Map<String, String> toMap(String data) {
        if (Strings.isNullOrEmpty(data)) {
            return Map.of();
        }
        return Splitter.on(',').withKeyValueSeparator('=').split(data);
    }
}
