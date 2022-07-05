package com.github.mjaroslav.globalnavalbattle.client;

import com.github.mjaroslav.globalnavalbattle.client.network.NetworkManager;
import com.github.mjaroslav.globalnavalbattle.client.network.PacketFactory;

import java.util.concurrent.Executors;

public class ClientServer {
    public static boolean hasServer() {
        return server != null;
    }

    public static boolean ready() {
        return hasServer() && server.isReady();
    }

    public static boolean closed() {
        return hasServer() && server.isClosed();
    }

    public static ClientServer startServer(String host, int port) {
        if (hasServer() && ready())
            return server;
        server = new ClientServer();
        try {
            server.run(host, port);
        } catch (Exception e) {
            e.printStackTrace();
            server.closed = true;
        }
        return server;
    }

    public static ClientServer getServer() {
        return server;
    }

    public static void closeServer() {
        if (hasServer())
            server.stop();
    }


    public void sendPacket(Object packet) {
        net.sendPacket(packet);
    }

    private static ClientServer server;

    private final NetworkManager net = new NetworkManager();

    private ClientServer() {
    }

    public void run(String host, int port) throws Exception {
        net.init();
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                net.openConnection(host, port);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void stop() {
        try {
            net.sendPacket(PacketFactory.leave(GlobalNavalBattle.getGlobalNavalBattle().getOptions().getUsername(), 0));
            net.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isReady() {
        return !net.isClosed() && net.isReady();
    }

    private boolean closed = false;

    public boolean isClosed() {
        return closed || net.isClosed();
    }
}
