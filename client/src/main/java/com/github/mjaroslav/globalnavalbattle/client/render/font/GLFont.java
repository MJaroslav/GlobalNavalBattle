package com.github.mjaroslav.globalnavalbattle.client.render.font;

public abstract class GLFont {
    public abstract void drawString(String text, double x, double y, double size);

    public abstract double getWidth(String text, double size);

    public abstract double getHeight(String text, double size);

    public abstract String getName();

    public abstract int getSize();

    public abstract String getType();

    @Override
    public String toString() {
        return String.format("%s[name = %s, type = %s, size = %s]", super.toString(), getName(), getType(), getSize());
    }
}
