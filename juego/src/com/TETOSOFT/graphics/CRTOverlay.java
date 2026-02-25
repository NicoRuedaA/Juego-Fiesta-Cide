package com.TETOSOFT.graphics;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

/**
 * Capa visual CRT que se dibuja encima de todo el juego.
 *
 * Efectos incluidos:
 * - Scanlines horizontales
 * - Vignette (oscurecimiento de bordes)
 * - Parpadeo sutil de pantalla
 * - Ruido de grano
 *
 * Uso: llamar a {@link #draw(Graphics2D, int, int)} al final de
 * GameEngine.draw(),
 * después de dibujar todo lo demás. No modifica ningún otro sistema.
 *
 * Para activar/desactivar: {@link #setEnabled(boolean)}
 * Para ajustar intensidad: modificar las constantes al inicio de la clase.
 */
public class CRTOverlay {

    // -------------------------------------------------------------------------
    // Parámetros ajustables
    // -------------------------------------------------------------------------

    /** Separación entre scanlines en píxeles. 2 = muy marcado, 4 = sutil. */
    private static final int SCANLINE_SPACING = 3;
    /** Opacidad de las scanlines (0.0 = invisible, 1.0 = negro sólido). */
    private static final float SCANLINE_ALPHA = 0.18f;
    /**
     * Intensidad del vignette (0.0 = sin efecto, 1.0 = bordes completamente
     * negros).
     */
    private static final float VIGNETTE_ALPHA = 0.55f;
    /** Velocidad del parpadeo aleatorio (ms entre pulsos). */
    private static final int FLICKER_INTERVAL = 6000;
    /** Intensidad del parpadeo (0.0 = sin parpadeo, 0.08 = apenas perceptible). */
    private static final float FLICKER_STRENGTH = 0.04f;
    /** Activa el grano de ruido analógico. */
    private static final boolean ENABLE_GRAIN = true;
    /** Intensidad del grano (0-255). */
    private static final int GRAIN_INTENSITY = 18;

    // -------------------------------------------------------------------------
    // Estado interno
    // -------------------------------------------------------------------------

    private boolean enabled = true;

    private BufferedImage scanlinesCache;
    private BufferedImage vignetteCache;
    private int cachedWidth = -1;
    private int cachedHeight = -1;

    private long flickerTimer = 0;
    private float flickerAlpha = 0f;
    private long grainSeed = 0;

    // -------------------------------------------------------------------------
    // API pública
    // -------------------------------------------------------------------------

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Dibuja el efecto CRT encima del frame actual.
     * Llamar al final de GameEngine.draw(), después de todo lo demás.
     */
    public void draw(Graphics2D g, int width, int height, long elapsedTime) {
        if (!enabled)
            return;

        rebuildCacheIfNeeded(width, height);

        Composite original = g.getComposite();

        // 1. Scanlines
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, SCANLINE_ALPHA));
        g.drawImage(scanlinesCache, 0, 0, null);

        // 2. Vignette
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g.drawImage(vignetteCache, 0, 0, null);

        // 3. Parpadeo
        flickerTimer += elapsedTime;
        if (flickerTimer > FLICKER_INTERVAL) {
            flickerTimer = 0;
            flickerAlpha = FLICKER_STRENGTH;
        }
        if (flickerAlpha > 0) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, flickerAlpha));
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);
            flickerAlpha = Math.max(0, flickerAlpha - 0.004f);
        }

        // 4. Grano de ruido
        if (ENABLE_GRAIN) {
            drawGrain(g, width, height);
        }

        g.setComposite(original);
    }

    // -------------------------------------------------------------------------
    // Construcción de caché
    // -------------------------------------------------------------------------

    private void rebuildCacheIfNeeded(int width, int height) {
        if (width == cachedWidth && height == cachedHeight)
            return;
        cachedWidth = width;
        cachedHeight = height;
        buildScanlines(width, height);
        buildVignette(width, height);
    }

    private void buildScanlines(int width, int height) {
        scanlinesCache = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scanlinesCache.createGraphics();
        g.setColor(Color.BLACK);
        for (int y = 0; y < height; y += SCANLINE_SPACING) {
            g.drawLine(0, y, width, y);
        }
        g.dispose();
    }

    private void buildVignette(int width, int height) {
        vignetteCache = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = vignetteCache.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Gradiente radial de negro transparente en el centro a negro opaco en los
        // bordes
        float cx = width / 2f;
        float cy = height / 2f;
        float rx = width * 0.72f;
        float ry = height * 0.72f;

        RadialGradientPaint vignette = new RadialGradientPaint(
                new java.awt.geom.Point2D.Float(cx, cy),
                Math.max(rx, ry),
                new java.awt.geom.Point2D.Float(cx, cy),
                new float[] { 0.0f, 0.55f, 1.0f },
                new Color[] {
                        new Color(0, 0, 0, 0),
                        new Color(0, 0, 0, 0),
                        new Color(0, 0, 0, (int) (255 * VIGNETTE_ALPHA))
                },
                MultipleGradientPaint.CycleMethod.NO_CYCLE);

        g.setPaint(vignette);
        g.fillRect(0, 0, width, height);
        g.dispose();
    }

    // -------------------------------------------------------------------------
    // Grano
    // -------------------------------------------------------------------------

    private void drawGrain(Graphics2D g, int width, int height) {
        grainSeed = (grainSeed * 6364136223846793005L + 1442695040888963407L);
        java.util.Random rng = new java.util.Random(grainSeed);

        int particles = (width * height) / 400;
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                GRAIN_INTENSITY / 255f));

        for (int i = 0; i < particles; i++) {
            int x = rng.nextInt(width);
            int y = rng.nextInt(height);
            int bright = rng.nextInt(200) + 55;
            g.setColor(new Color(bright, bright, bright));
            g.fillRect(x, y, 1, 1);
        }
    }
}