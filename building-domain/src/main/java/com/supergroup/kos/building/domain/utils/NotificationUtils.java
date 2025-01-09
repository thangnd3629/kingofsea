package com.supergroup.kos.building.domain.utils;

public class NotificationUtils {
    public static Long getGrowthPercent(Long baseStat, Double currentGrowthRate, Double nextGrowthRate) {
        if (baseStat == 0) return 0L;
        return (long) ((nextGrowthRate - currentGrowthRate) * 100 / currentGrowthRate);
    }
}
