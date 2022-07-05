package com.github.mjaroslav.globalnavalbattle.client.render.gui.screen;

import com.github.mjaroslav.globalnavalbattle.client.render.GLColor;
import com.github.mjaroslav.globalnavalbattle.client.render.GLTexture;
import com.github.mjaroslav.globalnavalbattle.client.render.gui.element.button.Button;
import com.github.mjaroslav.globalnavalbattle.client.resource.ResourceLoader;
import com.github.mjaroslav.globalnavalbattle.client.render.gui.Screen;
import com.github.mjaroslav.globalnavalbattle.common.References;

import static org.lwjgl.opengl.GL11.*;

public class ScreenMainMenu extends Screen {
    private GLTexture bg;
    private static final GLColor BLACK8 = GLColor.BLACK.copy().setAlpha(0.8);
    private static final GLColor LIGHT_GRAY4 = GLColor.LIGHT_GRAY.copy().setAlpha(0.4);
    private static final GLColor GRAY4 = GLColor.GRAY.copy().setAlpha(0.4);
    private static final GLColor DARK_GRAY4 = GLColor.DARK_GRAY.copy().setAlpha(0.4);

    @Override
    public void initGui() {
        bg = ResourceLoader.getOrLoadTexture(ResourceLoader.getTexturePath("main_background.png"));
        elements.add(new Button(this, 25, 90, 250, 40, "Играть", 20, LIGHT_GRAY4, GRAY4, DARK_GRAY4, GLColor.WHITE));
        elements.add(new Button(this, 25, 155, 250, 40, "Настройки", 20, LIGHT_GRAY4, GRAY4, DARK_GRAY4, GLColor.WHITE));
        elements.add(new Button(this, 25, height() - 65, 250, 40, "Выйти", 20, LIGHT_GRAY4, GRAY4, DARK_GRAY4, GLColor.WHITE));
    }

    @Override
    public void buttonUpdated(int id) {
        switch (id) {
            case 0: {
                GNB.setScreen(new ScreenLogin());
                break;
            }
            case 1: {
                GNB.setScreen(new ScreenSettings());
                break;
            }
            case 2: {
                GNB.shouldClose();
                break;
            }
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
        drawStringCentered(References.NAME, 150, 45, 36);
        double tx = width() - 10 - GNB.FONT.getWidth(GNB.getVersion(), 20);
        double ty = 10;
        drawString(GNB.getVersion(), tx, ty, 20);
    }

    @Override
    public GLColor selectColor() {
        return GLColor.WHITE;
    }
}
