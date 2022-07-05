package com.github.mjaroslav.globalnavalbattle.common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Utils {
    public static Map<String, String> parseKeyValueString(String input, String separatorRegex, String pairSeparatorRegex, boolean throwOnBadPair) {
        Map<String, String> result = new HashMap<>();
        for (String pair : input.split(separatorRegex)) {
            String[] info = pair.split(pairSeparatorRegex);
            try {
                result.put(info[0], info[1]);
            } catch (Exception e) {
                if (throwOnBadPair) {
                    e.printStackTrace();
                    throw e;
                }
            }
        }
        return result;
    }

    public static Map<String, String> parseKeyValueString(String input) {
        return parseKeyValueString(input, " ", "=", false);
    }

    public static boolean stringIsEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static int intInRange(Random rnd, int min, int max) {
        return rnd.nextInt((max + 1) - min) + min;
    }

    public static boolean isPosInRect(double x, double y, double minX, double minY, double maxX, double maxY) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }
}
