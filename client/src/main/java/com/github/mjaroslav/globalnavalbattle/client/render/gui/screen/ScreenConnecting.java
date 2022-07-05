package com.github.mjaroslav.globalnavalbattle.client.render.gui.screen;

import com.github.mjaroslav.globalnavalbattle.client.ClientServer;
import com.github.mjaroslav.globalnavalbattle.client.render.GLColor;
import com.github.mjaroslav.globalnavalbattle.client.render.GLTexture;
import com.github.mjaroslav.globalnavalbattle.client.render.gui.element.button.Button;
import com.github.mjaroslav.globalnavalbattle.client.resource.ResourceLoader;
import com.github.mjaroslav.globalnavalbattle.client.render.gui.Screen;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.opengl.GL11.*;

public class ScreenConnecting extends Screen {
    private GLTexture bg;
    private static final GLColor BLACK8 = GLColor.BLACK.copy().setAlpha(0.8);
    private static final GLColor LIGHT_GRAY4 = GLColor.LIGHT_GRAY.copy().setAlpha(0.4);
    private static final GLColor GRAY4 = GLColor.GRAY.copy().setAlpha(0.4);
    private static final GLColor DARK_GRAY4 = GLColor.DARK_GRAY.copy().setAlpha(0.4);

    @Override
    public void initGui() {
        bg = ResourceLoader.getOrLoadTexture(ResourceLoader.getTexturePath("main_background.png"));
        elements.add(new Button(this, width() / 2 - 125, height() / 2 + 10, 250, 40, "Отмена", 20, LIGHT_GRAY4, GRAY4, DARK_GRAY4, GLColor.WHITE));
        String[] info = GNB.getOptions().getLastServer().split(":");
        String host = info[0];
        int port = 25565;
        try {
            if (info.length == 2)
                port = Integer.valueOf(info[0]);
            if (port < 1)
                port = 25565;
        } catch (Exception ignored) {
        }
        final int p = port;
        new Thread(() -> ClientServer.startServer(host, p)).start();
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
        GLColor.WHITE.bind();
        drawStringCentered("Подключение" + WAIT[waiting], width() / 2, height() / 2 - 30, 20);
    }

    private static final String[] WAIT = new String[]{"", ".", "..", "..."};

    @Override
    public GLColor selectColor() {
        return GLColor.WHITE;
    }

    @Override
    protected void keyRelease(int key, int uChar) {
        if (key == GLFW.GLFW_KEY_ESCAPE)
            GNB.setScreen(new ScreenLogin());
    }

    private int ticks = 0;
    private int waiting = 0;

    @Override
    public void updateScreen() {
        waiting = ticks / 4;
        if (waiting > 3) {
            waiting = 0;
            ticks = 0;
        }
        ticks++;
    }
}
