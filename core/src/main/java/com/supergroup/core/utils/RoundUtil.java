package com.supergroup.core.utils;

public class RoundUtil {
    public static Double roundDouble(Double number){
        return (double) Math.floor(number * 100) / 100;
    }

    public static Integer roundDoubleToInteger(Double number) {
        return Math.toIntExact(Math.round(number));
    }
    public static Double roundDouble5Decimal(Double number) {
        return (double) Math.floor(number * 10000) / 10000;
    }
}
