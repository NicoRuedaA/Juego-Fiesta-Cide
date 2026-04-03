package com.TETOSOFT.tilegame.systems;

import java.awt.event.KeyEvent;
import javax.swing.JFrame;

import com.TETOSOFT.input.GameAction;
import com.TETOSOFT.input.InputManager;

/**
 * Gestiona el input de navegación en los menús (↑ ↓ ENTER ESC P).
 * Separado de PlayerController para no interferir con el input del juego.
 */
public class MenuController {

    private InputManager inputManager;

    private final GameAction up     = new GameAction("up",     GameAction.DETECT_INITAL_PRESS_ONLY);
    private final GameAction down   = new GameAction("down",   GameAction.DETECT_INITAL_PRESS_ONLY);
    private final GameAction enter  = new GameAction("enter",  GameAction.DETECT_INITAL_PRESS_ONLY);
    private final GameAction pause  = new GameAction("pause",  GameAction.DETECT_INITAL_PRESS_ONLY);
    private final GameAction escape = new GameAction("escape", GameAction.DETECT_INITAL_PRESS_ONLY);

    public void init(JFrame window) {
        inputManager = new InputManager(window);
        inputManager.mapToKey(up,     KeyEvent.VK_UP);
        inputManager.mapToKey(down,   KeyEvent.VK_DOWN);
        inputManager.mapToKey(enter,  KeyEvent.VK_ENTER);
        inputManager.mapToKey(pause,  KeyEvent.VK_P);
        inputManager.mapToKey(escape, KeyEvent.VK_ESCAPE);
    }

    public boolean isUpPressed()     { return up.isPressed();     }
    public boolean isDownPressed()   { return down.isPressed();   }
    public boolean isEnterPressed()  { return enter.isPressed();  }
    public boolean isPausePressed()  { return pause.isPressed();  }
    public boolean isEscapePressed() { return escape.isPressed(); }
}
