package com.TETOSOFT.tilegame.sprites;

import com.TETOSOFT.graphics.Animation;

/**
 * Variante de Fly — más rápida y agresiva.
 * Spawnea desde SpawnerGrub cada 5 segundos.
 */
public class VariantFly extends Fly {

    public VariantFly(Animation left, Animation right,
                      Animation deadLeft, Animation deadRight) {
        super(left, right, deadLeft, deadRight);
    }

    @Override public float getMaxSpeed() { return 0.35f; } // más rápida que Fly normal (0.2f)
}
