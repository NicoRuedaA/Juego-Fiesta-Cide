package com.TETOSOFT.tilegame;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.TETOSOFT.graphics.Sprite;
import com.TETOSOFT.tilegame.sprites.Creature;
import com.TETOSOFT.tilegame.sprites.Player;

/**
 * Renders a {@link TileMap} — background, tiles, and sprites — with
 * horizontal parallax scrolling centred on the player.
 *
 * El fondo parallax se compone de N capas ordenadas de más lejana (índice 0)
 * a más cercana (último índice). Cada capa tiene un factor de velocidad entre
 * 0.0 (estática) y 1.0 (se mueve igual que el mapa).
 */
public class TileMapDrawer {

    private static final int TILE_SIZE      = 64;
    private static final int TILE_SIZE_BITS = 6;

    // -------------------------------------------------------------------------
    // Capa de parallax
    // -------------------------------------------------------------------------

    public static class ParallaxLayer {
        final Image image;
        /** 0.0 = no se mueve, 1.0 = se mueve igual que el mapa. */
        final float speed;

        public ParallaxLayer(Image image, float speed) {
            this.image = image;
            this.speed = speed;
        }
    }

    private final List<ParallaxLayer> layers = new ArrayList<>();

    /** Añade una capa de parallax. Llamar en orden: fondo → primer plano. */
    public void addParallaxLayer(Image image, float speed) {
        layers.add(new ParallaxLayer(image, speed));
    }

    /** Elimina todas las capas (útil al cambiar de nivel). */
    public void clearParallaxLayers() {
        layers.clear();
    }

    // -------------------------------------------------------------------------
    // Coordinate helpers
    // -------------------------------------------------------------------------

    public static int pixelsToTiles(float pixels) { return pixelsToTiles(Math.round(pixels)); }
    public static int pixelsToTiles(int pixels)   { return pixels >> TILE_SIZE_BITS; }
    public static int tilesToPixels(int numTiles)  { return numTiles << TILE_SIZE_BITS; }

    // -------------------------------------------------------------------------
    // Draw
    // -------------------------------------------------------------------------

    public void draw(Graphics2D g, TileMap map, int screenWidth, int screenHeight) {
        Sprite player   = map.getPlayer();
        int    mapWidth = tilesToPixels(map.getWidth());
        int    mapHeight = tilesToPixels(map.getHeight());

        int offsetX = screenWidth / 2 - Math.round(player.getX()) - TILE_SIZE;
        offsetX = Math.min(offsetX, 0);
        offsetX = Math.max(offsetX, screenWidth - mapWidth);

        // El mapa siempre se ancla al fondo de la pantalla
        int offsetY = screenHeight - mapHeight;

        drawBackground(g, screenWidth, screenHeight, mapWidth, offsetX);
        drawTiles(g, map, screenWidth, offsetX, offsetY);
        drawSprites(g, map, screenWidth, offsetX, offsetY);
        drawPlayer(g, player, offsetX, offsetY);
    }

    // -------------------------------------------------------------------------
    // Background parallax
    // -------------------------------------------------------------------------

    private void drawBackground(Graphics2D g, int sw, int sh, int mapWidth, int offsetX) {
        // Fondo negro si no hay capas o si ninguna cubre toda la pantalla
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, sw, sh);

        if (layers.isEmpty()) return;

        for (ParallaxLayer layer : layers) {
            drawLayer(g, layer, sw, sh, mapWidth, offsetX);
        }
    }

    /**
     * Dibuja una capa escalándola para cubrir toda la altura de pantalla
     * y repitiéndola horizontalmente si es necesario.
     */
    private void drawLayer(Graphics2D g, ParallaxLayer layer,
                           int sw, int sh, int mapWidth, int offsetX) {
        Image img = layer.image;
        int srcW  = img.getWidth(null);
        int srcH  = img.getHeight(null);

        if (srcW <= 0 || srcH <= 0) return;

        // Escalar para que cubra exactamente la altura de pantalla,
        // manteniendo proporción en el ancho
        int dstH = sh;
        int dstW = (int) ((float) srcW / srcH * dstH);
        if (dstW <= 0) dstW = sw;

        // Desplazamiento horizontal proporcional a la velocidad de la capa
        int scrollX = (int) (offsetX * layer.speed);

        // Primer X para cubrir desde el borde izquierdo
        int startX = scrollX % dstW;
        if (startX > 0) startX -= dstW;

        for (int x = startX; x < sw; x += dstW) {
            g.drawImage(img, x, 0, dstW, dstH, null);
        }
    }

    // -------------------------------------------------------------------------
    // Tiles, sprites, player
    // -------------------------------------------------------------------------

    private void drawTiles(Graphics2D g, TileMap map, int screenWidth, int offsetX, int offsetY) {
        int firstTileX = pixelsToTiles(-offsetX);
        int lastTileX  = firstTileX + pixelsToTiles(screenWidth) + 1;

        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = firstTileX; x <= lastTileX; x++) {
                Image image = map.getTile(x, y);
                if (image != null) {
                    g.drawImage(image, tilesToPixels(x) + offsetX, tilesToPixels(y) + offsetY, null);
                }
            }
        }
    }

    private void drawSprites(Graphics2D g, TileMap map, int screenWidth, int offsetX, int offsetY) {
        Iterator<Sprite> it = map.getSprites();
        while (it.hasNext()) {
            Sprite sprite = it.next();
            int x = Math.round(sprite.getX()) + offsetX;
            int y = Math.round(sprite.getY()) + offsetY;
            g.drawImage(sprite.getImage(), x, y, null);

            if (sprite instanceof Creature && x >= 0 && x < screenWidth) {
                ((Creature) sprite).wakeUp();
            }
        }
    }

    private void drawPlayer(Graphics2D g, Sprite playerSprite, int offsetX, int offsetY) {
        Player player = (Player) playerSprite;
        if (!player.isVisible()) return;
        g.drawImage(player.getImage(),
                Math.round(player.getX()) + offsetX,
                Math.round(player.getY()) + offsetY,
                null);
    }
}
