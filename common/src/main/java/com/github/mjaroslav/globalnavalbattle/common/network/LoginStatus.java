package com.github.mjaroslav.globalnavalbattle.common.network;

public enum LoginStatus {
    OK(0), BAD_USERNAME(1), USERNAME_USED(2), NOT_LOGGED(3), UNKNOWN(-1);

    public final int ID;

    LoginStatus(int id) {
        ID = id;
    }

    public static LoginStatus getById(int id) {
        for (LoginStatus status : values())
            if (status.ID == id)
                return status;
        return UNKNOWN;
    }
}
