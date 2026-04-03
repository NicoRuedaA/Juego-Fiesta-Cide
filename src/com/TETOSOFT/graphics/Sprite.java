package com.TETOSOFT.graphics;

import java.awt.Image;

/**
 * A positioned, animated game object.
 */
public class Sprite {

    protected Animation anim;

    private float x, y;
    private float dx, dy;

    public Sprite(Animation anim) {
        this.anim = anim;
    }

    public void update(long elapsedTime) {
        x += dx * elapsedTime;
        y += dy * elapsedTime;
        anim.update(elapsedTime);
    }

    // --- Position ------------------------------------------------------------

    public float getX() { return x; }
    public float getY() { return y; }

    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }

    // --- Velocity ------------------------------------------------------------

    public float getVelocityX() { return dx; }
    public float getVelocityY() { return dy; }

    public void setVelocityX(float dx) { this.dx = dx; }
    public void setVelocityY(float dy) { this.dy = dy; }

    // --- Dimensions ----------------------------------------------------------

    public int getWidth()  { return anim.getImage().getWidth(null);  }
    public int getHeight() { return anim.getImage().getHeight(null); }

    // --- Rendering -----------------------------------------------------------

    public Image getImage() { return anim.getImage(); }

    public Object clone() {
        return new Sprite(anim);
    }
}
