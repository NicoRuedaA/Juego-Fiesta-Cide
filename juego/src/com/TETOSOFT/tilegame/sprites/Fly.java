package com.TETOSOFT.tilegame.sprites;

import com.TETOSOFT.graphics.Animation;

/** A slow-flying enemy. */
public class Fly extends Creature {

    public Fly(Animation left, Animation right,
               Animation deadLeft, Animation deadRight) {
        super(left, right, deadLeft, deadRight);
    }

    @Override public float   getMaxSpeed() { return 0.2f; }
    @Override public boolean isFlying()    { return isAlive(); }
}
