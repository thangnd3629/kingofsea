package com.supergroup.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NamedParameterInterpolation {
    private NamedParameterInterpolation() {}
    private static final String wildcard ="xxx";

    public static String format(String template, Map<String, Object> parameters, boolean allowNull) {
        if (Objects.isNull(template)) return null;
        StringBuilder newTemplate = new StringBuilder(template);
        List<Object> valueList = new ArrayList<>();

        Matcher matcher = Pattern.compile("[$][{](\\w+)}").matcher(template);

        while (matcher.find()) {
            String key = matcher.group(1);
            String paramName = "${" + key + "}";
            int index = newTemplate.indexOf(paramName);
            if (index != -1) {
                newTemplate.replace(index, index + paramName.length(), "%s");
                if (Objects.isNull(parameters.get(key))){
                    if (allowNull){
                        valueList.add(wildcard);
                    }
                    else {
                        throw KOSException.of(ErrorCode.NOTIFICATION_NAMED_PARAM_MISSING);
                    }

                }
                valueList.add(parameters.get(key));
            }
        }

        return String.format(newTemplate.toString(), valueList.toArray());
    }
}
