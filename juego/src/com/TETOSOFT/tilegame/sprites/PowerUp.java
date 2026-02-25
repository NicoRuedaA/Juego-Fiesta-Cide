package com.TETOSOFT.tilegame.sprites;

import java.lang.reflect.Constructor;
import com.TETOSOFT.graphics.Animation;
import com.TETOSOFT.graphics.Sprite;

/**
 * Base class for collectible items.
 * Concrete types are defined as inner classes so adding new power-ups
 * only requires a new inner class here.
 */
public abstract class PowerUp extends Sprite {

    public PowerUp(Animation anim) { super(anim); }

    @Override
    public Object clone() {
        Constructor<?> ctor = getClass().getConstructors()[0];
        try {
            return ctor.newInstance((Animation) anim.clone());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // -------------------------------------------------------------------------
    // Concrete types
    // -------------------------------------------------------------------------

    /** Collectible coin — gives the player points and eventually an extra life. */
    public static class Star extends PowerUp {
        public Star(Animation anim) { super(anim); }
    }

    /** Changes the background music. */
    public static class Music extends PowerUp {
        public Music(Animation anim) { super(anim); }
    }

    /** Advances the game to the next map. */
    public static class Goal extends PowerUp {
        public Goal(Animation anim) { super(anim); }
    }
}
