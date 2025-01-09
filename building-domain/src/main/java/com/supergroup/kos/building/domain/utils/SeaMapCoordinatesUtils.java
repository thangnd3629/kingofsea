package com.supergroup.kos.building.domain.utils;

import java.util.ArrayList;
import java.util.List;

import com.supergroup.kos.building.domain.model.seamap.Coordinates;

import lombok.AllArgsConstructor;

public class SeaMapCoordinatesUtils {
    public static String toStringCoordinates(Long x, Long y) {
        return "(" + x + ";" + y + ")";
    }

    public static Coordinates stringToCoordinates(String s) {
        List<String> list = List.of(s.substring(1, s.length() - 1).split("\\;"));
        return new Coordinates(Long.valueOf(list.get(0)), Long.valueOf(list.get(1)));
    }

    public static String toStringCoordinatesWithStep(String s, Long stepX, Long stepY) {
        List<String> list = List.of(s.substring(1, s.length() - 1).split("\\;"));
        return toStringCoordinates(Long.parseLong(list.get(0)) + stepX, Long.parseLong(list.get(1)) + stepY);
    }

    public static List<Coordinates> stringToListCoordinates(String s) {
        List<String> list = List.of(s.substring(1, s.length() - 1).replace(" ", "").split("\\,"));
        List<Coordinates> response = new ArrayList<>();
        for (String s1 : list) {
            response.add(stringToCoordinates(s1));
        }
        return response;
    }

    public static Coordinates getCurrentLocation(Coordinates start, Coordinates end, Double speed, Long elapsedTime) {
        double bearAngle = angleBetweenVector(new Vector(1., 0.), new Vector(
                (double) (end.getX() - start.getX()), (double) (end.getY() - start.getY())));
        Double currentX = start.getX() + elapsedTime * speed * Math.cos(bearAngle);
        Double currentY = start.getY() + elapsedTime * speed * Math.sin(bearAngle);
        return new Coordinates(currentX.longValue(), currentY.longValue());
    }

    private static Double angleBetweenVector(Vector a, Vector b) { // from a to b, range 2Pi
        Double angle = Math.atan2(Cross(a, b), Dot(a, b));
        return angle;

    }

    private static Double Dot(Vector A, Vector B) {
        return A.x * B.x + A.y * B.y;
    }

    private static Double Cross(Vector A, Vector B) {
        return A.x * B.y - A.y * B.x;

    }

    @AllArgsConstructor
    public static class Vector {
        public Double x;
        public Double y;
    }


}
