package com.github.mjaroslav.globalnavalbattle.client.render.gui.element.button;

import com.github.mjaroslav.globalnavalbattle.client.render.GLColor;
import com.github.mjaroslav.globalnavalbattle.client.render.gui.Element;
import com.github.mjaroslav.globalnavalbattle.client.render.gui.Screen;
import com.github.mjaroslav.globalnavalbattle.common.utils.Utils;

import static org.lwjgl.opengl.GL11.*;

public class Button extends Element {
    private final GLColor COLOR;
    private final GLColor HOVERCOLOR;
    private final GLColor FONTCOLOR;
    private final GLColor DCOLOR;
    private String title;
    private int titleSize;

    public Button(Screen parent, int x, int y, int w, int h, String text, int textSize, GLColor color, GLColor hoverColor, GLColor disableColor, GLColor fontColor) {
        super(parent, x, y, w, h);
        title = text;
        titleSize = textSize;
        COLOR = color;
        HOVERCOLOR = hoverColor;
        FONTCOLOR = fontColor;
        DCOLOR = disableColor;
    }

    public Button(Screen parent, int x, int y, int w, int h, String text, int textSize) {
        this(parent, x, y, w, h, text, textSize, GLColor.LIGHT_GRAY, GLColor.GRAY, GLColor.DARK_GRAY, GLColor.BLACK);
    }

    @Override
    protected void renderForeground() {
        drawTitle();
    }

    @Override
    protected void renderBackground() {
        COLOR.bind();
        drawRect();
    }

    @Override
    protected void renderForegroundDisabled() {
        drawTitle();
    }

    @Override
    protected void renderBackgroundDisabled() {
        DCOLOR.bind();
        drawRect();
    }

    @Override
    protected void renderBackgroundHovered(int x, int y) {
        HOVERCOLOR.bind();
        drawRect();
    }

    @Override
    protected void renderForegroundHovered(int x, int y) {
        drawTitle();
    }

    @Override
    protected void renderForegroundSelected() {
        drawTitle();
    }

    @Override
    protected void renderBackgroundSelected() {
        COLOR.bind();
        drawRect();
        PARENT.selectColor().bind();
        drawBorder();
    }

    private void drawTitle() {
        FONTCOLOR.bind();
        if (!Utils.stringIsEmpty(title) && titleSize > 0)
            drawStringCentered(title, titleSize);
    }

    private void drawRect() {
        glBegin(GL_QUADS);
        glVertex2d(X, Y + HEIGHT);
        glVertex2d(X, Y);
        glVertex2d(X + WIDTH, Y);
        glVertex2d(X + WIDTH, Y + HEIGHT);
        glEnd();
    }

    private void drawBorder() {
        PARENT.selectColor().bind();
        glBegin(GL_LINES);
        glVertex2d(X, Y);
        glVertex2d(X + WIDTH, Y);
        glVertex2d(X, Y);
        glVertex2d(X, Y + HEIGHT);
        glVertex2d(X + WIDTH, Y);
        glVertex2d(X + WIDTH, Y + HEIGHT);
        glVertex2d(X, Y + HEIGHT);
        glVertex2d(X + WIDTH, Y + HEIGHT);
        glEnd();
    }
}
