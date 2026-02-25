package com.TETOSOFT.tilegame.sprites;

import com.TETOSOFT.graphics.Animation;

/** A slow ground-crawling enemy. */
public class Grub extends Creature {

    public Grub(Animation left, Animation right,
                Animation deadLeft, Animation deadRight) {
        super(left, right, deadLeft, deadRight);
    }

    @Override public float getMaxSpeed() { return 0.05f; }
}
