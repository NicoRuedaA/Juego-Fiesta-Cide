package com.TETOSOFT.core;

import java.awt.*;
import javax.swing.ImageIcon;

import com.TETOSOFT.graphics.ScreenManager;

/**
 * Abstract base class that owns the game loop.
 * Subclasses implement {@link #draw(Graphics2D)} and may override
 * {@link #update(long)}.
 */
public abstract class GameCore {

    protected static final int FONT_SIZE = 18;

    private boolean isRunning;
    protected ScreenManager screen;

    /** Signals the game loop to stop after the current frame. */
    public void stop() {
        isRunning = false;
    }

    /** Entry point: initialises the window then runs the game loop. */
    public void run() {
        try {
            init();
            gameLoop();
        } finally {
            screen.restoreScreen();
            lazilyExit();
        }
    }

    /**
     * Opens the window and marks the game as running.
     * Subclasses should call {@code super.init()} first.
     */
    public void init() {
        screen = new ScreenManager();
        screen.setWindowedMode(GameConstants.WINDOW_WIDTH, GameConstants.WINDOW_HEIGHT);

        Window window = screen.getWindow();
        window.setFont(new Font("Dialog", Font.PLAIN, FONT_SIZE));
        window.setBackground(Color.BLACK);
        window.setForeground(Color.WHITE);

        isRunning = true;
    }

    public Image loadImage(String fileName) {
        return new ImageIcon(fileName).getImage();
    }

    /** Runs the fixed-step game loop until {@link #stop()} is called. */
    private void gameLoop() {
        long currTime = System.currentTimeMillis();

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

    /**
     * Updates game state.  Default implementation does nothing.
     *
     * @param elapsedTime milliseconds since the last frame
     */
    public void update(long elapsedTime) {}

    /** Renders the current frame. */
    public abstract void draw(Graphics2D g);

    /**
     * Waits 2 seconds for the JVM to exit on its own, then forces
     * {@code System.exit(0)}.  Needed when the Java Sound system is active.
     */
    private void lazilyExit() {
        Thread t = new Thread(() -> {
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
            System.exit(0);
        });
        t.setDaemon(true);
        t.start();
    }
}
