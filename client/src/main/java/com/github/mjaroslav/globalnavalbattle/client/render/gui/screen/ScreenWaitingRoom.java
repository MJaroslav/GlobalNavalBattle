package com.github.mjaroslav.globalnavalbattle.client.render.gui.screen;

import com.github.mjaroslav.globalnavalbattle.client.ClientServer;
import com.github.mjaroslav.globalnavalbattle.client.render.GLColor;
import com.github.mjaroslav.globalnavalbattle.client.render.GLTexture;
import com.github.mjaroslav.globalnavalbattle.client.render.gui.element.button.IconButton;
import com.github.mjaroslav.globalnavalbattle.client.resource.ResourceLoader;
import com.github.mjaroslav.globalnavalbattle.client.render.gui.Screen;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.opengl.GL11.*;

public class ScreenWaitingRoom extends Screen {
    private GLTexture bg;
    private GLTexture home;
    private static final GLColor BLACK8 = GLColor.BLACK.copy().setAlpha(0.8);
    private static final GLColor LIGHT_GRAY4 = GLColor.LIGHT_GRAY.copy().setAlpha(0.4);
    private static final GLColor GRAY4 = GLColor.GRAY.copy().setAlpha(0.4);
    private static final GLColor DARK_GRAY4 = GLColor.DARK_GRAY.copy().setAlpha(0.4);

    @Override
    public void initGui() {
        bg = ResourceLoader.getOrLoadTexture(ResourceLoader.getTexturePath("main_background.png"));
        home = ResourceLoader.getOrLoadTexture(ResourceLoader.getTexturePath("home.png"));
        elements.add(new IconButton(this, 5, height() - 45, 40, 40, home, LIGHT_GRAY4, GRAY4, DARK_GRAY4, GLColor.WHITE));
    }

    @Override
    public void buttonUpdated(int id) {
        if (id == 0) {
            GNB.setScreen(new ScreenMainMenu());
            ClientServer.closeServer();
        }
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
    }

    @Override
    protected void keyRelease(int key, int uChar) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            ClientServer.closeServer();
            GNB.setScreen(new ScreenMainMenu());
        }
    }

    @Override
    public GLColor selectColor() {
        return GLColor.WHITE;
    }

    @Override
    public boolean canUse() {
        return ClientServer.ready();
    }
}
