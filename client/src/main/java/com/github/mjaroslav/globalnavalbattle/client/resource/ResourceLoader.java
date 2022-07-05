package com.github.mjaroslav.globalnavalbattle.client.resource;

import com.github.mjaroslav.globalnavalbattle.client.render.GLTexture;

import java.util.ArrayList;
import java.util.List;

public class ResourceLoader {
    private static final List<GLTexture> TEXTURES = new ArrayList<>();

    public static GLTexture getOrLoadTexture(ResourcePath path) {
        GLTexture texture = getTexture(path);
        if (texture == null) {
            texture = GLTexture.loadTexture(path);
            TEXTURES.add(texture);
        }
        return texture;
    }

    public static GLTexture getTexture(ResourcePath path) {
        for (GLTexture texture : TEXTURES)
            if (path.equals(texture.getPath()))
                return texture;
        return null;
    }

    public static void reloadAll() {
        TEXTURES.forEach(GLTexture::reload);
    }

    public static void unloadTextures() {
        TEXTURES.forEach(GLTexture::delete);
        TEXTURES.clear();
    }

    public static ResourcePath getTexturePath(String name) {
        return new ResourcePath(ResourceType.TEXTURES, name);
    }

    public static ResourcePath getTextPath(String name) {
        return new ResourcePath(ResourceType.TEXT, name);
    }

    public static ResourcePath getFontPath(String name) {
        return new ResourcePath(ResourceType.FONT, name);
    }

    public static ResourcePath getPath(String name) {
        return new ResourcePath(ResourceType.OTHER, name);
    }
}
