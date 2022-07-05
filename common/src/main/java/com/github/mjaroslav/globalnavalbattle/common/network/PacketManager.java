package com.github.mjaroslav.globalnavalbattle.common.network;

import com.github.mjaroslav.globalnavalbattle.common.Packets;
import com.google.gson.JsonObject;
import io.netty.channel.ChannelHandlerContext;
import com.github.mjaroslav.globalnavalbattle.common.utils.Logger;

import java.util.*;

public class PacketManager {
    private Map<String, List<Incoming>> listeners = new HashMap<>();

    public int addListener(String packetType, Incoming listener) {
        if (!listeners.containsKey(packetType))
            listeners.put(packetType, new ArrayList<>());
        listeners.get(packetType).add(listener);
        return listeners.get(packetType).size() - 1;
    }

    public boolean removeListener(String packetType, Incoming listener) {
        return listeners.get(packetType).remove(listener);
    }

    public void removeListener(String packetType, int index) {
        listeners.get(packetType).remove(index);
    }

    public List<Incoming> getListeners(String packetType) {
        return listeners.getOrDefault(packetType, Collections.emptyList());
    }

    public void receive(JsonObject msg, ChannelHandlerContext ctx, Network network) {
        if (!msg.has(Packets.MAIN_TYPE)) {
            Logger.error("Empty packet received!");
            return;
        }
        String type = msg.get(Packets.MAIN_TYPE).getAsString();
        for (Incoming incoming : getListeners(type))
            incoming.onReceive(msg.has(Packets.MAIN_PACKET) ? msg.get(Packets.MAIN_PACKET) : null, ctx, network);
    }
}
