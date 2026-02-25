package com.TETOSOFT.graphics;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

/**
 * Owns the game window and exposes the double-buffered rendering surface.
 */
public class ScreenManager {

    private JFrame frame;

    /**
     * Creates and displays a non-resizable window with the given content area.
     * The window is centred on screen.
     */
    public void setWindowedMode(int width, int height) {
        frame = new JFrame("SuperMiro Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIgnoreRepaint(true);
        frame.setResizable(false);

        frame.getContentPane().setPreferredSize(new Dimension(width, height));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.createBufferStrategy(2);
    }

    /** Returns a Graphics2D for the back buffer. Must call {@link #update()} to flip. */
    public Graphics2D getGraphics() {
        if (frame != null) {
            BufferStrategy strategy = frame.getBufferStrategy();
            return (Graphics2D) strategy.getDrawGraphics();
        }
        return null;
    }

    /** Flips the back buffer to the screen. */
    public void update() {
        if (frame != null) {
            BufferStrategy strategy = frame.getBufferStrategy();
            if (!strategy.contentsLost()) {
                strategy.show();
            }
        }
        Toolkit.getDefaultToolkit().sync();
    }

    public JFrame getWindow() { return frame; }

    public int getWidth()  { return frame != null ? frame.getContentPane().getWidth()  : 0; }
    public int getHeight() { return frame != null ? frame.getContentPane().getHeight() : 0; }

    /** Disposes the window. */
    public void restoreScreen() {
        if (frame != null) {
            frame.dispose();
            frame = null;
        }
    }

    public BufferedImage createCompatibleImage(int w, int h, int transparency) {
        if (frame != null) {
            return frame.getGraphicsConfiguration().createCompatibleImage(w, h, transparency);
        }
        return null;
    }
}
