package com.TETOSOFT.assets;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;

/**
 * Loads images from disk and caches them so each file is only read once.
 * Also provides mirror/flip helpers used when building sprite sheets.
 */
public class AssetManager {

    private final GraphicsConfiguration gc;
    private final Map<String, Image> cache = new HashMap<>();

    public AssetManager(GraphicsConfiguration gc) {
        this.gc = gc;
    }

    /**
     * Returns the image at {@code images/<name>}, loading it on first access.
     */
    public Image loadImage(String name) {
        return cache.computeIfAbsent("images/" + name,
                path -> new ImageIcon(path).getImage());
    }

    /** Returns a horizontally mirrored copy of the given image. */
    public Image getMirrorImage(Image image) {
        return getScaledImage(image, -1, 1);
    }

    /** Returns a vertically flipped copy of the given image. */
    public Image getFlippedImage(Image image) {
        return getScaledImage(image, 1, -1);
    }

    // -------------------------------------------------------------------------

    private Image getScaledImage(Image src, float scaleX, float scaleY) {
        AffineTransform transform = new AffineTransform();
        transform.scale(scaleX, scaleY);
        transform.translate(
                (scaleX - 1) * src.getWidth(null)  / 2,
                (scaleY - 1) * src.getHeight(null) / 2);

        Image dst = gc.createCompatibleImage(
                src.getWidth(null), src.getHeight(null), Transparency.BITMASK);
        Graphics2D g = (Graphics2D) dst.getGraphics();
        g.drawImage(src, transform, null);
        g.dispose();
        return dst;
    }

    public GraphicsConfiguration getGraphicsConfiguration() { return gc; }
}
