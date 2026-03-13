package com.TETOSOFT.assets;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * Loads images from disk and caches them so each file is only read once.
 * Also provides mirror/flip helpers used when building sprite sheets.
 *
 * Carga primero desde el classpath (dentro del JAR), y si no encuentra
 * el recurso cae en la ruta relativa al directorio de trabajo (modo dev).
 */
public class AssetManager {

    private final GraphicsConfiguration gc;
    private final Map<String, Image> cache = new HashMap<>();

    public AssetManager(GraphicsConfiguration gc) {
        this.gc = gc;
    }

    /**
     * Returns the image at {@code images/<name>}, loading it on first access.
     * Looks inside the JAR first; falls back to a relative path for development.
     */
    public Image loadImage(String name) {
        return cache.computeIfAbsent(name, n -> loadFromClasspathOrDisk(n));
    }

    private Image loadFromClasspathOrDisk(String name) {
        // 1. Intentar cargar desde dentro del JAR / classpath
        String resourcePath = "/images/" + name;
        try (InputStream is = AssetManager.class.getResourceAsStream(resourcePath)) {
            if (is != null) {
                return ImageIO.read(is);
            }
        } catch (IOException e) {
            // continúa al fallback
        }

        // 2. Fallback: ruta relativa al directorio de trabajo (modo desarrollo)
        Image img = new ImageIcon("images/" + name).getImage();
        if (img.getWidth(null) <= 0) {
            System.err.println("[AssetManager] No se encontró la imagen: " + name);
        }
        return img;
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
                (scaleX - 1) * src.getWidth(null) / 2,
                (scaleY - 1) * src.getHeight(null) / 2);

        Image dst = gc.createCompatibleImage(
                src.getWidth(null), src.getHeight(null), Transparency.BITMASK);
        Graphics2D g = (Graphics2D) dst.getGraphics();
        g.drawImage(src, transform, null);
        g.dispose();
        return dst;
    }

    public GraphicsConfiguration getGraphicsConfiguration() {
        return gc;
    }
}