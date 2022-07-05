package com.github.mjaroslav.globalnavalbattle.common.utils;

import org.apache.logging.log4j.LogManager;

public class Logger {
    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger("FGLGames");

    public static void info(String message) {
        log.info(message);
    }

    public static void error(String message) {
        log.error(message);
    }

    public static void warn(String message) {
        log.warn(message);
    }

    public static void error(String message, Throwable error) {
        log.error(message, error);
    }

    public static void debug(String message) {
        log.debug(message);
    }
}
