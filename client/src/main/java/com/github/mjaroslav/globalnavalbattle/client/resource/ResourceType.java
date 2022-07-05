package com.github.mjaroslav.globalnavalbattle.client.resource;

public enum ResourceType {
    FONT("fonts"), TEXTURES("textures"), TEXT("texts"), OTHER(null);

    public final String PATH;

    ResourceType(String path) {
        PATH = path;
    }
}
