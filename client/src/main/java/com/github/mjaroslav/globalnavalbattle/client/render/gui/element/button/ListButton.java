package com.github.mjaroslav.globalnavalbattle.client.render.gui.element.button;

import com.github.mjaroslav.globalnavalbattle.client.render.GLColor;
import com.github.mjaroslav.globalnavalbattle.client.render.gui.Element;
import com.github.mjaroslav.globalnavalbattle.client.render.gui.Screen;
import com.github.mjaroslav.globalnavalbattle.common.utils.Utils;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.opengl.GL11.*;

public class ListButton<T> extends Element {
    private final GLColor COLOR;
    private final GLColor HOVERCOLOR;
    private final GLColor FONTCOLOR;
    private final GLColor DCOLOR;
    private String title;
    private int titleSize;
    private int activatedSubbutton = -1;
    private final T[] LIST;
    private int current;
    private static final String PREV = "◄";
    private static final String NEXT = "►";

    public ListButton(Screen parent, int x, int y, int w, int h, T[] values, int currentValue, String text, int textSize, GLColor color, GLColor hoverColor, GLColor disableColor, GLColor fontColor) {
        super(parent, x, y, w < textSize * 2 ? textSize * 2 : w, h);
        title = text;
        titleSize = textSize;
        COLOR = color;
        HOVERCOLOR = hoverColor;
        FONTCOLOR = fontColor;
        DCOLOR = disableColor;
        LIST = values;
        current = currentValue;
    }

    public ListButton(Screen parent, int x, int y, int w, int h, T[] values, int currentValue, String text, int textSize) {
        this(parent, x, y, w, h, values, currentValue, text, textSize, GLColor.LIGHT_GRAY, GLColor.GRAY, GLColor.DARK_GRAY, GLColor.BLACK);
    }

    public T[] getList() {
        return LIST;
    }

    public T getValue() {
        return current != -1 ? LIST[current] : null;
    }

    public int getCurrent() {
        return current;
    }

    private void next() {
        current++;
        if (current >= LIST.length)
            current = 0;
    }

    private void prev() {
        current--;
        if (current < 0)
            current = 0;
    }

    @Override
    protected void renderForeground() {
        drawButtonText();
    }

    @Override
    protected void renderBackground() {
        COLOR.bind();
        drawLeftRect();
        drawCenterRect();
        drawRightRect();
    }

    @Override
    protected void renderForegroundDisabled() {
        drawButtonText();
    }

    @Override
    protected void renderBackgroundDisabled() {
        DCOLOR.bind();
        drawLeftRect();
        drawCenterRect();
        drawRightRect();
    }

    @Override
    protected void renderBackgroundHovered(int x, int y) {
        COLOR.bind();
        drawCenterRect();
        if (Utils.isPosInRect(x, y, 0, 0, titleSize * 2, HEIGHT))
            HOVERCOLOR.bind();
        else COLOR.bind();
        drawLeftRect();
        if (Utils.isPosInRect(x, y, WIDTH - titleSize * 2, 0, WIDTH, HEIGHT))
            HOVERCOLOR.bind();
        else COLOR.bind();
        drawRightRect();
    }

    @Override
    protected void renderForegroundHovered(int x, int y) {
        drawButtonText();
    }

    @Override
    protected void renderForegroundSelected() {
        drawButtonText();
    }

    @Override
    protected void renderBackgroundSelected() {
        COLOR.bind();
        drawCenterRect();
        drawLeftRect();
        drawRightRect();
        PARENT.selectColor().bind();
        if (activatedSubbutton == 0)
            drawLeftBorder();
        else if (activatedSubbutton == 1)
            drawRightBorder();
        else drawBorder();
    }

    @Override
    public void unselected() {
        activatedSubbutton = -1;
    }

    @Override
    public void keyRelease(int key, int uChar) {
        if (key == GLFW.GLFW_KEY_ESCAPE && activatedSubbutton != -1) {
            activatedSubbutton = -1;
            return;
        }
        if (key == GLFW.GLFW_KEY_LEFT && activatedSubbutton != 0) {
            activatedSubbutton = 0;
            return;
        } else if (key == GLFW.GLFW_KEY_RIGHT && activatedSubbutton != 1) {
            activatedSubbutton = 1;
            return;
        }
        if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
            if (activatedSubbutton == 0)
                prev();
            else if (activatedSubbutton == 1)
                next();
            PARENT.buttonUpdated(PARENT.getActivatedElement());
        }
        super.keyRelease(key, uChar);
    }

    public void activate(int x, int y) {
        if (Utils.isPosInRect(x, y, 0, 0, titleSize * 2, HEIGHT))
            prev();
        if (Utils.isPosInRect(x, y, WIDTH - titleSize * 2, 0, WIDTH, HEIGHT))
            next();
    }

    private String title() {
        return String.format(title, getValue());
    }

    private void drawButtonText() {
        FONTCOLOR.bind();
        if (!Utils.stringIsEmpty(title()) && titleSize > 0)
            drawStringCentered(title(), titleSize);
        drawStringCentered(PREV, titleSize, HEIGHT / 2, titleSize);
        drawStringCentered(NEXT, WIDTH - titleSize, HEIGHT / 2, titleSize);
    }

    private void drawCenterRect() {
        glBegin(GL_QUADS);
        glVertex2d(X + titleSize * 2, Y + HEIGHT);
        glVertex2d(X + titleSize * 2, Y);
        glVertex2d(X + WIDTH - titleSize * 2, Y);
        glVertex2d(X + WIDTH - titleSize * 2, Y + HEIGHT);
        glEnd();
    }

    private void drawLeftRect() {
        glBegin(GL_QUADS);
        glVertex2d(X, Y + HEIGHT);
        glVertex2d(X, Y);
        glVertex2d(X + titleSize * 2, Y);
        glVertex2d(X + titleSize * 2, Y + HEIGHT);
        glEnd();
    }

    private void drawRightRect() {
        glBegin(GL_QUADS);
        glVertex2d(X + WIDTH - titleSize * 2, Y + HEIGHT);
        glVertex2d(X + WIDTH - titleSize * 2, Y);
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

    private void drawLeftBorder() {
        PARENT.selectColor().bind();
        glBegin(GL_LINES);
        glVertex2d(X, Y);
        glVertex2d(X + titleSize * 2, Y);
        glVertex2d(X, Y);
        glVertex2d(X, Y + HEIGHT);
        glVertex2d(X + titleSize * 2, Y);
        glVertex2d(X + titleSize * 2, Y + HEIGHT);
        glVertex2d(X, Y + HEIGHT);
        glVertex2d(X + titleSize * 2, Y + HEIGHT);
        glEnd();
    }

    private void drawRightBorder() {
        PARENT.selectColor().bind();
        glBegin(GL_LINES);
        glVertex2d(X + WIDTH - titleSize * 2, Y);
        glVertex2d(X + WIDTH, Y);
        glVertex2d(X + WIDTH - titleSize * 2, Y);
        glVertex2d(X + WIDTH - titleSize * 2, Y + HEIGHT);
        glVertex2d(X + WIDTH, Y);
        glVertex2d(X + WIDTH, Y + HEIGHT);
        glVertex2d(X + WIDTH - titleSize * 2, Y + HEIGHT);
        glVertex2d(X + WIDTH, Y + HEIGHT);
        glEnd();
    }
}
