package com.github.mjaroslav.globalnavalbattle.client.logic;

import com.github.mjaroslav.globalnavalbattle.common.logic.Player;

public class ClientPlayer extends Player {
    private final String username;

    public ClientPlayer(String username) {
        this.username = username;
    }

    @Override
    public String username() {
        return username;
    }
}
