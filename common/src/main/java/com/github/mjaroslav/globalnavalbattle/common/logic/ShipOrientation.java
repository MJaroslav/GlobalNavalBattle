package com.github.mjaroslav.globalnavalbattle.common.logic;

public enum ShipOrientation {
    HORIZONTAL(0),
    VERTICAL(1),
    UNKNOWN(-1);

    public final int ID;

    ShipOrientation(int id) {
        ID = id;
    }

    public static ShipOrientation getById(int id) {
        for (ShipOrientation orientation : values())
            if (orientation.ID == id)
                return orientation;
        return UNKNOWN;
    }
}
