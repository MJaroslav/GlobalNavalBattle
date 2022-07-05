package com.github.mjaroslav.globalnavalbattle.server.logic;

import io.netty.channel.Channel;
import com.github.mjaroslav.globalnavalbattle.common.logic.Player;

public class ServerPlayer extends Player {
    private String username;
    private Channel channel;

    public ServerPlayer(String username, Channel channel) {
        this.username = username;
        this.channel = channel;
    }

    @Override
    public String username() {
        return username;
    }

    public Channel connection() {
        return channel;
    }
}
