package com.github.mjaroslav.globalnavalbattle.common.network;

import com.google.gson.JsonElement;
import io.netty.channel.ChannelHandlerContext;

public interface Incoming {
    void onReceive(JsonElement packet, ChannelHandlerContext ctx, Network network);
}
