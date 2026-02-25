package com.TETOSOFT.tilegame.systems;

import java.util.Iterator;

import com.TETOSOFT.core.GameConstants;
import com.TETOSOFT.graphics.Sprite;
import com.TETOSOFT.tilegame.TileMap;
import com.TETOSOFT.tilegame.sprites.Creature;
import com.TETOSOFT.tilegame.sprites.Player;
import com.TETOSOFT.tilegame.sprites.PowerUp;

/**
 * Detects and resolves collisions between the player and other sprites.
 *
 * Game-rule logic (scoring, lives, map transitions) is communicated back to
 * the caller via the {@link Listener} interface so this class stays decoupled
 * from {@link com.TETOSOFT.tilegame.GameEngine}.
 */
public class CollisionSystem {

    // -------------------------------------------------------------------------
    // Listener — implemented by GameEngine
    // -------------------------------------------------------------------------

    public interface Listener {
        void onCoinCollected();
        void onGoalReached();
        void onPlayerDied();
    }

    private final Listener listener;

    public CollisionSystem(Listener listener) {
        this.listener = listener;
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Checks the player against every other sprite.
     *
     * @param player  the player sprite
     * @param map     the current tile map
     * @param canKill true when the player is moving downward (stomp detection)
     */
    public void checkPlayerCollisions(Player player, TileMap map, boolean isFalling) {
        if (!player.isAlive()) return;
        if (player.isInvincible()) return; // periodo de gracia activo

        Sprite hit = getFirstCollision(player, map);

        if (hit instanceof PowerUp) {
            handlePowerUp(player, map, (PowerUp) hit);
        } else if (hit instanceof Creature) {
            Creature enemy = (Creature) hit;
            float playerFeet  = player.getY() + player.getHeight();
            float enemyCenter = enemy.getY() + enemy.getHeight() / 2f;
            boolean canKill = isFalling && playerFeet <= enemyCenter + 8;
            handleCreatureCollision(player, enemy, canKill);
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private void handlePowerUp(Player player, TileMap map, PowerUp powerUp) {
        map.removeSprite(powerUp);
        if (powerUp instanceof PowerUp.Star) {
            listener.onCoinCollected();
        } else if (powerUp instanceof PowerUp.Goal) {
            listener.onGoalReached();
        }
        // PowerUp.Music: handled elsewhere (sound system)
    }

    private void handleCreatureCollision(Player player, Creature enemy, boolean canKill) {
        if (canKill) {
            enemy.setState(Creature.STATE_DYING);
            player.setY(enemy.getY() - player.getHeight());
            player.bounce();
        } else {
            float knockbackX = player.getX() < enemy.getX() ? -0.4f : 0.4f;
            player.triggerInvincibility();
            player.setVelocityX(knockbackX);
            player.setVelocityY(-0.5f);
            listener.onPlayerDied();
        }
    }

    /**
     * Returns the first non-self, alive sprite that overlaps {@code sprite},
     * or {@code null} if there is no collision.
     */
    private Sprite getFirstCollision(Sprite sprite, TileMap map) {
        Iterator<Sprite> it = map.getSprites();
        while (it.hasNext()) {
            Sprite other = it.next();
            if (overlaps(sprite, other)) return other;
        }
        return null;
    }

    /** AABB overlap test. Returns false for dead creatures and self-checks. */
    private boolean overlaps(Sprite a, Sprite b) {
        if (a == b) return false;
        if (a instanceof Creature && !((Creature) a).isAlive()) return false;
        if (b instanceof Creature && !((Creature) b).isAlive()) return false;

        int ax = Math.round(a.getX()), ay = Math.round(a.getY());
        int bx = Math.round(b.getX()), by = Math.round(b.getY());

        return ax < bx + b.getWidth()  &&
               bx < ax + a.getWidth()  &&
               ay < by + b.getHeight() &&
               by < ay + a.getHeight();
    }
}
