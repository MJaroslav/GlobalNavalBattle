package com.github.mjaroslav.globalnavalbattle.common.logic;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class Ship {
    public final ShipType TYPE;
    public final ShipOrientation ORIENTATION;

    public final int X;
    public final int Y;

    private final List<Point> WOUNDS = new ArrayList<>();

    public Ship(ShipType type, int x, int y, ShipOrientation orientation) {
        TYPE = type;
        X = x;
        Y = y;
        ORIENTATION = orientation;
    }

    public boolean wound(Point pos) {
        if (WOUNDS.contains(pos) && !getShipBounds().pointInBox(pos)) return false;
        WOUNDS.add(pos);
        return true;
    }

    public JsonObject toJson() {
        JsonObject result = new JsonObject();
        result.addProperty("type", TYPE.ID);
        result.addProperty("x", X);
        result.addProperty("y", Y);
        result.addProperty("orientation", ORIENTATION.ID);
        return result;
    }

    public static Ship fromJson(JsonObject object) {
        ShipType type = ShipType.getByID(object.get("type").getAsInt());
        int x = object.get("x").getAsInt();
        int y = object.get("y").getAsInt();
        ShipOrientation orientation = ShipOrientation.getById(object.get("orientation").getAsInt());
        return new Ship(type, x, y, orientation);
    }

    public boolean isDestroyed() {
        return WOUNDS.size() >= TYPE.LENGTH;
    }

    public BoundBox getShipBounds() {
        return BoundBox.createBySize(Point.create(X, Y),
                ORIENTATION.equals(ShipOrientation.HORIZONTAL) ? TYPE.LENGTH : 1,
                ORIENTATION.equals(ShipOrientation.VERTICAL) ? TYPE.LENGTH : 1);
    }

    public BoundBox getLocationBounds() {
        return getShipBounds().expand(1);
    }
}
