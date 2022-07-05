package com.github.mjaroslav.globalnavalbattle.client;

import com.github.mjaroslav.globalnavalbattle.client.config.OptionsManager;
import com.github.mjaroslav.globalnavalbattle.client.render.GLColor;
import com.github.mjaroslav.globalnavalbattle.client.utils.KeyHandler;
import com.github.mjaroslav.globalnavalbattle.client.utils.MouseButton;
import com.github.mjaroslav.globalnavalbattle.client.utils.MouseHandler;
import com.github.mjaroslav.globalnavalbattle.client.render.font.BitmapPNGFont;
import com.github.mjaroslav.globalnavalbattle.client.render.font.GLFont;
import com.github.mjaroslav.globalnavalbattle.client.render.gui.Screen;
import com.github.mjaroslav.globalnavalbattle.client.render.gui.screen.ScreenMainMenu;
import com.github.mjaroslav.globalnavalbattle.client.resource.ResourceLoader;
import com.github.mjaroslav.globalnavalbattle.common.References;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GlobalNavalBattle {
    private static GlobalNavalBattle instance;

    public static GlobalNavalBattle getGlobalNavalBattle() {
        if (instance == null) {
            try {
                instance = new GlobalNavalBattle();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
        return instance;
    }

    private static final int[] FPS = new int[]{30, 60, 120};

    private final OptionsManager optionsManager;

    private long windowId;

    private boolean started = false;
    private boolean running = true;

    private int x;
    private int y;
    private int width;
    private int height;

    public final int GUI_WIDTH = 800;
    public final int GUI_HEIGHT = 400;

    private String title = References.title();
    private String version = References.version();

    public GLFont FONT;

    private Screen screenToSwap;
    private Screen currentScreen;

    private GlobalNavalBattle() throws IOException, UnsupportedOperationException {
        // Init options
        optionsManager = new OptionsManager();
        width = getOptions().getWidth();
        height = getOptions().getHeight();
        // Init window
        glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));
        if (!glfwInit())
            throw new UnsupportedOperationException("GLFW initialization failed!");
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        windowId = glfwCreateWindow(getOptions().getWidth(), getOptions().getHeight(), getTitle(), NULL, NULL);
        if (windowId == NULL)
            throw new UnsupportedOperationException("Could not create our Window!");
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (vidMode != null)
            glfwSetWindowPos(windowId, (vidMode.width() - getOptions().getWidth()) / 2, (vidMode.height() - getOptions().getHeight()) / 2);
        glfwMakeContextCurrent(windowId);
        glfwSetCursorPosCallback(windowId, (window, xPos, yPos) -> {
            x = (int) xPos;
            y = (int) yPos;
            if (currentScreen != null && !screenSwapped && !screenSwapping)
                currentScreen.onMouseMoved((int) (x / getDelta()), (int) (y / getDelta()));
        });
        glfwSetKeyCallback(windowId, new KeyHandler() {
            @Override
            public void onRelease(int key) {
                if (currentScreen != null && !screenSwapped && !screenSwapping)
                    currentScreen.onKeyRelease(key, -1);
            }
        });
        glfwSetCharCallback(windowId, (window, codepoint) -> {
            if (currentScreen != null && !screenSwapped && !screenSwapping)
                currentScreen.onKeyRelease(-1, codepoint);
        });
        glfwSetMouseButtonCallback(windowId, new MouseHandler() {
            @Override
            public void onRelease(MouseButton button) {
                if (currentScreen != null && !screenSwapped && !screenSwapping)
                    currentScreen.onMouseClicked(x, y, button);
            }
        });
        glfwSetWindowSizeCallback(windowId, (window, newWidth, newHeight) -> {
            getOptions().setWidth(newWidth);
            getOptions().setHeight(newHeight);
            width = newWidth;
            height = newHeight;
            glViewport(0, 0, getOptions().getWidth(), getOptions().getHeight());
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            glOrtho(0, getOptions().getWidth(), getOptions().getHeight(), 0, 1, -1);
            glMatrixMode(GL_MODELVIEW);
            glLoadIdentity();
            if (newHeight != GUI_HEIGHT * getDelta())
                setHeight((int) (GUI_HEIGHT * getDelta()));
        });
        glfwShowWindow(windowId);

        // Init GL
        GL.createCapabilities();
        glClearColor(1F, 1F, 1F, 1F);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glViewport(0, 0, getOptions().getWidth(), getOptions().getHeight());
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, getOptions().getWidth(), getOptions().getHeight(), 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        FONT = BitmapPNGFont.loadFont(ResourceLoader.getPath("default"));
    }

    private boolean screenSwapping = false;
    private boolean screenSwapped = false;

    public void setScreen(Screen screen) {
        if (!screen.canUse())
            return;
        screenSwapping = true;
        screenToSwap = screen;
        screenSwapped = true;
    }

    public long getWindowId() {
        return windowId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersion() {
        return version;
    }

    public double getDelta() {
        return (double) width / GUI_WIDTH;
    }

    public int getWidth() {
        return width;
    }


    public int getHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
        glfwSetWindowSize(windowId, width, height);
    }

    public void setHeight(int height) {
        this.height = height;
        glfwSetWindowSize(windowId, width, height);
    }

    private void update() {
        if (currentScreen != null && !screenSwapped && !screenSwapping)
            if (currentScreen.canUse())
                currentScreen.update();
            else setScreen(new ScreenMainMenu());
    }

    private void render() {
        glPushMatrix();
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GLColor.WHITE.bind();
        if (screenSwapping && screenSwapped) {
                if(screenToSwap != null)
                    screenToSwap.initGui();
                currentScreen = screenToSwap;
                screenToSwap = null;
                screenSwapping = false;
                screenSwapped = false;
        }
        if (currentScreen != null)
            if (currentScreen.canUse())
                currentScreen.render();
            else setScreen(new ScreenMainMenu());
        glPopMatrix();
    }

    private int ticks = 0;

    public int getTicks() {
        return ticks;
    }

    public void run() {
        if (started)
            throw new UnsupportedOperationException("Game already started!");
        else started = true;
        long lastTime = System.nanoTime() / 1000000000L;
        int frames = 1;
        setScreen(new ScreenMainMenu());
        new UpdateThread().start();
        while (running) {
            glfwPollEvents();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            render();
            glfwSwapBuffers(windowId);
            if (lastTime != System.nanoTime() / 1000000000L) {
                lastTime = System.nanoTime() / 1000000000L;
                glfwSetWindowTitle(windowId, getTitle() + " | FPS: " + frames);
                frames = 1;
            } else frames++;
            sync(FPS[getOptions().getFps()]);
            if (glfwWindowShouldClose(windowId))
                running = false;
        }
        Callbacks.glfwFreeCallbacks(windowId);
        ResourceLoader.unloadTextures();
        glfwDestroyWindow(windowId);
        glfwTerminate();
        ClientServer.closeServer();
    }

    public int getFPS() {
        return FPS[getOptions().getFps()];
    }

    public void shouldClose() {
        glfwSetWindowShouldClose(windowId, true);
    }

    private long variableYieldTime, lastTime;

    private void sync(int fps) {
        if (fps <= 0) return;
        long sleepTime = 1000000000 / fps;
        long yieldTime = Math.min(sleepTime, variableYieldTime + sleepTime % (1000000));
        long overSleep = 0;
        try {
            while (true) {
                long t = System.nanoTime() - lastTime;
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
            lastTime = System.nanoTime() - Math.min(overSleep, sleepTime);
            if (overSleep > variableYieldTime)
                variableYieldTime = Math.min(variableYieldTime + 200000, sleepTime);
            else if (overSleep < variableYieldTime - 200 * 1000)
                variableYieldTime = Math.max(variableYieldTime - 2000, 0);
        }
    }

    public OptionsManager.Options getOptions() {
        return optionsManager.getOptions();
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