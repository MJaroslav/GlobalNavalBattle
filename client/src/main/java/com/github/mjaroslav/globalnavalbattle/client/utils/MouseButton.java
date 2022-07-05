package com.github.mjaroslav.globalnavalbattle.client.utils;

import static org.lwjgl.glfw.GLFW.*;

public enum MouseButton {
    LEFT(GLFW_MOUSE_BUTTON_LEFT), RIGHT(GLFW_MOUSE_BUTTON_RIGHT), MIDDLE(GLFW_MOUSE_BUTTON_MIDDLE), UNKNOWN(-1);

    public final int KEY;

    MouseButton(int key) {
        KEY = key;
    }

    public static MouseButton getByKey(int key) {
        for (MouseButton button : values())
            if (button.KEY == key)
                return button;
        return UNKNOWN;
    }
}