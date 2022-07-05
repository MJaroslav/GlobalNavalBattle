package com.github.mjaroslav.globalnavalbattle.client.utils;

import org.lwjgl.glfw.GLFWKeyCallback;
import static org.lwjgl.glfw.GLFW.*;

public abstract class KeyHandler extends GLFWKeyCallback {
    private static final boolean[] PRESSED = new boolean[65536];

    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        if(key < 0 || key > 65535)
            return;
        PRESSED[key] = action == GLFW_REPEAT;
        if(action == GLFW_RELEASE)
            onRelease(key);
    }

    public abstract void onRelease(int key);

    public static boolean isPressed(int key) {
        return PRESSED[key];
    }

    public static boolean shiftPressed() {
        return isPressed(GLFW_KEY_LEFT_SHIFT) || isPressed(GLFW_KEY_RIGHT_SHIFT);
    }

    public static boolean altPressed() {
        return isPressed(GLFW_KEY_LEFT_ALT) || isPressed(GLFW_KEY_RIGHT_ALT);
    }

    public static boolean controlPressed() {
        return isPressed(GLFW_KEY_LEFT_CONTROL) || isPressed(GLFW_KEY_RIGHT_CONTROL);
    }


}
