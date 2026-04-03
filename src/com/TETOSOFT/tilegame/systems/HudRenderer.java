package com.TETOSOFT.tilegame.systems;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Iterator;

import com.TETOSOFT.graphics.Sprite;
import com.TETOSOFT.tilegame.TileMap;
import com.TETOSOFT.tilegame.TileMapDrawer;
import com.TETOSOFT.tilegame.sprites.Player;

/**
 * Draws the heads-up display and optional debug hitboxes.
 *
 * Layout: panel izquierdo (vidas), panel central (monedas), panel derecho (nivel).
 * Estética arcade retro con fondo semitransparente y tipografía monoespaciada.
 */
public class HudRenderer {

    // -------------------------------------------------------------------------
    // Paleta
    // -------------------------------------------------------------------------
    private static final Color PANEL_BG      = new Color(0, 0, 0, 160);
    private static final Color PANEL_BORDER  = new Color(255, 255, 255, 60);
    private static final Color COLOR_LIVES   = new Color(255, 80,  80);   // rojo coral
    private static final Color COLOR_COINS   = new Color(255, 210, 50);   // amarillo dorado
    private static final Color COLOR_LEVEL   = new Color(80,  200, 255);  // azul cielo
    private static final Color COLOR_LABEL   = new Color(180, 180, 180);  // gris claro
    private static final Color COLOR_SPRINT  = new Color(255, 140, 0);    // naranja
    private static final Color COLOR_DEBUG   = new Color(0,   255, 200);  // cian

    private static final int PANEL_H        = 44;
    private static final int PANEL_RADIUS   = 8;
    private static final int PADDING        = 12;
    private static final int TOP            = 10;

    // -------------------------------------------------------------------------
    // Draw principal
    // -------------------------------------------------------------------------

    public void draw(Graphics2D g, Player player,
                     int lives, int coins, int currentMap, boolean debugHitboxes,
                     int screenW) {

        enableAntialiasing(g);

        // Tres paneles: vidas | monedas | nivel
        int panelW = 160;
        int gap    = 10;
        int totalW = panelW * 3 + gap * 2;
        int startX = (screenW - totalW) / 2;

        drawLivesPanel  (g, startX,                     TOP, panelW, PANEL_H, lives);
        drawCoinsPanel  (g, startX + panelW + gap,      TOP, panelW, PANEL_H, coins);
        drawLevelPanel  (g, startX + (panelW + gap) * 2, TOP, panelW, PANEL_H, currentMap);

        // Indicadores flotantes (sprint, debug)
        int floatingY = TOP + PANEL_H + 14;
        if (player.isSprinting()) {
            drawFloatingBadge(g, startX, floatingY, "▶▶ SPRINT", COLOR_SPRINT);
        }
        if (debugHitboxes) {
            drawFloatingBadge(g, startX + panelW + gap, floatingY, "HITBOX [F1]", COLOR_DEBUG);
        }
    }

    // -------------------------------------------------------------------------
    // Paneles individuales
    // -------------------------------------------------------------------------

    private void drawLivesPanel(Graphics2D g, int x, int y, int w, int h, int lives) {
        drawPanel(g, x, y, w, h);

        g.setFont(labelFont());
        g.setColor(COLOR_LABEL);
        g.drawString("VIDAS", x + PADDING, y + 16);

        // Corazones
        int heartX = x + PADDING;
        int heartY = y + 22;
        for (int i = 0; i < Math.min(lives, 6); i++) {
            drawHeart(g, heartX + i * 20, heartY,
                    i < lives ? COLOR_LIVES : new Color(80, 40, 40));
        }
        // Si hay más de 6 vidas, mostrar número
        if (lives > 6) {
            g.setFont(valueFont());
            g.setColor(COLOR_LIVES);
            g.drawString("×" + lives, x + PADDING, y + 36);
        }
    }

    private void drawCoinsPanel(Graphics2D g, int x, int y, int w, int h, int coins) {
        drawPanel(g, x, y, w, h);

        g.setFont(labelFont());
        g.setColor(COLOR_LABEL);
        g.drawString("MONEDAS", x + PADDING, y + 16);

        g.setFont(valueFont());
        g.setColor(COLOR_COINS);
        g.drawString("✦ " + String.format("%03d", coins), x + PADDING, y + 36);
    }

    private void drawLevelPanel(Graphics2D g, int x, int y, int w, int h, int level) {
        drawPanel(g, x, y, w, h);

        g.setFont(labelFont());
        g.setColor(COLOR_LABEL);
        g.drawString("NIVEL", x + PADDING, y + 16);

        g.setFont(bigValueFont());
        g.setColor(COLOR_LEVEL);
        g.drawString(String.format("%02d", level), x + PADDING, y + 38);
    }

    // -------------------------------------------------------------------------
    // Primitivas de dibujo
    // -------------------------------------------------------------------------

    private void drawPanel(Graphics2D g, int x, int y, int w, int h) {
        RoundRectangle2D rect = new RoundRectangle2D.Float(x, y, w, h, PANEL_RADIUS, PANEL_RADIUS);

        // Fondo semitransparente
        g.setColor(PANEL_BG);
        g.fill(rect);

        // Borde sutil
        g.setColor(PANEL_BORDER);
        g.draw(rect);
    }

    private void drawHeart(Graphics2D g, int x, int y, Color color) {
        g.setFont(new Font("Dialog", Font.PLAIN, 14));
        g.setColor(color);
        g.drawString("♥", x, y + 14);
    }

    private void drawFloatingBadge(Graphics2D g, int x, int y, String text, Color color) {
        FontMetrics fm = g.getFontMetrics(labelFont());
        int tw = fm.stringWidth(text);
        int bw = tw + PADDING * 2;
        int bh = 20;

        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 40));
        g.fillRoundRect(x, y, bw, bh, 6, 6);
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
        g.drawRoundRect(x, y, bw, bh, 6, 6);

        g.setFont(labelFont());
        g.setColor(color);
        g.drawString(text, x + PADDING, y + 14);
    }

    // -------------------------------------------------------------------------
    // Fuentes
    // -------------------------------------------------------------------------

    private Font labelFont()    { return new Font("Monospaced", Font.BOLD,  10); }
    private Font valueFont()    { return new Font("Monospaced", Font.BOLD,  16); }
    private Font bigValueFont() { return new Font("Monospaced", Font.BOLD,  22); }

    // -------------------------------------------------------------------------
    // Antialiasing
    // -------------------------------------------------------------------------

    private void enableAntialiasing(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,    RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    // -------------------------------------------------------------------------
    // Hitboxes debug
    // -------------------------------------------------------------------------

    public void drawHitboxes(Graphics2D g, TileMap map, int screenWidth, int screenHeight) {
        Sprite player  = map.getPlayer();
        int mapWidth   = TileMapDrawer.tilesToPixels(map.getWidth());

        int offsetX = screenWidth / 2 - Math.round(player.getX()) - 64;
        offsetX = Math.min(offsetX, 0);
        offsetX = Math.max(offsetX, screenWidth - mapWidth);
        int offsetY = screenHeight - TileMapDrawer.tilesToPixels(map.getHeight());

        drawBox(g, player, offsetX, offsetY, new Color(0, 255, 0, 160));

        Iterator<Sprite> it = map.getSprites();
        while (it.hasNext()) {
            drawBox(g, it.next(), offsetX, offsetY, new Color(255, 0, 0, 160));
        }
    }

    private void drawBox(Graphics2D g, Sprite sprite, int offsetX, int offsetY, Color color) {
        int x = Math.round(sprite.getX()) + offsetX;
        int y = Math.round(sprite.getY()) + offsetY;

        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 40));
        g.fillRect(x, y, sprite.getWidth(), sprite.getHeight());
        g.setColor(color);
        g.drawRect(x, y, sprite.getWidth(), sprite.getHeight());
    }
}
