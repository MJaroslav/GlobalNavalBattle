package com.github.mjaroslav.globalnavalbattle.client.render.gui.element.button;

import com.github.mjaroslav.globalnavalbattle.client.render.GLColor;
import com.github.mjaroslav.globalnavalbattle.client.utils.KeyHandler;
import com.github.mjaroslav.globalnavalbattle.client.render.gui.Element;
import com.github.mjaroslav.globalnavalbattle.client.render.gui.Screen;
import com.github.mjaroslav.globalnavalbattle.common.utils.Utils;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.opengl.GL11.*;

public class TextField extends Element {
    private final GLColor COLOR;
    private final GLColor HOVERCOLOR;
    private final GLColor FONTCOLOR;
    private final GLColor PLACEHOLDERCOLOR;
    private final GLColor DCOLOR;
    private String placeholder;
    private String value;
    private int titleSize;
    private static final String OUT_OF_RANGE = "..";

    public TextField(Screen parent, int x, int y, int w, int h, String placeholder, String value, int textSize, GLColor color, GLColor hoverColor, GLColor disableColor, GLColor fontColor, GLColor placeholderColor) {
        super(parent, x, y, w, h);
        this.placeholder = placeholder == null ? "" : placeholder;
        titleSize = textSize;
        COLOR = color;
        this.value = value == null ? "" : value;
        HOVERCOLOR = hoverColor;
        FONTCOLOR = fontColor;
        DCOLOR = disableColor;
        PLACEHOLDERCOLOR = placeholderColor;
    }

    public TextField(Screen parent, int x, int y, int w, int h, String placeholder, String value, int textSize) {
        this(parent, x, y, w, h, placeholder, value, textSize, GLColor.LIGHT_GRAY, GLColor.GRAY, GLColor.DARK_GRAY, GLColor.BLACK, GLColor.DARK_GRAY);
    }

    @Override
    protected void renderForeground() {
        drawText();
    }

    @Override
    protected void renderBackground() {
        COLOR.bind();
        drawRect();
    }

    @Override
    protected void renderForegroundDisabled() {
        drawText();
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
        drawText();
    }

    @Override
    protected void renderForegroundSelected() {
        drawText();
    }

    @Override
    protected void renderBackgroundSelected() {
        COLOR.bind();
        drawRect();
        PARENT.selectColor().bind();
        drawBorder();
    }

    private void drawText() {
        if (Utils.stringIsEmpty(value)) {
            PLACEHOLDERCOLOR.bind();
            drawStringCentered(placeholder, titleSize);
        } else {
            FONTCOLOR.bind();
            StringBuilder text = new StringBuilder();
            int pos = value.length() - 1;
            while (pos > -1) {
                if (stringWidth(value.charAt(pos) + text.toString(), titleSize) <= WIDTH - 20 && !(stringWidth(OUT_OF_RANGE + text, titleSize) >= WIDTH - 20))
                    text.insert(0, value.charAt(pos));
                if (stringWidth(OUT_OF_RANGE + text, titleSize) >= WIDTH - 20)
                    break;
                pos--;
            }
            if (pos > 0)
                text.insert(0, OUT_OF_RANGE);
            drawString(text.toString(), 10, (HEIGHT - titleSize) / 2, titleSize);
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

    @Override
    public void keyRelease(int key, int uChar) {
        if (key == GLFW.GLFW_KEY_BACKSPACE || key == GLFW.GLFW_KEY_DELETE) {
            removeChar();
        } else if (uChar > -1 && Character.getType(uChar) != Character.MODIFIER_LETTER)
            value += (char) uChar;
        else
            super.keyRelease(key, uChar);
    }

    private void removeChar() {
        if (value.length() > 0)
            value = value.substring(0, value.length() - 1);
    }

    private int ticks = 0;

    public String getValue() {
        return value.isEmpty() ? placeholder : value;
    }

    @Override
    public void update(boolean activated) {
        if (activated)
            if (KeyHandler.isPressed(GLFW.GLFW_KEY_DELETE) || KeyHandler.isPressed(GLFW.GLFW_KEY_BACKSPACE)) {
                if (ticks > 19 || ticks == 0 || ticks == 5 || ticks == 10 || ticks == 15)
                    removeChar();
                ticks++;
            } else ticks = 0;
        else ticks = 0;
    }

    @Override
    public void unselected() {
        PARENT.buttonUpdated(PARENT.getActivatedElement());
    }
}
