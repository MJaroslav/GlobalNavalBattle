package com.github.mjaroslav.globalnavalbattle.common.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BoundBox {
    private Point min;
    private Point max;
    private int width;
    private int height;

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Point getMax() {
        return max.copy();
    }

    public Point getMin() {
        return min.copy();
    }

    private BoundBox(Point a, Point b) {
        min = Point.min(a, b);
        max = Point.max(a, b);
        width = max.getX() - min.getX();
        height = max.getY() - min.getY();
    }

    public boolean boxIntersects(BoundBox box) {
        return pointInBox(box.min.getX(), box.max.getY()) ||
                pointInBox(box.min.getX(), box.min.getY()) ||
                pointInBox(box.max.getX(), box.min.getY()) ||
                pointInBox(box.max.getX(), box.max.getY());
    }

    public boolean boxInside(BoundBox box) {
        return pointInBox(box.min.getX(), box.max.getY()) ||
                pointInBox(box.min.getX(), box.min.getY()) ||
                pointInBox(box.max.getX(), box.min.getY()) ||
                pointInBox(box.max.getX(), box.max.getY());
    }

    public List<Point> toPointList() {
        ArrayList<Point> result = new ArrayList<>();
        for (int x = min.getX(); x < max.getX() + 1; x++)
            for (int y = min.getY(); y < max.getY() + 1; y++)
                result.add(Point.create(x, y));
        return result;
    }

    public void forEach(Consumer<Point> action) {
        toPointList().forEach(action);
    }

    public boolean pointInBox(Point point) {
        return pointInBox(point.getX(), point.getY());
    }

    public boolean pointInBox(int x, int y) {
        return x >= min.getX() && x <= max.getX() && y >= min.getY() && y < +max.getY();
    }


    public BoundBox expand(int value) {
        return expand(value, value, value, value);
    }

    public BoundBox expand(int up, int down, int left, int right) {
        min.down(down);
        min.left(left);
        max.up(up);
        max.right(right);
        width = max.getX() - min.getX();
        height = max.getY() - min.getY();
        return this;
    }

    public BoundBox copy() {
        return create(min.copy(), max.copy());
    }

    @Override
    public String toString() {
        return String.format("%s@[%s;%s;%s;%s]", getClass().getName(), min.getX(), min.getY(), max.getX(), max.getY());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BoundBox) {
            BoundBox box = (BoundBox) obj;
            return box.min.equals(min) && box.max.equals(max);
        }
        if (obj instanceof Point)
            return pointInBox((Point) obj);
        else
            return super.equals(obj);
    }

    public static BoundBox create(Point a, Point b) {
        return new BoundBox(a, b);
    }

    public static BoundBox createBySize(Point offset, int width, int height) {
        return create(offset, offset.copy().right(width).up(height));
    }
}
