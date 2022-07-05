package com.github.mjaroslav.globalnavalbattle.client.render;

import de.matthiasmann.twl.utils.PNGDecoder;
import com.github.mjaroslav.globalnavalbattle.client.resource.ResourcePath;
import com.github.mjaroslav.globalnavalbattle.common.utils.Logger;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public class GLTexture {
    private ResourcePath path;

    private int ID;
    public int width;
    private int height;

    public boolean isReloadable() {
        return getPath() != null;
    }

    public ResourcePath getPath() {
        return path;
    }

    public void reload() {
        if (!isReloadable())
            return;
        if (ID != MemoryUtil.NULL)
            delete();
        GLTexture reloaded = loadTexture(getPath());
        ID = reloaded.ID;
        width = reloaded.width;
        height = reloaded.height;
    }

    public GLTexture() {
        ID = glGenTextures();
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, ID);
    }

    private void setParameter(int name, int value) {
        glTexParameteri(GL_TEXTURE_2D, name, value);
    }

    public int getID() {
        return ID;
    }

    public void delete() {
        glDeleteTextures(ID);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    private void setHeight(int height) {
        this.height = height;
    }

    private void setWidth(int width) {
        this.width = width;
    }

    private void uploadData(int w, int h, ByteBuffer data) {
        uploadData(GL_RGBA8, w, h, GL_RGBA, data);
    }

    private void uploadData(int intFormat, int w, int h, int f, ByteBuffer data) {
        glTexImage2D(GL_TEXTURE_2D, 0, intFormat, w, h, 0, f, GL_UNSIGNED_BYTE, data);
    }

    public static GLTexture createTexture(int w, int h, ByteBuffer data, boolean linear) {
        GLTexture result = new GLTexture();
        result.setWidth(w);
        result.setHeight(h);
        result.bind();
        if (linear) {
            result.setParameter(GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            result.setParameter(GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        } else {
            result.setParameter(GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            result.setParameter(GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        }
        result.uploadData(w, h, data);
        return result;
    }

    public static GLTexture loadTexture(ResourcePath path) {
        try {
            InputStream stream = path.getInputStream();
            PNGDecoder decoder = new PNGDecoder(stream);
            ByteBuffer buffer = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
            decoder.decode(buffer, 4 * decoder.getWidth(), PNGDecoder.Format.RGBA);
            buffer.flip();
            GLTexture result = createTexture(decoder.getWidth(), decoder.getHeight(), buffer, true);
            result.path = path;
            return result;
        } catch (Exception e) {
            ByteBuffer buffer = ByteBuffer.allocateDirect(4 * 2 * 2);
            putColor(Color.black, buffer);
            putColor(Color.magenta, buffer);
            putColor(Color.magenta, buffer);
            putColor(Color.black, buffer);
            buffer.flip();
            GLTexture result = createTexture(2, 2, buffer, false);
            if (e instanceof IOException || e instanceof NullPointerException) {
                Logger.warn("Can not find texture \"" + path + "\"");
                result.path = path;
            } else Logger.error("Exception when texture loading", e);
            return result;
        }
    }

    private static void putColor(Color color, ByteBuffer buffer) {
        buffer.put((byte) (color.getRed() & 0xFF))
                .put((byte) (color.getGreen() & 0xFF))
                .put((byte) (color.getBlue() & 0xFF))
                .put((byte) (color.getAlpha() & 0xFF));
    }
}
