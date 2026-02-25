package com.TETOSOFT.tilegame.sprites;

import java.lang.reflect.Constructor;
import com.TETOSOFT.core.GameConstants;
import com.TETOSOFT.graphics.Animation;
import com.TETOSOFT.graphics.Sprite;

/**
 * A Sprite affected by gravity that can move and die.
 * Subclasses supply four directional/death animations and override
 * {@link #getMaxSpeed()} and {@link #isFlying()} as needed.
 */
public abstract class Creature extends Sprite {

    public static final int STATE_NORMAL = 0;
    public static final int STATE_DYING  = 1;
    public static final int STATE_DEAD   = 2;

    private final Animation left;
    private final Animation right;
    private final Animation deadLeft;
    private final Animation deadRight;

    private int  state;
    private long stateTime;

    public Creature(Animation left, Animation right,
                    Animation deadLeft, Animation deadRight) {
        super(right);
        this.left      = left;
        this.right     = right;
        this.deadLeft  = deadLeft;
        this.deadRight = deadRight;
        state = STATE_NORMAL;
    }

    protected Animation getLeftAnim()     { return left;      }
    protected Animation getRightAnim()    { return right;     }
    protected Animation getDeadLeftAnim() { return deadLeft;  }
    protected Animation getDeadRightAnim(){ return deadRight; }

    public Object clone() {
        Constructor<?> ctor = getClass().getConstructors()[0];
        try {
            return ctor.newInstance(
                    left.clone(), right.clone(), deadLeft.clone(), deadRight.clone());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // -------------------------------------------------------------------------
    // State
    // -------------------------------------------------------------------------

    public int getState() { return state; }

    public void setState(int newState) {
        if (this.state == newState) return;
        this.state = newState;
        stateTime  = 0;
        if (newState == STATE_DYING) {
            setVelocityX(0);
            setVelocityY(0);
        }
    }

    public boolean isAlive()  { return state == STATE_NORMAL; }
    public boolean isFlying() { return false; }

    // -------------------------------------------------------------------------
    // Speed (subclasses override)
    // -------------------------------------------------------------------------

    public float getMaxSpeed() { return 0; }

    // -------------------------------------------------------------------------
    // Collision callbacks
    // -------------------------------------------------------------------------

    /** Called by PhysicsSystem when the creature hits a tile horizontally. */
    public void collideHorizontal() { setVelocityX(-getVelocityX()); }

    /** Called by PhysicsSystem when the creature hits a tile vertically. */
    public void collideVertical()   { setVelocityY(0); }

    /**
     * Called by TileMapDrawer the first time the creature enters the screen.
     * By default starts the creature moving left.
     */
    public void wakeUp() {
        if (state == STATE_NORMAL && getVelocityX() == 0) {
            setVelocityX(-getMaxSpeed());
        }
    }

    // -------------------------------------------------------------------------
    // Update
    // -------------------------------------------------------------------------

    @Override
    public void update(long elapsedTime) {
        // 1. Choose the right animation
        Animation target = anim;
        if (getVelocityX() < 0) target = left;
        else if (getVelocityX() > 0) target = right;

        if (state == STATE_DYING) {
            target = (target == left) ? deadLeft : deadRight;
        }

        if (anim != target) {
            anim = target;
            anim.start();
        }

        // 2. Advance frames or lock to a specific frame.
        //    Animation layout: 0-2 = walk, 3 = idle, 4 = jump
        if (state == STATE_NORMAL) {
            if (getVelocityY() != 0) {
                anim.setCurrFrame(4);              // airborne → jump frame
            } else if (getVelocityX() == 0) {
                anim.setCurrFrame(3);              // standing → idle frame
            } else {
                anim.updateWalkCycle(elapsedTime); // moving   → advance frames 0-2 only
            }
        } else {
            anim.update(elapsedTime);
        }

        // 3. Death timer
        stateTime += elapsedTime;
        if (state == STATE_DYING && stateTime >= GameConstants.CREATURE_DIE_TIME) {
            setState(STATE_DEAD);
        }
    }
}
