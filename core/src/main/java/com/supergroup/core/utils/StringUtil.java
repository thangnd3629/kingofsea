package com.supergroup.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonParser;

public class StringUtil {
    public static String getSlug(String name) {
        String res = name.toLowerCase(Locale.ENGLISH);
        res = res.replace(" ", "_");
        return res.trim();
    }

    public static String convertHyperLink(String content) {
        return content.replaceAll("(?:https|http)://([\\w/%.\\-?&=!#]+(?!.*\\[/))",
                                  "<a href=\"$0\">$1</a>");
    }

    public static List<Integer> getArrayFromString(String str) {
        List<Integer> rs = new ArrayList<>();
        var points = new ArrayList<Integer>();
        var arr = JsonParser.parseString(str).getAsJsonArray();
        for (var i = 0; i < arr.size(); i++) {
            rs.add(arr.get(i).getAsInt());
        }
        return rs;
    }

    public static List<String> getListStringFromRawStringComma(String str) {
        return Arrays.asList(StringUtils.splitPreserveAllTokens(str, ","));
    }
}
