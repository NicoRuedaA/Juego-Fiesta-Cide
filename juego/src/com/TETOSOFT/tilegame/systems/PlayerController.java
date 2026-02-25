package com.TETOSOFT.tilegame.systems;

import java.awt.event.KeyEvent;
import javax.swing.JFrame;

import com.TETOSOFT.input.GameAction;
import com.TETOSOFT.input.InputManager;
import com.TETOSOFT.tilegame.sprites.Player;

/**
 * Reads player input each frame and applies it to the {@link Player} sprite.
 *
 * Key bindings are defined once in {@link #init(JFrame)} and can be changed
 * without touching any other class.
 */
public class PlayerController {

    private InputManager inputManager;

    private final GameAction moveLeft    = new GameAction("moveLeft");
    private final GameAction moveRight   = new GameAction("moveRight");
    private final GameAction jump        = new GameAction("jump");
    private final GameAction sprint      = new GameAction("sprint");
    private final GameAction duck        = new GameAction("duck");
    private final GameAction exit        = new GameAction("exit",        GameAction.DETECT_INITAL_PRESS_ONLY);
    private final GameAction toggleDebug = new GameAction("toggleDebug", GameAction.DETECT_INITAL_PRESS_ONLY);

    /**
     * Attaches the input manager to the game window and registers key bindings.
     */
    public void init(JFrame window) {
        inputManager = new InputManager(window);
        inputManager.setCursor(InputManager.INVISIBLE_CURSOR);

        inputManager.mapToKey(moveLeft,    KeyEvent.VK_LEFT);
        inputManager.mapToKey(moveRight,   KeyEvent.VK_RIGHT);
        inputManager.mapToKey(jump,        KeyEvent.VK_UP);
        inputManager.mapToKey(duck,        KeyEvent.VK_DOWN);
        inputManager.mapToKey(sprint,      KeyEvent.VK_SHIFT);
        inputManager.mapToKey(sprint,      KeyEvent.VK_Z);
        inputManager.mapToKey(exit,        KeyEvent.VK_ESCAPE);
        inputManager.mapToKey(toggleDebug, KeyEvent.VK_F1);
    }

    /** Returns true if the debug toggle was pressed this frame. */
    public boolean isDebugTogglePressed() {
        return toggleDebug.isPressed();
    }

    /**
     * Aplica el input del frame al jugador.
     *
     * @param elapsedTime ms desde el último frame (necesario para el boost de salto)
     * @return true si se pulsó exit
     */
    public boolean update(Player player, long elapsedTime) {
        if (!player.isAlive()) return exit.isPressed();

        player.setSprinting(sprint.isPressed());

        // Leer duck una sola vez — isPressed() consume el estado
        boolean isDucking = duck.isPressed();
        player.setDucking(isDucking);

        float vx = 0;
        // Agachado: no puede moverse a los lados
        if (!isDucking) {
            if (moveLeft.isPressed())  vx -= player.getMaxSpeed();
            if (moveRight.isPressed()) vx += player.getMaxSpeed();
        }
        player.setVelocityX(vx);

        if (jump.isPressed()) {
            player.jump(false);
            player.holdJump(elapsedTime);
        } else {
            player.releaseJump();
        }

        // Rebote + salto: si se pisa enemigo y se pulsa salto a la vez,
        // combinar ambas fuerzas en lugar del salto normal
        if (player.consumeBounce() && jump.isPressed()) {
            player.jumpFromBounce();
        }

        return exit.isPressed();
    }
}
