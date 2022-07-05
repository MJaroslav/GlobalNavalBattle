package com.github.mjaroslav.globalnavalbattle.common.logic;

import com.google.gson.JsonObject;

public class Point {
    private int x;
    private int y;

    private Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point up() {
        y++;
        return this;
    }

    public Point down() {
        y--;
        return this;
    }

    public Point left() {
        x--;
        return this;
    }

    public Point right() {
        x++;
        return this;
    }

    public Point up(int value) {
        y += value;
        return this;
    }

    public Point down(int value) {
        y -= value;
        return this;
    }

    public Point left(int value) {
        x -= value;
        return this;
    }

    public Point right(int value) {
        x += value;
        return this;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public JsonObject toJson() {
        JsonObject result = new JsonObject();
        result.addProperty("x", x);
        result.addProperty("y", y);
        return result;
    }

    public Point min(Point point) {
        return create(Math.min(x, point.x), Math.min(y, point.y));
    }

    public Point max(Point point) {
        return create(Math.max(x, point.x), Math.max(y, point.y));
    }

    public Point copy() {
        return create(x, y);
    }

    public static Point min(Point a, Point b) {
        return a.min(b);
    }

    public static Point max(Point a, Point b) {
        return a.max(b);
    }

    public static Point fromJson(JsonObject object) {
        return create(object.get("x").getAsInt(), object.get("y").getAsInt());
    }

    @Override
    public String toString() {
        return String.format("%s@[%s;%s]", getClass().getName(), x, y);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Point) {
            Point point = (Point) obj;
            return point.x == x && point.y == y;
        } else
            return super.equals(obj);
    }

    public static Point create(int x, int y) {
        return new Point(x, y);
    }
}
