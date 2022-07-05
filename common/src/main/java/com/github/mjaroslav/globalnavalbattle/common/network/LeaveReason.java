package com.github.mjaroslav.globalnavalbattle.common.network;

public enum LeaveReason {
    JUST_LEAVE(0), CONNECTION_LOST(1), UNKNOWN(-1);

    public final int ID;

    LeaveReason(int id) {
        ID = id;
    }

    public static LeaveReason getByID(int id) {
        for (LeaveReason reason : values())
            if (reason.ID == id)
                return reason;
        return UNKNOWN;
    }
}
