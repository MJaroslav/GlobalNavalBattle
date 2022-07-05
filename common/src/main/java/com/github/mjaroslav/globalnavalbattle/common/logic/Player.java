package com.github.mjaroslav.globalnavalbattle.common.logic;

import com.github.mjaroslav.globalnavalbattle.common.utils.Userdata;
import com.github.mjaroslav.globalnavalbattle.common.utils.UserdataBuilder;

public abstract class Player {
    public abstract String username();

    public Userdata toData() {
        return new UserdataBuilder(username()).build();
    }
}
