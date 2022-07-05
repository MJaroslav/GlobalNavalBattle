package com.github.mjaroslav.globalnavalbattle.common.network;

import com.github.mjaroslav.globalnavalbattle.common.logic.Player;

import java.util.List;

public interface Network {
    Player getPlayer(String username);

    List<Player> getPlayers();

    void sendPacket(Object packet, String... players);

    void receivePacket(Object packet, Object... attachments) throws Exception;

    void openConnection(String host, int port) throws Exception;

    void closeConnection() throws Exception;

    void init() throws Exception;
}
