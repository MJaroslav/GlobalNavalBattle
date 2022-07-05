package com.github.mjaroslav.globalnavalbattle.server;

import com.github.mjaroslav.globalnavalbattle.server.logic.LogicManager;
import com.github.mjaroslav.globalnavalbattle.server.logic.LogicWaitingStart;
import com.github.mjaroslav.globalnavalbattle.server.logic.ServerPlayer;
import com.github.mjaroslav.globalnavalbattle.server.network.NetworkManager;
import com.google.gson.JsonElement;

import java.util.concurrent.Executors;

public class Server {
    private static Server server;

    private Server() {
    }

    public static Server getServer() {
        if (server == null)
            server = new Server();
        return server;
    }

    public NetworkManager net = new NetworkManager();

    public void onPlayerJoined(ServerPlayer player) {
        manager.onPlayerJoined(player);
    }

    public void onPlayerLeaved(String player) {
        manager.onPlayerLeaved(player);
    }


    public void onPacketFromPlayer(ServerPlayer player, NetworkManager net, String type, JsonElement packet) {
        manager.onPacketFromPlayer(player, net, type, packet);
    }

    public void run(int port) throws Exception {
        net.init();
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                net.openConnection("localhost", port);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        new UpdateThread().start();
        while (!net.isClosed() && !net.isReady()) {
            Thread.sleep(1000);
        }
        manager.put(new LogicWaitingStart());
    }

    public void stop() {
        running = false;
        try {
            net.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int ticks = 0;

    public int getTicks() {
        return ticks;
    }

    private boolean running = true;

    public boolean hasGame() {
        return !manager.hasElement(LogicWaitingStart.NAME);
    }

    public final LogicManager manager = new LogicManager();

    private void update() {
        manager.update();
    }

    public class UpdateThread extends Thread {
        long variableYieldTime1 = 0;
        long lastTime1 = 0;
        long sleepTime;
        long yieldTime;
        long overSleep;

        public void run() {
            while (running) {
                update();
                ticks++;
                int fps = 20;
                sleepTime = 1000000000 / fps;
                yieldTime = Math.min(sleepTime, variableYieldTime1 + sleepTime % (1000000));
                overSleep = 0;
                try {
                    while (true) {
                        long t = System.nanoTime() - lastTime1;
                        if (t < sleepTime - yieldTime) Thread.sleep(1);
                        else if (t < sleepTime) Thread.yield();
                        else {
                            overSleep = t - sleepTime;
                            break;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lastTime1 = System.nanoTime() - Math.min(overSleep, sleepTime);
                    if (overSleep > variableYieldTime1)
                        variableYieldTime1 = Math.min(variableYieldTime1 + 200000, sleepTime);
                    else if (overSleep < variableYieldTime1 - 200 * 1000)
                        variableYieldTime1 = Math.max(variableYieldTime1 - 2000, 0);
                }
            }
        }
    }
}
