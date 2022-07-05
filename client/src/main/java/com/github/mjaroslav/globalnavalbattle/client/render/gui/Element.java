package com.github.mjaroslav.globalnavalbattle.client.render.gui;

import com.github.mjaroslav.globalnavalbattle.client.render.GLColor;
import com.github.mjaroslav.globalnavalbattle.common.utils.Utils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public abstract class Element {
    public final Screen PARENT;
    public final int X;
    public final int Y;
    public final int WIDTH;
    public final int HEIGHT;
    private boolean disabled;

    public Element(Screen parent, int x, int y, int w, int h) {
        PARENT = parent;
        X = x;
        Y = y;
        WIDTH = w;
        HEIGHT = h;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public final void render() {
        GL11.glPushMatrix();
        GLColor.WHITE.bind();
        renderBackground();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GLColor.WHITE.bind();
        GL11.glTranslated(0, 0, 0.5);
        renderForeground();
        GL11.glPopMatrix();
    }

    public final void renderHovered(int x, int y) {
        GL11.glPushMatrix();
        GLColor.WHITE.bind();
        renderBackgroundHovered(x, y);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GLColor.WHITE.bind();
        GL11.glTranslated(0, 0, 0.5);
        renderForegroundHovered(x, y);
        GL11.glPopMatrix();
    }

    public final void renderSelected() {
        GL11.glPushMatrix();
        GLColor.WHITE.bind();
        renderBackgroundSelected();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GLColor.WHITE.bind();
        GL11.glTranslated(0, 0, 0.5);
        renderForegroundSelected();
        GL11.glPopMatrix();
    }

    public final void renderDisabled() {
        GL11.glPushMatrix();
        GLColor.WHITE.bind();
        renderBackgroundDisabled();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GLColor.WHITE.bind();
        GL11.glTranslated(0, 0, 0.5);
        renderForegroundDisabled();
        GL11.glPopMatrix();
    }

    protected abstract void renderForeground();

    protected abstract void renderBackground();

    protected abstract void renderForegroundDisabled();

    protected abstract void renderBackgroundDisabled();

    protected abstract void renderBackgroundHovered(int x, int y);

    protected abstract void renderForegroundHovered(int x, int y);

    protected abstract void renderForegroundSelected();

    protected abstract void renderBackgroundSelected();

    public boolean isHovered(int x, int y) {
        return Utils.isPosInRect(x, y, X, Y, X + WIDTH, Y + HEIGHT);
    }

    public void drawString(String text, int x, int y, int size) {
        PARENT.drawString(text, X + x, Y + y, size);
    }

    public void drawStringCentered(String text, int x, int y, int size) {
        PARENT.drawStringCentered(text, X+ x, Y + y, size);
    }

    public void drawStringCentered(String text, int size) {
        PARENT.drawStringCentered(text, X + WIDTH / 2, Y + HEIGHT / 2, size);
    }

    public void keyRelease(int key, int uChar) {
        if (key == GLFW.GLFW_KEY_ESCAPE)
            PARENT.deactivateElements();
        if(key == GLFW.GLFW_KEY_LEFT)
            PARENT.prevElement();
        if(key == GLFW.GLFW_KEY_RIGHT)
            PARENT.nextElement();
        if(key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER)
            PARENT.buttonUpdated(PARENT.getActivatedElement());
    }

    public final double stringWidth(String text, double size) {
        return PARENT.stringWidth(text, size);
    }

    public final double stringHeight(String text, double size) {
        return PARENT.stringHeight(text, size);
    }

    public void unselected() {}

    public void update(boolean activated) {}
}
