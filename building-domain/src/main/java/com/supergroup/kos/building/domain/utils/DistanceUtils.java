package com.supergroup.kos.building.domain.utils;

import com.supergroup.kos.building.domain.model.seamap.Coordinates;

public class DistanceUtils {
    public static Double getDistance(Coordinates source, Coordinates destination) {
        Long sourceX = source.getX();
        Long sourceY = source.getY();
        Long destX = destination.getX();
        Long destY = destination.getY();
        return
                Math.sqrt(Math.pow(Math.abs(sourceX - destX), 2) + Math.pow(Math.abs(sourceY - destY), 2));

    }
}
