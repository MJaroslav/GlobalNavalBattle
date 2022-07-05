package com.github.mjaroslav.globalnavalbattle.server.logic;

import com.github.mjaroslav.globalnavalbattle.server.network.NetworkManager;
import com.google.gson.JsonElement;

public interface LogicElement {
    void update();

    String name();

    void onPlayerJoined(ServerPlayer player);

    void onPlayerLeaved(String player);

    void onPacketFromPlayer(ServerPlayer player, NetworkManager net, String type, JsonElement packet);
}
