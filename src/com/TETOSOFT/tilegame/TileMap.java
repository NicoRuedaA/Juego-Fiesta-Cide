package com.TETOSOFT.tilegame;

import java.awt.Image;
import java.util.Iterator;
import java.util.LinkedList;

import com.TETOSOFT.graphics.Sprite;

/**
 * Stores the tile grid and the list of sprites for one level.
 * The player sprite is tracked separately from the rest.
 */
public class TileMap {

    private final Image[][]       tiles;
    private final LinkedList<Sprite> sprites = new LinkedList<>();
    private Sprite player;

    /**
     * @param width  map width  in tiles
     * @param height map height in tiles
     */
    public TileMap(int width, int height) {
        tiles = new Image[width][height];
    }

    // -------------------------------------------------------------------------
    // Dimensions
    // -------------------------------------------------------------------------

    public int getWidth()  { return tiles.length; }
    public int getHeight() { return tiles[0].length; }

    // -------------------------------------------------------------------------
    // Tiles
    // -------------------------------------------------------------------------

    /**
     * Returns the tile image at (x, y), or {@code null} if the position is
     * out of bounds or empty.
     */
    public Image getTile(int x, int y) {
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) return null;
        return tiles[x][y];
    }

    public void setTile(int x, int y, Image tile) {
        tiles[x][y] = tile;
    }

    /**
     * Elimina el tile en (x, y) — usado al romper bloques desde abajo.
     * Equivalente a setTile(x, y, null).
     */
    public void breakTile(int x, int y) {
        if (x >= 0 && x < getWidth() && y >= 0 && y < getHeight()) {
            tiles[x][y] = null;
        }
    }

    // -------------------------------------------------------------------------
    // Player
    // -------------------------------------------------------------------------

    public Sprite getPlayer()           { return player; }
    public void   setPlayer(Sprite p)   { this.player = p; }

    // -------------------------------------------------------------------------
    // Sprites
    // -------------------------------------------------------------------------

    public void addSprite(Sprite sprite)    { sprites.add(sprite); }
    public void removeSprite(Sprite sprite) { sprites.remove(sprite); }

    /** Returns an iterator over all non-player sprites. */
    public Iterator<Sprite> getSprites()    { return sprites.iterator(); }
}
