package com.github.mjaroslav.globalnavalbattle.client.render.gui.element.button;

import com.github.mjaroslav.globalnavalbattle.client.render.GLColor;
import com.github.mjaroslav.globalnavalbattle.client.render.GLTexture;
import com.github.mjaroslav.globalnavalbattle.client.render.gui.Element;
import com.github.mjaroslav.globalnavalbattle.client.render.gui.Screen;

import static org.lwjgl.opengl.GL11.*;

public class IconButton extends Element {
    private final GLColor COLOR;
    private final GLColor HOVERCOLOR;
    private final GLColor ICONCOLOR;
    private final GLColor DCOLOR;
    private final GLTexture ICON;

    public IconButton(Screen parent, int x, int y, int w, int h, GLTexture icon, GLColor color, GLColor hoverColor, GLColor disableColor, GLColor iconColor) {
        super(parent, x, y, w < 14 ? 14 : w, h < 14 ? 14 : h);
        COLOR = color;
        HOVERCOLOR = hoverColor;
        ICONCOLOR = iconColor;
        ICON = icon;
        DCOLOR = disableColor;
    }

    public IconButton(Screen parent, int x, int y, int w, int h, GLTexture icon) {
        this(parent, x, y, w, h, icon, GLColor.LIGHT_GRAY, GLColor.GRAY, GLColor.DARK_GRAY, GLColor.BLACK);
    }

    @Override
    protected void renderForeground() {
        drawIcon();
    }

    @Override
    protected void renderBackground() {
        COLOR.bind();
        drawRect();
    }

    @Override
    protected void renderForegroundDisabled() {
        drawIcon();
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
        drawIcon();
    }

    @Override
    protected void renderForegroundSelected() {
        drawIcon();
    }

    @Override
    protected void renderBackgroundSelected() {
        COLOR.bind();
        drawRect();
        PARENT.selectColor().bind();
        drawBorder();
    }

    private void drawIcon() {
        if (ICON != null) {
            ICONCOLOR.bind();
            ICON.bind();
            glBegin(GL_QUADS);
            glTexCoord2d(0, 1);
            glVertex2d(X + 2, Y + HEIGHT - 2);
            glTexCoord2d(0, 0);
            glVertex2d(X + 2, Y + 2);
            glTexCoord2d(1, 0);
            glVertex2d(X + WIDTH - 2, Y + 2);
            glTexCoord2d(1, 1);
            glVertex2d(X + WIDTH - 2, Y + HEIGHT - 2);
            glEnd();
        }
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
