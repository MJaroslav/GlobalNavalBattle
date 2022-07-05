package com.github.mjaroslav.globalnavalbattle.server.logic;

import com.google.gson.JsonElement;
import com.github.mjaroslav.globalnavalbattle.server.network.NetworkManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogicManager {
    private final Map<String, LogicElement> elements = new HashMap<>();

    public void update() {
        elements.values().forEach(LogicElement::update);
    }

    public void put(LogicElement element) {
        if (!elements.containsKey(element.name()))
            elements.put(element.name(), element);
    }

    public void remove(String name) {
        elements.remove(name);
    }

    public List<LogicElement> getLogicElements() {
        return new ArrayList<>(elements.values());
    }

    public LogicElement getLogicElement(String name) {
        return elements.getOrDefault(name, EMPTYLOGIC);
    }

    public void onPlayerJoined(ServerPlayer player) {
        elements.values().forEach(e -> e.onPlayerJoined(player));
    }

    public void onPlayerLeaved(String player) {
        elements.values().forEach(e -> e.onPlayerLeaved(player));
    }

    public void onPacketFromPlayer(ServerPlayer player, NetworkManager net, String type, JsonElement packet) {
        elements.values().forEach(e -> e.onPacketFromPlayer(player, net, type, packet));
    }

    public boolean hasElement(String name) {
        return elements.containsKey(name);
    }

    private static final LogicElement EMPTYLOGIC = new LogicElement() {
        @Override
        public void update() {
        }

        @Override
        public String name() {
            return "EMPTY";
        }

        @Override
        public void onPlayerJoined(ServerPlayer player) {
        }

        @Override
        public void onPlayerLeaved(String player) {
        }

        @Override
        public void onPacketFromPlayer(ServerPlayer player, NetworkManager net, String type, JsonElement packet) {
        }
    };
}
