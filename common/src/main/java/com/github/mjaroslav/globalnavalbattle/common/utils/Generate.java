package com.github.mjaroslav.globalnavalbattle.common.utils;

public class Generate {
    public static String username() {
        return String.format("Player%s", System.nanoTime());
    }
}
