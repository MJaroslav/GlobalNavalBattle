package com.github.mjaroslav.globalnavalbattle.server;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

public class Main {
    public static void main(String... args) {
        int port = 25565;
        for (String arg : args) {
            if (arg.startsWith("port"))
                port = Integer.valueOf(arg.replace("port=", ""));
            if (arg.startsWith("debug"))
                if (arg.replace("debug", "").equals("+"))
                    Configurator.setRootLevel(Level.DEBUG);
                else
                    Configurator.setLevel("FGLGames", Level.DEBUG);
        }
        try {
            Server.getServer().run(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
