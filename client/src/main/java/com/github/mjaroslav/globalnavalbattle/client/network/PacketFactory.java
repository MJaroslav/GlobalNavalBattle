package com.github.mjaroslav.globalnavalbattle.client.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.github.mjaroslav.globalnavalbattle.common.logic.Ship;
import com.github.mjaroslav.globalnavalbattle.common.network.LeaveReason;
import com.github.mjaroslav.globalnavalbattle.common.utils.JsonUtils;
import com.github.mjaroslav.globalnavalbattle.common.utils.Userdata;

import static com.github.mjaroslav.globalnavalbattle.common.Packets.*;

public class PacketFactory {
    private static final String EMPTY_PATTERN = "{\"" + MAIN_TYPE + "\": \"%s\"}";
    private static final String VALUE = "{\"" + MAIN_TYPE + "\": \"%s\", \"" + MAIN_PACKET + "\": %s}";

    private static String format(String type) {
        return String.format(EMPTY_PATTERN, type);
    }

    private static String format(String type, Object object) {
        return String.format(VALUE, type, JsonUtils.toJsonElement(object).toString());
    }

    private static String format(String type, JsonElement element) {
        return String.format(VALUE, type, element.toString());
    }

    public static String login(Userdata data) {
        return format(TYPE_LOGIN, data);
    }

    public static String leave(String username, LeaveReason reason) {
        return leave(username, reason.ID);
    }

    public static String leave(String username, int reason) {
        JsonObject obj = new JsonObject();
        obj.addProperty(PACKET_USERNAME, username);
        obj.addProperty(PACKET_REASON, reason);
        return format(TYPE_LEAVE, obj);
    }

    public static String setShip(Ship ship) {
        return format(TYPE_SET_SHIP, ship.toJson());
    }

    public static String ready() {
        return format(TYPE_READY);
    }

    public static String unready() {
        return format(TYPE_UNREADY);
    }
}
