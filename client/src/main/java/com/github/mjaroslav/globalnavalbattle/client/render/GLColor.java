package com.github.mjaroslav.globalnavalbattle.client.render;

import java.awt.*;

import static org.lwjgl.opengl.GL11.glColor4d;

public class GLColor {
    public static final GLColor BLACK = new GLColor(Color.black);
    public static final GLColor BLUE = new GLColor(Color.blue);
    public static final GLColor CYAN = new GLColor(Color.cyan);
    public static final GLColor DARK_GRAY = new GLColor(Color.darkGray);
    public static final GLColor GRAY = new GLColor(Color.gray);
    public static final GLColor GREEN = new GLColor(Color.green);
    public static final GLColor LIGHT_GRAY = new GLColor(Color.lightGray);
    public static final GLColor MAGENTA = new GLColor(Color.magenta);
    public static final GLColor ORANGE = new GLColor(Color.orange);
    public static final GLColor PINK = new GLColor(Color.pink);
    public static final GLColor WHITE = new GLColor(Color.white);
    public static final GLColor YELLOW = new GLColor(Color.yellow);

    private double red;
    private double green;
    private double blue;
    private double alpha;

    public GLColor(String hex) {
        this(hex, 1);
    }

    public GLColor(String hex, double alpha) {
        int color = Integer.parseInt(hex, 16);
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;
        setRed(red / 255d);
        setGreen(green / 255d);
        setBlue(blue / 255d);
        setAlpha(alpha);
    }

    public GLColor(Color awt) {
        this(awt.getRed() / 255d, awt.getGreen() / 255d, awt.getBlue() / 255d, awt.getAlpha() / 255d);
    }

    public GLColor(double red, double green, double blue) {
        this(red, green, blue, 1);
    }

    public GLColor(double red, double green, double blue, double alpha) {
        setRed(red);
        setGreen(green);
        setBlue(blue);
        setAlpha(alpha);
    }

    public GLColor setRed(double red) {
        this.red = red;
        if (this.red > 1)
            this.red = 0;
        else if (this.red < 0)
            this.red = 0;
        return this;
    }

    public GLColor setGreen(double green) {
        this.green = green;
        if (this.green > 1)
            this.green = 0;
        else if (this.green < 0)
            this.green = 0;
        return this;
    }

    public GLColor setBlue(double blue) {
        this.blue = blue;
        if (this.blue > 1)
            this.blue = 0;
        else if (this.blue < 0)
            this.blue = 0;
        return this;
    }

    public GLColor setAlpha(double alpha) {
        this.alpha = alpha;
        if (this.alpha > 1)
            this.alpha = 0;
        else if (this.alpha < 0)
            this.alpha = 0;
        return this;
    }

    public void bind() {
        bind(1, 1, 1, 1);
    }

    public void bind(double modRed, double modGreen, double modBlue, double modAlpha) {
        glColor4d(red * modRed, green * modGreen, blue * modBlue, alpha * modAlpha);
    }

    public GLColor copy(double modRed, double modGreen, double modBlue, double modAlpha) {
        return new GLColor(red * modRed, green * modGreen, blue * modBlue, alpha * modAlpha);
    }

    public GLColor copy() {
        return copy(1, 1, 1, 1);
    }
}
