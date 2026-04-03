package com.TETOSOFT.tilegame.sprites;

import java.util.function.Supplier;

import com.TETOSOFT.graphics.Animation;
import com.TETOSOFT.graphics.Sprite;

/**
 * Variante de Grub que cada {@link #SPAWN_INTERVAL_MS} ms instancia un
 * {@link VariantFly} en su posición.
 *
 * El Fly a spawnear se obtiene a través de un {@code Supplier<Sprite>}
 * inyectado en el constructor — así SpawnerGrub no depende de SpriteFactory.
 *
 * GameEngine recoge los hijos pendientes cada frame con {@link #pollSpawn()}.
 */
public class SpawnerGrub extends Grub {

    public static final long SPAWN_INTERVAL_MS = 5000; // 5 segundos

    private final Supplier<Sprite> flySupplier;
    private long   spawnTimer   = 0;
    private Sprite pendingSpawn = null;

    public SpawnerGrub(Animation left, Animation right,
                       Animation deadLeft, Animation deadRight,
                       Supplier<Sprite> flySupplier) {
        super(left, right, deadLeft, deadRight);
        this.flySupplier = flySupplier;
    }

    @Override
    public Object clone() {
        return new SpawnerGrub(
                (Animation) getLeftAnim().clone(),
                (Animation) getRightAnim().clone(),
                (Animation) getDeadLeftAnim().clone(),
                (Animation) getDeadRightAnim().clone(),
                flySupplier);
    }

    @Override
    public void update(long elapsedTime) {
        super.update(elapsedTime);

        if (!isAlive()) return;

        spawnTimer += elapsedTime;
        if (spawnTimer >= SPAWN_INTERVAL_MS) {
            spawnTimer = 0;
            Sprite fly = flySupplier.get();
            fly.setX(getX());
            fly.setY(getY() - fly.getHeight()); // justo encima del SpawnerGrub
            pendingSpawn = fly;
        }
    }

    /**
     * Devuelve el VariantFly recién creado (si hay uno) y lo limpia.
     * GameEngine llama esto cada frame y añade el resultado al TileMap.
     */
    public Sprite pollSpawn() {
        Sprite s = pendingSpawn;
        pendingSpawn = null;
        return s;
    }
}
