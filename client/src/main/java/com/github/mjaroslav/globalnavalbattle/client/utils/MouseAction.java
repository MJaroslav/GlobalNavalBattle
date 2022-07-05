package com.github.mjaroslav.globalnavalbattle.client.utils;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public enum MouseAction {
    RELEASE(GLFW_RELEASE), PRESSED(GLFW_PRESS), UNKNOWN(-1);

    public final int KEY;

    MouseAction(int key) {
        KEY = key;
    }

    public static MouseAction getByKey(int key) {
        for (MouseAction action : values())
            if (action.KEY == key)
                return action;
        return UNKNOWN;
    }
}