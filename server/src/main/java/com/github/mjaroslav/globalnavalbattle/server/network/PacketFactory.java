package com.github.mjaroslav.globalnavalbattle.server.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.github.mjaroslav.globalnavalbattle.common.logic.Player;
import com.github.mjaroslav.globalnavalbattle.common.network.LeaveReason;
import com.github.mjaroslav.globalnavalbattle.common.network.LoginStatus;
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

    public static String login() {
        return format(TYPE_LOGIN);
    }

    public static String players(Player... players) {
        Userdata[] data = new Userdata[players.length];
        for (int i = 0; i < players.length; i++)
            data[i] = players[i].toData();
        return format(TYPE_PLAYERS, data);
    }

    public static String join(Player player) {
        return format(TYPE_JOIN, JsonUtils.toJsonElement(player.toData()));
    }

    public static String leave(String username, LeaveReason reason) {
        return leave(username, reason.ID);
    }

    private static String leave(String username, int reason) {
        JsonObject obj = new JsonObject();
        obj.addProperty(PACKET_USERNAME, username);
        obj.addProperty(PACKET_REASON, reason);
        return format(TYPE_LEAVE, obj);
    }

    public static String status(LoginStatus status) {
        return status(status.ID);
    }

    private static String status(int status) {
        return format(TYPE_STATUS, status);
    }

    public static String ready(String username) {
        return format(TYPE_READY, username);
    }

    public static String unready(String username) {
        return format(TYPE_UNREADY, username);
    }
}
