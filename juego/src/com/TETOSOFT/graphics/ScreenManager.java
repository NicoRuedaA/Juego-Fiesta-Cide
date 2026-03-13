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

    public void setFullScreenMode() {
        GraphicsDevice device = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();

        frame = new JFrame("Super Cide Bros");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIgnoreRepaint(true);
        frame.setUndecorated(true); // sin barra de título
        frame.setResizable(false);

        device.setFullScreenWindow(frame); // pantalla completa real (exclusiva)
        frame.createBufferStrategy(2);
    }

    public Graphics2D getGraphics() {
        if (frame != null) {
            BufferStrategy strategy = frame.getBufferStrategy();
            return (Graphics2D) strategy.getDrawGraphics();
        }
        return null;
    }

    public void update() {
        if (frame != null) {
            BufferStrategy strategy = frame.getBufferStrategy();
            if (!strategy.contentsLost()) {
                strategy.show();
            }
        }
        Toolkit.getDefaultToolkit().sync();
    }

    public JFrame getWindow() {
        return frame;
    }

    public int getWidth() {
        return frame != null ? frame.getContentPane().getWidth() : 0;
    }

    public int getHeight() {
        return frame != null ? frame.getContentPane().getHeight() : 0;
    }

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
