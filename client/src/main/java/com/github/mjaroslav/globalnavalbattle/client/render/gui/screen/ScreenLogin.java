package com.github.mjaroslav.globalnavalbattle.client.render.gui.screen;

import com.github.mjaroslav.globalnavalbattle.client.render.GLColor;
import com.github.mjaroslav.globalnavalbattle.client.render.GLTexture;
import com.github.mjaroslav.globalnavalbattle.client.render.gui.element.button.Button;
import com.github.mjaroslav.globalnavalbattle.client.render.gui.element.button.TextField;
import com.github.mjaroslav.globalnavalbattle.client.resource.ResourceLoader;
import com.github.mjaroslav.globalnavalbattle.client.render.gui.Screen;
import com.github.mjaroslav.globalnavalbattle.common.utils.Generate;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.opengl.GL11.*;

public class ScreenLogin extends Screen {
    private GLTexture bg;
    private static final GLColor BLACK8 = GLColor.BLACK.copy().setAlpha(0.8);
    private static final GLColor LIGHT_GRAY4 = GLColor.LIGHT_GRAY.copy().setAlpha(0.4);
    private static final GLColor GRAY4 = GLColor.GRAY.copy().setAlpha(0.4);
    private static final GLColor DARK_GRAY4 = GLColor.DARK_GRAY.copy().setAlpha(0.4);

    @Override
    public void initGui() {
        bg = ResourceLoader.getOrLoadTexture(ResourceLoader.getTexturePath("main_background.png"));
        elements.add(new TextField(this, 25, 50, 250, 40, Generate.username(), GNB.getOptions().getUsername(), 20, LIGHT_GRAY4, GRAY4, DARK_GRAY4, GLColor.WHITE, GLColor.DARK_GRAY));
        elements.add(new TextField(this, 25, 140, 250, 40, "localhost:25565", GNB.getOptions().getLastServer(), 20, LIGHT_GRAY4, GRAY4, DARK_GRAY4, GLColor.WHITE, GLColor.DARK_GRAY));
        elements.add(new Button(this, 25, height() - 130, 250, 40, "Подключиться", 20, LIGHT_GRAY4, GRAY4, DARK_GRAY4, GLColor.WHITE));
        elements.add(new Button(this, 25, height() - 65, 250, 40, "Назад", 20, LIGHT_GRAY4, GRAY4, DARK_GRAY4, GLColor.WHITE));
    }

    @Override
    public void buttonUpdated(int id) {
        if (id == 2) {
            GNB.getOptions().setUsername(((TextField) elements.get(0)).getValue());
            GNB.getOptions().setLastServer(((TextField) elements.get(1)).getValue());
            GNB.setScreen(new ScreenConnecting());
        }
        if (id == 3)
            GNB.setScreen(new ScreenMainMenu());
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
        glVertex2d(300, 0);
        glVertex2d(300, height());
        double tx = width() - 20 - GNB.FONT.getWidth(GNB.getVersion(), 20);
        double ty = GNB.FONT.getHeight(GNB.getVersion(), 20) + 20;
        glVertex2d(tx, ty);
        glVertex2d(tx, 0);
        glVertex2d(width(), 0);
        glVertex2d(width(), ty);
        glEnd();
    }

    @Override
    protected void renderForeground() {
        GLColor.WHITE.bind();
        double tx = width() - 10 - GNB.FONT.getWidth(GNB.getVersion(), 20);
        double ty = 10;
        drawString(GNB.getVersion(), tx, ty, 20);
        drawString("Имя пользователя", 25, 25, 20);
        drawString("IP:port", 25, 115, 20);
    }

    @Override
    protected void keyRelease(int key, int uChar) {
        if (key == GLFW.GLFW_KEY_ESCAPE)
            GNB.setScreen(new ScreenMainMenu());
    }

    @Override
    public GLColor selectColor() {
        return GLColor.WHITE;
    }
}
