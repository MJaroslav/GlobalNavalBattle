package com.github.mjaroslav.globalnavalbattle.client.render.font;

import com.github.mjaroslav.globalnavalbattle.client.render.GLTexture;
import com.github.mjaroslav.globalnavalbattle.client.resource.ResourceLoader;
import com.github.mjaroslav.globalnavalbattle.client.resource.ResourcePath;
import com.github.mjaroslav.globalnavalbattle.client.resource.ResourceType;
import com.github.mjaroslav.globalnavalbattle.common.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class BitmapPNGFont extends GLFont {
    private final Map<Integer, GLTexture> pages;
    private final Map<Integer, Glyph> glyphs;
    private final Map<Integer, Map<Integer, Integer>> kernings;

    private int pageWidth;
    private int pageHeight;
    private int size;
    private String face;

    private BitmapPNGFont() {
        pages = new HashMap<>();
        glyphs = new HashMap<>();
        kernings = new HashMap<>();
    }

    @Override
    public String getName() {
        return face;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public String getType() {
        return "bitmap\\png";
    }

    @Override
    public void drawString(String text, double x, double y, double size) {
        size /= this.size;
        double dx = x;
        double dy = y;
        double cx;
        double cy;
        double cw;
        double ch;
        int prevChar;
        int prevPage = -1;
        glPushMatrix();
        for (String line : text.split("\n")) {
            for (int c : line.chars().toArray()) {
                if (glyphs.containsKey(c)) {
                    Glyph g = glyphs.get(c);
                    if (prevPage != g.PAGE) {
                        if (prevPage != -1) glEnd();
                        pages.get(g.PAGE).bind();
                        glBegin(GL_QUADS);
                        prevPage = g.PAGE;
                    }
                    cx = dx + g.X_OFFSET * size;
                    cy = dy + g.Y_OFFSET * size;
                    cw = g.WIDTH * size;
                    ch = g.HEIGHT * size;
                    glTexCoord2d(g.U_MIN, g.V_MAX);
                    glVertex2d(cx, cy + ch);
                    glTexCoord2d(g.U_MIN, g.V_MIN);
                    glVertex2d(cx, cy);
                    glTexCoord2d(g.U_MAX, g.V_MIN);
                    glVertex2d(cx + cw, cy);
                    glTexCoord2d(g.U_MAX, g.V_MAX);
                    glVertex2d(cx + cw, cy + ch);
                    prevChar = c;
                    dx += (g.X_ADVANCE + getKerning(prevChar, c)) * size;
                }
            }
            dy += this.size;
            dx = 0;
        }
        glEnd();
        glPopMatrix();
    }

    @Override
    public double getWidth(String text, double size) {
        size /= this.size;
        double w = 0;
        double lw;
        int prevChar;
        for (String line : text.split("\n")) {
            lw = 0;
            prevChar = -1;
            for (int c : line.chars().toArray())
                if (glyphs.containsKey(c)) {
                    Glyph g = glyphs.get(c);
                    lw += g.X_ADVANCE + getKerning(prevChar, c);
                    prevChar = c;
                }
            w = Math.max(w, lw);
        }
        return w * size;
    }

    @Override
    public double getHeight(String text, double size) {
        size /= this.size;
        return text.split("\n").length * this.size * size;
    }

    private void setKerning(int first, int second, int amount) {
        if (amount == 0 && kernings.containsKey(first) && kernings.get(first).containsKey(second)) {
            kernings.get(first).remove(second);
            if (kernings.get(first).isEmpty())
                kernings.remove(first);
        } else {
            if (!kernings.containsKey(first))
                kernings.put(first, new HashMap<>());
            if (kernings.get(first).containsKey(second))
                kernings.get(first).replace(second, amount);
            else
                kernings.get(first).put(second, amount);
        }
    }

    private int getKerning(int first, int second) {
        return kernings.containsKey(first) ? kernings.get(first).getOrDefault(second, 0) : 0;
    }

    public static BitmapPNGFont loadFont(ResourcePath path) {
        path.setType(ResourceType.FONT);
        BitmapPNGFont result;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(path.getInputStream("font.fnt")))) {
            result = new BitmapPNGFont();
            String line;
            Map<String, String> info;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) continue;
                if (line.startsWith("info ")) {
                    info = Utils.parseKeyValueString(line.substring(7));
                    result.face = info.get("face");
                    result.size = Integer.valueOf(info.get("size"));
                }
                if (line.startsWith("common ")) {
                    info = Utils.parseKeyValueString(line.substring(7));
                    result.pageWidth = Integer.valueOf(info.get("scaleW"));
                    result.pageHeight = Integer.valueOf(info.get("scaleH"));
                }
                if (line.startsWith("page ")) {
                    info = Utils.parseKeyValueString(line.substring(5));
                    result.pages.put(Integer.valueOf(info.get("id")),
                            ResourceLoader.getOrLoadTexture(path.copy(info.get("file").replace("\"", ""))));
                }
                if (line.startsWith("char ")) {
                    info = Utils.parseKeyValueString(line.substring(5));
                    int page = Integer.valueOf(info.get("page"));
                    double uMin = (double) Integer.valueOf(info.get("x")) / result.pageWidth;
                    double vMin = (double) Integer.valueOf(info.get("y")) / result.pageHeight;
                    double uMax = (double) (Integer.valueOf(info.get("x")) + Integer.valueOf(info.get("width"))) / result.pageWidth;
                    double vMax = (double) (Integer.valueOf(info.get("y")) + Integer.valueOf(info.get("height"))) / result.pageHeight;
                    int width = Integer.valueOf(info.get("width"));
                    int height = Integer.valueOf(info.get("height"));
                    int xOffset = Integer.valueOf(info.get("xoffset"));
                    int yOffset = Integer.valueOf(info.get("yoffset"));
                    int xAdvance = Integer.valueOf(info.get("xadvance"));
                    result.glyphs.put(Integer.valueOf(info.get("id")), new Glyph(page, uMin, vMin, uMax, vMax, width, height, xOffset, yOffset, xAdvance));
                }
                if (line.startsWith("kerning ")) {
                    info = Utils.parseKeyValueString(line.substring(8));
                    int first = Integer.valueOf(info.get("first"));
                    int second = Integer.valueOf(info.get("second"));
                    int amount = Integer.valueOf(info.get("amount"));
                    result.setKerning(first, second, amount);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    private static class Glyph {
        private final double U_MIN;
        private final double V_MIN;
        private final double U_MAX;
        private final double V_MAX;
        private final int WIDTH;
        private final int HEIGHT;
        private final int X_OFFSET;
        private final int Y_OFFSET;
        private final int X_ADVANCE;
        private final int PAGE;

        private Glyph(int page, double uMin, double vMin, double uMax, double vMax, int width, int height, int xOffset, int yOffset, int xAdvance) {
            PAGE = page;
            U_MIN = uMin;
            V_MIN = vMin;
            U_MAX = uMax;
            V_MAX = vMax;
            WIDTH = width;
            HEIGHT = height;
            X_OFFSET = xOffset;
            Y_OFFSET = yOffset;
            X_ADVANCE = xAdvance;
        }
    }
}
