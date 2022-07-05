package com.github.mjaroslav.globalnavalbattle.common.logic;

public enum PositionType {
    UNDISCOVERED(0),
    DISCOVERED(1),
    SHIP(2),
    WOUNDED_SHIP(3),
    DESTROYED_SHIP(4),
    UNKNOWN(-1);

    public final int ID;

    PositionType(int id) {
        ID = id;
    }

    public static PositionType getByID(int id) {
        for (PositionType type : values())
            if (type.ID == id)
                return type;
        return UNKNOWN;
    }
}
