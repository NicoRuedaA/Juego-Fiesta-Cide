package com.TETOSOFT.tilegame.systems;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Dibuja las pantallas de menú principal, game over y pausa.
 * No gestiona input — solo renderiza según el estado que le pase GameEngine.
 */
public class MenuRenderer {

    // -------------------------------------------------------------------------
    // Paleta
    // -------------------------------------------------------------------------
    private static final Color BG_OVERLAY   = new Color(0, 0, 0, 200);
    private static final Color TITLE_COLOR  = new Color(255, 220, 50);
    private static final Color BTN_NORMAL   = new Color(40, 40, 60, 220);
    private static final Color BTN_HOVER    = new Color(80, 80, 140, 240);
    private static final Color BTN_BORDER   = new Color(255, 255, 255, 80);
    private static final Color BTN_TEXT     = Color.WHITE;
    private static final Color PAUSE_COLOR  = new Color(255, 255, 255, 220);

    private static final int BTN_W      = 300;
    private static final int BTN_H      = 54;
    private static final int BTN_GAP    = 18;
    private static final int BTN_RADIUS = 12;

    // -------------------------------------------------------------------------
    // Menú principal
    // -------------------------------------------------------------------------

    public void drawMainMenu(Graphics2D g, int sw, int sh, int hoveredButton) {
        enableAA(g);

        // Fondo oscuro
        g.setColor(BG_OVERLAY);
        g.fillRect(0, 0, sw, sh);

        // Título
        g.setFont(new Font("Monospaced", Font.BOLD, 64));
        drawCenteredText(g, "SUPER MIRO", sw, sh / 2 - 120, TITLE_COLOR);

        g.setFont(new Font("Monospaced", Font.BOLD, 20));
        drawCenteredText(g, "GAME", sw, sh / 2 - 60, new Color(200, 200, 200));

        // Botones: 0=Jugar  1=Ajustes  2=Salir
        String[] labels = {"▶  JUGAR", "⚙  AJUSTES", "✕  SALIR"};
        int startY = sh / 2;
        for (int i = 0; i < labels.length; i++) {
            int x = (sw - BTN_W) / 2;
            int y = startY + i * (BTN_H + BTN_GAP);
            drawButton(g, x, y, BTN_W, BTN_H, labels[i], hoveredButton == i);
        }

        // Hint teclado
        g.setFont(new Font("Monospaced", Font.PLAIN, 13));
        drawCenteredText(g, "↑ ↓ para navegar  ·  ENTER para seleccionar",
                sw, sh - 40, new Color(160, 160, 160));
    }

    // -------------------------------------------------------------------------
    // Game Over
    // -------------------------------------------------------------------------

    public void drawGameOver(Graphics2D g, int sw, int sh, int hoveredButton) {
        enableAA(g);

        g.setColor(BG_OVERLAY);
        g.fillRect(0, 0, sw, sh);

        // Título
        g.setFont(new Font("Monospaced", Font.BOLD, 72));
        drawCenteredText(g, "GAME OVER", sw, sh / 2 - 100, new Color(220, 50, 50));

        // Botones: 0=Volver a jugar  1=Menú principal
        String[] labels = {"↺  VOLVER A JUGAR", "⌂  MENÚ PRINCIPAL"};
        int startY = sh / 2;
        for (int i = 0; i < labels.length; i++) {
            int x = (sw - BTN_W) / 2;
            int y = startY + i * (BTN_H + BTN_GAP);
            drawButton(g, x, y, BTN_W, BTN_H, labels[i], hoveredButton == i);
        }

        g.setFont(new Font("Monospaced", Font.PLAIN, 13));
        drawCenteredText(g, "↑ ↓ para navegar  ·  ENTER para seleccionar",
                sw, sh - 40, new Color(160, 160, 160));
    }

    // -------------------------------------------------------------------------
    // Pausa
    // -------------------------------------------------------------------------

    public void drawPause(Graphics2D g, int sw, int sh) {
        enableAA(g);

        // Overlay semitransparente sobre el juego
        g.setColor(new Color(0, 0, 0, 120));
        g.fillRect(0, 0, sw, sh);

        // Caja central
        int bw = 320, bh = 100;
        int bx = (sw - bw) / 2, by = (sh - bh) / 2;
        g.setColor(new Color(20, 20, 40, 220));
        g.fill(new RoundRectangle2D.Float(bx, by, bw, bh, 16, 16));
        g.setColor(BTN_BORDER);
        g.draw(new RoundRectangle2D.Float(bx, by, bw, bh, 16, 16));

        g.setFont(new Font("Monospaced", Font.BOLD, 42));
        drawCenteredText(g, "PAUSA", sw, by + 58, PAUSE_COLOR);

        g.setFont(new Font("Monospaced", Font.PLAIN, 14));
        drawCenteredText(g, "Pulsa P para continuar", sw, by + 84, new Color(160, 160, 160));
    }

    // -------------------------------------------------------------------------
    // Primitivas
    // -------------------------------------------------------------------------

    private void drawButton(Graphics2D g, int x, int y, int w, int h,
                            String label, boolean hovered) {
        RoundRectangle2D rect = new RoundRectangle2D.Float(x, y, w, h, BTN_RADIUS, BTN_RADIUS);

        g.setColor(hovered ? BTN_HOVER : BTN_NORMAL);
        g.fill(rect);

        g.setColor(hovered ? Color.WHITE : BTN_BORDER);
        g.draw(rect);

        g.setFont(new Font("Monospaced", Font.BOLD, 18));
        g.setColor(BTN_TEXT);

        FontMetrics fm = g.getFontMetrics();
        int tx = x + (w - fm.stringWidth(label)) / 2;
        int ty = y + (h + fm.getAscent() - fm.getDescent()) / 2;
        g.drawString(label, tx, ty);
    }

    private void drawCenteredText(Graphics2D g, String text, int sw, int y, Color color) {
        g.setColor(color);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, (sw - fm.stringWidth(text)) / 2, y);
    }

    private void enableAA(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
}
