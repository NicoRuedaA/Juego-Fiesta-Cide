package com.TETOSOFT.tilegame.systems;

import java.awt.Point;

import com.TETOSOFT.core.GameConstants;
import com.TETOSOFT.tilegame.TileMap;
import com.TETOSOFT.tilegame.TileMapDrawer;
import com.TETOSOFT.tilegame.sprites.Creature;
import com.TETOSOFT.tilegame.sprites.Player;

/**
 * Applies gravity and moves creatures, resolving tile collisions.
 *
 * Gravity is asymmetric: falling is faster than rising, giving the
 * responsive Mario-style feel.
 */
public class PhysicsSystem {

    // -------------------------------------------------------------------------
    // Listener — notifica cuando el jugador golpea un tile por debajo
    // -------------------------------------------------------------------------

    public interface BlockHitListener {
        /** Llamado cuando {@code creature} golpea el tile (tx, ty) subiendo. */
        void onBlockHitFromBelow(Creature creature, int tx, int ty);
    }

    private BlockHitListener blockHitListener;

    public void setBlockHitListener(BlockHitListener l) { this.blockHitListener = l; }

    private final Point tileCache = new Point();

    /**
     * Updates a creature's position for one frame.
     *
     * @param creature    the creature to update
     * @param map         the tile map used for collision detection
     * @param elapsedTime milliseconds since the last frame
     * @return the creature's old Y position (used by CollisionSystem to decide
     *         whether a stomp kill is valid)
     */
    public float update(Creature creature, TileMap map, long elapsedTime) {

        // --- Gravity ---------------------------------------------------------
        if (!creature.isFlying()) {
            float gravity = creature.getVelocityY() > 0
                    ? GameConstants.GRAVITY_FALLING
                    : GameConstants.GRAVITY;
            creature.setVelocityY(creature.getVelocityY() + gravity * elapsedTime);
        }

        // --- Horizontal movement ---------------------------------------------
        float oldY = creature.getY();
        moveX(creature, map, elapsedTime);

        // --- Vertical movement -----------------------------------------------
        moveY(creature, map, elapsedTime);

        return oldY;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private void moveX(Creature creature, TileMap map, long elapsedTime) {
        float dx   = creature.getVelocityX();
        float newX = creature.getX() + dx * elapsedTime;

        Point tile = getTileCollision(creature, map, newX, creature.getY());
        if (tile == null) {
            creature.setX(newX);
        } else {
            if (dx > 0) {
                creature.setX(TileMapDrawer.tilesToPixels(tile.x) - creature.getWidth());
            } else if (dx < 0) {
                creature.setX(TileMapDrawer.tilesToPixels(tile.x + 1));
            }
            creature.collideHorizontal();
        }
    }

    private void moveY(Creature creature, TileMap map, long elapsedTime) {
        float dy   = creature.getVelocityY();
        float newY = creature.getY() + dy * elapsedTime;

        Point tile = getTileCollision(creature, map, creature.getX(), newY);
        if (tile == null) {
            creature.setY(newY);
        } else {
            if (dy > 0) {
                creature.setY(TileMapDrawer.tilesToPixels(tile.y) - creature.getHeight());
            } else if (dy < 0) {
                creature.setY(TileMapDrawer.tilesToPixels(tile.y + 1));
                // Golpe desde abajo — notificar para romper el bloque
                if (blockHitListener != null && creature instanceof Player) {
                    blockHitListener.onBlockHitFromBelow(creature, tile.x, tile.y);
                }
            }
            creature.collideVertical();
        }
    }

    /**
     * Returns the first solid tile that overlaps the creature's bounding box
     * at the given position, or {@code null} if the path is clear.
     */
    public Point getTileCollision(Creature creature, TileMap map, float newX, float newY) {
        float fromX = Math.min(creature.getX(), newX);
        float fromY = Math.min(creature.getY(), newY);
        float toX   = Math.max(creature.getX(), newX);
        float toY   = Math.max(creature.getY(), newY);

        int x0 = TileMapDrawer.pixelsToTiles(fromX);
        int y0 = TileMapDrawer.pixelsToTiles(fromY);
        int x1 = TileMapDrawer.pixelsToTiles(toX + creature.getWidth()  - 1);
        int y1 = TileMapDrawer.pixelsToTiles(toY + creature.getHeight() - 1);

        for (int x = x0; x <= x1; x++) {
            for (int y = y0; y <= y1; y++) {
                if (x < 0 || x >= map.getWidth() || map.getTile(x, y) != null) {
                    tileCache.setLocation(x, y);
                    return tileCache;
                }
            }
        }
        return null;
    }
}
