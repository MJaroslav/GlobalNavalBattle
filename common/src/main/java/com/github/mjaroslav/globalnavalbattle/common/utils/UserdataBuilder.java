package com.github.mjaroslav.globalnavalbattle.common.utils;

public class UserdataBuilder {
    private String username;

    public UserdataBuilder(String username) {
        this.username = username;
    }

    public UserdataBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public Userdata build() {
        Userdata userdata = new Userdata();
        if (Utils.stringIsEmpty(username))
            username = Generate.username();
        userdata.setUsername(username);
        return userdata;
    }

    public static boolean usernameAllowed(String username) {
        return !Utils.stringIsEmpty(username);
    }
}
