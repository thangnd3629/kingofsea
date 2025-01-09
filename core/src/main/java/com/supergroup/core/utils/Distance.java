package com.supergroup.core.utils;

public class Distance {
    public static Double calculateDistance(Long x1, Long y1, Long x2 , Long y2) {
        return Math.sqrt((double) ((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)));
    }
}
