package com.github.mjaroslav.globalnavalbattle.common.logic;

public enum ShipType {
    BATTLESHIP(0, 4, 1, "Линкор"),
    CRUISER(1, 3, 2, "Крейсер"),
    DESTROYER(2, 2, 3, "Эсминец"),
    TORPEDO_BOAT(3, 1, 4, "Торпедный катер"),
    UNKNOWN(-1, 0, 0, "Неизвестно");

    public final int LENGTH;
    public final int ID;
    public final int MAX_COUNT;
    public final String NAME;

    ShipType(int id, int length, int maxCount, String name) {
        ID = id;
        LENGTH = length;
        MAX_COUNT = maxCount;
        NAME = name;
    }

    public static ShipType getByID(int id) {
        for (ShipType ship : values())
            if (ship.ID == id)
                return ship;
        return UNKNOWN;
    }
}
