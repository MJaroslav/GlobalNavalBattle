package com.github.mjaroslav.globalnavalbattle.server.logic;

import com.github.mjaroslav.globalnavalbattle.server.network.NetworkManager;
import com.github.mjaroslav.globalnavalbattle.server.network.PacketFactory;
import com.google.gson.JsonElement;
import com.github.mjaroslav.globalnavalbattle.common.Packets;
import com.github.mjaroslav.globalnavalbattle.common.logic.BattleField;
import com.github.mjaroslav.globalnavalbattle.common.logic.Ship;
import com.github.mjaroslav.globalnavalbattle.server.Server;

import java.util.HashMap;
import java.util.Map;

public class LogicWaitingStart implements LogicElement {
    private final Map<String, BattleField> fields = new HashMap<>();
    private final Server server = Server.getServer();

    private final Map<String, Boolean> readyMap = new HashMap<>();
    private int ticks = 10 * 20;

    @Override
    public void update() {
        if (readyMap.size() > 1)
            if (allReady()) {
                ticks--;
                if (ticks <= 0) {
                    server.manager.remove(name());
                }
            } else ticks = 10 * 20;
        else ticks = 10 * 20;
    }

    private boolean checkFields() {
        return true;
    }

    private boolean allReady() {
        boolean result = true;
        for (Boolean check : readyMap.values())
            if (!check) {
                result = false;
                break;
            }
        return result && checkFields();
    }

    public static final String NAME = "waitingstart";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void onPlayerJoined(ServerPlayer player) {
        readyMap.put(player.username(), false);
        fields.put(player.username(), new BattleField());
    }

    @Override
    public void onPlayerLeaved(String player) {
        readyMap.remove(player);
        fields.remove(player);
    }

    @Override
    public void onPacketFromPlayer(ServerPlayer player, NetworkManager net, String type, JsonElement packet) {
        if (player != null)
            if (type.equals(Packets.TYPE_READY)) {
                readyMap.replace(player.username(), true);
                net.sendPacket(PacketFactory.ready(player.username()));
            } else if (type.equals(Packets.TYPE_UNREADY)) {
                readyMap.replace(player.username(), false);
                net.sendPacket(PacketFactory.unready(player.username()));
            } else if(type.equals(Packets.TYPE_SET_SHIP)) {
                if(!readyMap.get(player.username())) {
                    if(fields.get(player.username()).canAddShip(Ship.fromJson(packet.getAsJsonObject())))
                        fields.get(player.username()).addShip(Ship.fromJson(packet.getAsJsonObject()));
                }
            }
    }
}
