package com.TETOSOFT.test;

import java.awt.*;
import javax.swing.ImageIcon;

import com.TETOSOFT.graphics.ScreenManager;

public abstract class GameCore {

    protected static final int FONT_SIZE = 18;

    // Resolucion de la ventana al iniciar
    private static final int WINDOW_WIDTH  = 800;
    private static final int WINDOW_HEIGHT = 600;

    private boolean isRunning;
    protected ScreenManager screen;

    public void stop() {
        isRunning = false;
    }

    public void run() {
        try {
            init();
            gameLoop();
        } finally {
            screen.restoreScreen();
            lazilyExit();
        }
    }

    public void lazilyExit() {
        Thread thread = new Thread() {
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {}
                System.exit(0);
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    public void init() {
        screen = new ScreenManager();
        screen.setWindowedMode(WINDOW_WIDTH, WINDOW_HEIGHT);

        Window window = screen.getWindow();
        window.setFont(new Font("Dialog", Font.PLAIN, FONT_SIZE));
        window.setBackground(Color.BLACK);
        window.setForeground(Color.WHITE);

        isRunning = true;
    }

    public Image loadImage(String fileName) {
        return new ImageIcon(fileName).getImage();
    }

    public void gameLoop() {
        long startTime = System.currentTimeMillis();
        long currTime = startTime;

        while (isRunning) {
            long elapsedTime = System.currentTimeMillis() - currTime;
            currTime += elapsedTime;

            update(elapsedTime);

            Graphics2D g = screen.getGraphics();
            draw(g);
            g.dispose();
            screen.update();
        }
    }

    public void update(long elapsedTime) {
        // subclases sobreescriben esto
    }

    public abstract void draw(Graphics2D g);
}
