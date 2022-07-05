package com.github.mjaroslav.globalnavalbattle.client.utils;

import org.lwjgl.glfw.GLFWMouseButtonCallback;

import static org.lwjgl.glfw.GLFW.*;

public abstract class MouseHandler extends GLFWMouseButtonCallback {
    private static boolean leftPressed;
    private static boolean rightPressed;
    private static boolean middlePressed;

    @Override
    public void invoke(long window, int button, int action, int mods) {
        if (button == GLFW_MOUSE_BUTTON_LEFT)
            leftPressed = action == GLFW_PRESS;
        if (button == GLFW_MOUSE_BUTTON_RIGHT)
            rightPressed = action == GLFW_PRESS;
        if (button == GLFW_MOUSE_BUTTON_MIDDLE)
            middlePressed = action == GLFW_PRESS;
        if (!MouseButton.getByKey(button).equals(MouseButton.UNKNOWN) && action == GLFW_RELEASE)
            onRelease(MouseButton.getByKey(button));
    }

    public abstract void onRelease(MouseButton button);

    public static boolean leftPressed() {
        return leftPressed;
    }

    public static boolean rightPressed() {
        return rightPressed;
    }

    public static boolean middlePressed() {
        return middlePressed;
    }
}
