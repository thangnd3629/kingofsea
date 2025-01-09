package com.supergroup.core.utils;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomUtil {
    public static <T> T random(Map<T, Float> map) {
        double rand = (double) new Random().nextInt(10000001) / 10000000.0;
        double total = 0.0D;
        //  sort list
        Map<T, Float> sortedMap = map.entrySet().stream()
                                     .sorted(Entry.comparingByValue(Comparator.reverseOrder()))
                                     .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        for (T t : sortedMap.keySet()) {
            double chance = sortedMap.get(t);
            total += chance;

            if (total >= rand) {
                return t;
            }
        }

        return null;
    }

    public static <T> T random(List<T> list) {
        return list.get(new Random().nextInt(list.size()));
    }

    public static Boolean random(Float percent) {
        double rand = (double) new Random().nextInt(10000000) / 10000000.0;
        return (rand < percent);
    }
}
