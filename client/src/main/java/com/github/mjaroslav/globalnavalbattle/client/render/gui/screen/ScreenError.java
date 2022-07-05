package com.github.mjaroslav.globalnavalbattle.client.render.gui.screen;

import com.github.mjaroslav.globalnavalbattle.client.render.GLColor;
import com.github.mjaroslav.globalnavalbattle.client.render.GLTexture;
import com.github.mjaroslav.globalnavalbattle.client.render.gui.element.button.Button;
import com.github.mjaroslav.globalnavalbattle.client.resource.ResourceLoader;
import com.github.mjaroslav.globalnavalbattle.client.render.gui.Screen;
import com.github.mjaroslav.globalnavalbattle.common.utils.Utils;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.opengl.GL11.*;

public class ScreenError extends Screen {
    private GLTexture bg;
    private static final GLColor BLACK8 = GLColor.BLACK.copy().setAlpha(0.8);
    private static final GLColor LIGHT_GRAY4 = GLColor.LIGHT_GRAY.copy().setAlpha(0.4);
    private static final GLColor GRAY4 = GLColor.GRAY.copy().setAlpha(0.4);
    private static final GLColor DARK_GRAY4 = GLColor.DARK_GRAY.copy().setAlpha(0.4);
    private String title;

    public ScreenError(String message) {
        title = message;
    }

    public ScreenError(String message, Throwable e) {
        title = Utils.stringIsEmpty(message) ? e.toString() : message + "\n" + e.toString();
    }

    @Override
    public void initGui() {
        bg = ResourceLoader.getOrLoadTexture(ResourceLoader.getTexturePath("main_background.png"));
        elements.add(new Button(this, width() / 2 - 125, height() / 2 + 10, 250, 40,
                "В главное меню", 20, LIGHT_GRAY4, GRAY4, DARK_GRAY4, GLColor.WHITE));
    }

    @Override
    public void buttonUpdated(int id) {
        GNB.setScreen(new ScreenLogin());
    }

    @Override
    protected void renderBackground() {
        if (bg != null)
            bg.bind();
        glBegin(GL_QUADS);
        glTexCoord2d(0, 1);
        glVertex2d(0, height());
        glTexCoord2d(0, 0);
        glVertex2d(0, 0);
        glTexCoord2d(1, 0);
        glVertex2d(width(), 0);
        glTexCoord2d(1, 1);
        glVertex2d(width(), height());
        glEnd();
        glBindTexture(GL_TEXTURE_2D, 0);
        BLACK8.bind();
        glBegin(GL_QUADS);
        glVertex2d(0, height());
        glVertex2d(0, 0);
        glVertex2d(width(), 0);
        glVertex2d(width(), height());
        glEnd();
    }

    @Override
    protected void renderForeground() {
        if (Utils.stringIsEmpty(title))
            return;
        GLColor.WHITE.bind();
        double h = stringHeight(title, 20);
        drawStringCentered(title, width() / 2, height() / 2d - 30 - h / 2, 20);
    }

    @Override
    public GLColor selectColor() {
        return GLColor.WHITE;
    }

    @Override
    protected void keyRelease(int key, int uChar) {
        if (key == GLFW.GLFW_KEY_ESCAPE)
            GNB.setScreen(new ScreenLogin());
    }
}
