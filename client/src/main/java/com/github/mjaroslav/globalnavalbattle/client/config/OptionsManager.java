package com.github.mjaroslav.globalnavalbattle.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.github.mjaroslav.globalnavalbattle.common.utils.Generate;
import com.github.mjaroslav.globalnavalbattle.common.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class OptionsManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private Options options;
    private final Options defaultOptions = generateDefaultOptions();

    private static final File FILE = new File(System.getProperty("globalnavalbattle.optionsfile", "options.txt"));

    public OptionsManager() throws IOException {
        if (FILE.exists() && FILE.isFile())
            read();
        else createNewFile();
    }

    public Options getOptions() {
        return options;
    }

    private Options generateDefaultOptions() {
        return new Options();
    }

    private void createNewFile() throws IOException {
        //noinspection ResultOfMethodCallIgnored
        FILE.createNewFile();
        options = defaultOptions;
        write();
    }

    private void read() throws IOException {
        try {
            Reader reader = Files.newBufferedReader(FILE.toPath(), StandardCharsets.UTF_8);
            options = GSON.fromJson(reader, Options.class);
            reader.close();
            if (options == null)
                createNewFile();
            write();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            createNewFile();
        }
    }

    private void write() {
        options.normalize();
    }

    public static class Options {
        @SerializedName("width")
        private int width = 600;
        @SerializedName("height")
        private int height = 400;
        @SerializedName("fps")
        private int fps = 0;
        @SerializedName("user")
        private String username = Generate.username();
        @SerializedName("last_server")
        private String lastServer = "";

        public int getFps() {
            return fps;
        }

        public void setFps(int fps) {
            this.fps = fps;
            normalize();
        }

        public String getUsername() {
            return username;
        }

        public String getLastServer() {
            return lastServer;
        }

        public void setLastServer(String lastServer) {
            this.lastServer = lastServer;
            normalize();
        }

        public void setUsername(String username) {
            this.username = username;
            normalize();
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
            normalize();
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
            normalize();
        }

        private void normalize() {
            if(width < 0)
                width = 100;
            if(height < 0)
                height = 100;
            if(fps < 0)
                fps = 0;
            if(fps > 2)
                fps = 2;
            if(Utils.stringIsEmpty(username))
                username = Generate.username();
            write();
        }

        private void write() {
            try {
                Writer writer = Files.newBufferedWriter(FILE.toPath(), StandardCharsets.UTF_8);
                GSON.toJson(this, writer);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
