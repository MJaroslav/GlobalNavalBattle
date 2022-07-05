package com.github.mjaroslav.globalnavalbattle.client.render.gui;

import com.github.mjaroslav.globalnavalbattle.client.GlobalNavalBattle;
import com.github.mjaroslav.globalnavalbattle.client.render.GLColor;
import com.github.mjaroslav.globalnavalbattle.client.utils.KeyHandler;
import com.github.mjaroslav.globalnavalbattle.client.utils.MouseButton;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public abstract class Screen {
    public final GlobalNavalBattle GNB = GlobalNavalBattle.getGlobalNavalBattle();

    protected final List<Element> elements = new ArrayList<>();
    protected int hoveredElement = -1;
    protected int activatedElement = -1;

    public void initGui() {
    }

    protected int x = 0;
    protected int y = 0;

    public final int width() {
        return GNB.GUI_WIDTH;
    }

    public final int height() {
        return GNB.GUI_HEIGHT;
    }

    public void elementClicked(int id, Element element, int elemX, int elemY) {
        buttonUpdated(id);
    }

    public void buttonUpdated(int id) {
    }

    public GLColor selectColor() {
        return GLColor.BLUE;
    }

    public final void render() {
        GL11.glPushMatrix();
        GLColor.WHITE.bind();
        GL11.glScaled(GNB.getDelta(), GNB.getDelta(), 1);
        GL11.glPushMatrix();
        GL11.glPushMatrix();
        GLColor.WHITE.bind();
        GL11.glTranslated(0, 0, -1);
        renderBackground();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(0, 0, 0.5);
        renderElements();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GLColor.WHITE.bind();
        renderForeground();
        GL11.glPopMatrix();
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }

    public final void onMouseMoved(int x, int y) {
        this.x = x;
        this.y = y;
        hoveredElement = -1;
        for (int id = 0; id < elements.size(); id++)
            if (elements.get(id).isHovered(x, y))
                hoveredElement = id;
    }

    public final void onMouseClicked(int x, int y, MouseButton button) {
        onClicked((int) (x / GNB.getDelta()), (int) (y / GNB.getDelta()), button);
    }

    protected void onClicked(int x, int y, MouseButton button) {
        if (button.equals(MouseButton.LEFT))
            if (hoveredElement != -1) {
                Element element = elements.get(hoveredElement);
                if (!element.isDisabled()) {
                    activatedElement = hoveredElement;
                    elementClicked(hoveredElement, element, x - element.X, y - element.Y);
                }
            } else if (activatedElement != -1) {
                elements.get(activatedElement).unselected();
                activatedElement = -1;
            }
    }

    public final void update() {
        updateScreen();
        for (int id = 0; id < elements.size(); id++)
            elements.get(id).update(activatedElement == id);
    }

    public void updateScreen() {
    }

    public int getActivatedElement() {
        return activatedElement;
    }

    public boolean isElementActivated(int id) {
        return activatedElement == id;
    }

    public void activateElement(int id) {
        if (id >= 0 && id < elements.size())
            activatedElement = id;
        else deactivateElements();
    }

    public void nextElement() {
        if (elements.size() == 0) {
            activatedElement = -1;
            return;
        }
        if (activatedElement != -1)
            elements.get(activatedElement).unselected();
        if (activatedElement == -1)
            activatedElement = 0;
        else {
            activatedElement++;
            if (activatedElement >= elements.size())
                activatedElement = 0;
        }
    }

    public void prevElement() {
        if (elements.size() == 0) {
            activatedElement = -1;
            return;
        }
        if (activatedElement != -1)
            elements.get(activatedElement).unselected();
        if (activatedElement == -1)
            activatedElement = 0;
        else {
            activatedElement--;
            if (activatedElement < 0)
                activatedElement = elements.size() - 1;
        }
    }

    public void deactivateElements() {
        activatedElement = -1;
    }

    protected final void renderElements() {
        for (int id = 0; id < elements.size(); id++) {
            Element element = elements.get(id);
            if (element.isDisabled())
                element.renderDisabled();
            else if (id == hoveredElement)
                element.renderHovered(x - element.X, y - element.Y);
            else if (id == activatedElement)
                element.renderSelected();
            else
                element.render();
        }
    }

    protected abstract void renderBackground();

    protected abstract void renderForeground();

    public final void drawString(String text, double x, double y, double size) {
        drawString(text, x, y, size, false, false);
    }

    public final void drawStringCentered(String text, double x, double y, double size) {
        drawString(text, x, y, size, true, true);
    }

    public final double stringWidth(String text, double size) {
        return GNB.FONT.getWidth(text, size);
    }

    public final double stringHeight(String text, double size) {
        return GNB.FONT.getHeight(text, size);
    }

    public final void drawString(String text, double x, double y, double size, boolean centeredX, boolean centeredY) {
        double xx;
        double yy = y;
        if(centeredX) {
            xx = x - GNB.FONT.getWidth(text, size) / 2d;
            if (centeredY)
                yy -= GNB.FONT.getHeight(text, size) / 2d;
            for(String line : text.split("\n")) {
                xx = x - GNB.FONT.getWidth(line, size) / 2d;
                GNB.FONT.drawString(line, xx, yy, size);
                yy += size;
            }
        } else {
            xx = x;
            if (centeredY)
                yy -= GNB.FONT.getHeight(text, size) / 2d;
            GNB.FONT.drawString(text, xx, yy, size);
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public final void onKeyRelease(int key, int uChar) {
        if (elements.size() > 0)
            if (key == GLFW.GLFW_KEY_TAB)
                if (activatedElement == -1)
                    activatedElement = 0;
                else if (KeyHandler.shiftPressed())
                    prevElement();
                else nextElement();
            else if (key == GLFW.GLFW_KEY_LEFT && activatedElement == -1)
                activatedElement = 0;
            else if (key == GLFW.GLFW_KEY_RIGHT && activatedElement == -1)
                activatedElement = elements.size() - 1;
        if (activatedElement != -1)
            elements.get(activatedElement).keyRelease(key, uChar);
        else keyRelease(key, uChar);
    }

    protected void keyRelease(int key, int uChar) {
    }

    public boolean canUse() {
        return true;
    }
}
