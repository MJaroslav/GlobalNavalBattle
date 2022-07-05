package com.github.mjaroslav.globalnavalbattle.common.logic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class BattleField {
    private final PositionType[][] FIELD;
    private final List<Ship> SHIPS = new ArrayList<>();

    public static BoundBox FIELD_BOX = BoundBox.create(Point.create(0, 0), Point.create(9, 9));

    public BattleField(PositionType[][] field) {
        FIELD = field;
    }

    public BattleField() {
        FIELD = new PositionType[10][10];
        FIELD_BOX.forEach(p -> FIELD[p.getX()][p.getY()] = PositionType.UNDISCOVERED);
    }

    public PositionType get(Point pos) {
        return FIELD_BOX.pointInBox(pos) && FIELD[pos.getY()][pos.getY()] != null ? FIELD[pos.getX()][pos.getY()] : PositionType.UNKNOWN;
    }

    public void set(Point pos, PositionType type) {
        if (FIELD_BOX.pointInBox(pos))
            FIELD[pos.getX()][pos.getY()] = type != null ? type : PositionType.UNKNOWN;
    }

    public int getShipCount(ShipType type) {
        int result = 0;
        for (Ship ship : SHIPS)
            if (ship.TYPE.equals(type))
                result++;
        return result;
    }

    public void fromJson(JsonObject object) {
        FIELD_BOX.forEach(p -> FIELD[p.getX()][p.getY()] = PositionType.UNDISCOVERED);
        JsonArray array;
        if (object.has(String.valueOf(PositionType.DISCOVERED.ID))) {
            array = object.get(String.valueOf(PositionType.DISCOVERED.ID)).getAsJsonArray();
            array.forEach(jsonElement -> {
                Point point = Point.fromJson(jsonElement.getAsJsonObject());
                set(point, PositionType.DISCOVERED);
            });
        }
        if (object.has(String.valueOf(PositionType.SHIP.ID))) {
            array = object.get(String.valueOf(PositionType.SHIP.ID)).getAsJsonArray();
            array.forEach(jsonElement -> {
                Point point = Point.fromJson(jsonElement.getAsJsonObject());
                set(point, PositionType.SHIP);
            });
        }
        if (object.has(String.valueOf(PositionType.WOUNDED_SHIP.ID))) {
            array = object.get(String.valueOf(PositionType.WOUNDED_SHIP.ID)).getAsJsonArray();
            array.forEach(jsonElement -> {
                Point point = Point.fromJson(jsonElement.getAsJsonObject());
                set(point, PositionType.WOUNDED_SHIP);
            });
        }
        if (object.has(String.valueOf(PositionType.DISCOVERED.ID))) {
            array = object.get(String.valueOf(PositionType.DISCOVERED.ID)).getAsJsonArray();
            array.forEach(jsonElement -> {
                Point point = Point.fromJson(jsonElement.getAsJsonObject());
                set(point, PositionType.DISCOVERED);
            });
        }
    }

    public JsonObject toJson(boolean withShips) {
        JsonObject result = new JsonObject();
        JsonArray destroyed = new JsonArray();
        JsonArray discovered = new JsonArray();
        JsonArray wounded = new JsonArray();
        JsonArray ship = new JsonArray();
        FIELD_BOX.forEach(point -> {
            PositionType type = get(point);
            switch (type) {
                case SHIP:
                    if (withShips)
                        ship.add(point.toJson());
                    break;
                case DESTROYED_SHIP:
                    destroyed.add(point.toJson());
                    break;
                case DISCOVERED:
                    discovered.add(point.toJson());
                    break;
                case WOUNDED_SHIP:
                    wounded.add(point.toJson());
                    break;
            }
        });
        result.add(String.valueOf(PositionType.DESTROYED_SHIP.ID), destroyed);
        result.add(String.valueOf(PositionType.DISCOVERED.ID), discovered);
        result.add(String.valueOf(PositionType.WOUNDED_SHIP.ID), wounded);
        if (withShips)
            result.add(String.valueOf(PositionType.SHIP.ID), ship);
        return result;
    }

    public int getShipCount() {
        return SHIPS.size();
    }

    public Ship getShip(Point pos) {
        for (Ship ship : SHIPS)
            if (ship.getShipBounds().pointInBox(pos))
                return ship;
        return null;
    }

    public boolean ready() {
        return true;
    }
    public boolean canAddShipWithType(Ship newShip) {
        return newShip.TYPE.MAX_COUNT >= getShipCount(newShip.TYPE);
    }

    public boolean locationAvailable(Ship newShip) {
        if (!FIELD_BOX.boxInside(newShip.getShipBounds()))
            return false;
        for (Ship ship : SHIPS)
            if (ship.getLocationBounds().boxIntersects(newShip.getLocationBounds()))
                return false;
        return true;
    }

    public boolean canAddShip(Ship newShip) {
        return canAddShipWithType(newShip) && locationAvailable(newShip);
    }

    public boolean shot(Point pos) {
        switch (get(pos)) {
            case SHIP:
                Ship ship = getShip(pos);
                if (ship != null && ship.wound(pos))
                    if (ship.isDestroyed()) {
                        ship.getLocationBounds().forEach(p -> FIELD[p.getX()][p.getY()] = PositionType.DISCOVERED);
                        ship.getShipBounds().forEach(p -> FIELD[p.getX()][p.getY()] = PositionType.DESTROYED_SHIP);
                    } else
                        set(pos, PositionType.WOUNDED_SHIP);
                return true;
            case UNDISCOVERED:
                FIELD[pos.getX()][pos.getY()] = PositionType.DISCOVERED;
        }
        return false;
    }

    public boolean addShip(Ship newShip) {
        if (canAddShip(newShip)) {
            SHIPS.add(newShip);
            newShip.getShipBounds().forEach(p -> FIELD[p.getX()][p.getY()] = PositionType.SHIP);
            return true;
        } else return false;
    }
}
