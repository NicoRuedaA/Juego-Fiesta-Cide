package com.TETOSOFT.input;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 * Maps keyboard and mouse events to {@link GameAction} objects.
 */
public class InputManager implements KeyListener, MouseListener,
        MouseMotionListener, MouseWheelListener {

    public static final Cursor INVISIBLE_CURSOR =
            Toolkit.getDefaultToolkit().createCustomCursor(
                    Toolkit.getDefaultToolkit().getImage(""),
                    new Point(0, 0),
                    "invisible");

    public static final int MOUSE_MOVE_LEFT   = 0;
    public static final int MOUSE_MOVE_RIGHT  = 1;
    public static final int MOUSE_MOVE_UP     = 2;
    public static final int MOUSE_MOVE_DOWN   = 3;
    public static final int MOUSE_WHEEL_UP    = 4;
    public static final int MOUSE_WHEEL_DOWN  = 5;
    public static final int MOUSE_BUTTON_1    = 6;
    public static final int MOUSE_BUTTON_2    = 7;
    public static final int MOUSE_BUTTON_3    = 8;

    private static final int NUM_MOUSE_CODES = 9;
    private static final int NUM_KEY_CODES   = 600;

    private final GameAction[] keyActions   = new GameAction[NUM_KEY_CODES];
    private final GameAction[] mouseActions = new GameAction[NUM_MOUSE_CODES];

    private final Point mouseLocation  = new Point();
    private final Point centerLocation = new Point();
    private final Component comp;
    private Robot   robot;
    private boolean isRecentering;

    public InputManager(Component comp) {
        this.comp = comp;
        comp.addKeyListener(this);
        comp.addMouseListener(this);
        comp.addMouseMotionListener(this);
        comp.addMouseWheelListener(this);
        comp.setFocusTraversalKeysEnabled(false);
    }

    public void setCursor(Cursor cursor)              { comp.setCursor(cursor); }
    public void mapToKey(GameAction action, int code) { keyActions[code] = action; }
    public void mapToMouse(GameAction action, int mc) { mouseActions[mc] = action; }

    public void setRelativeMouseMode(boolean mode) {
        if (mode == isRelativeMouseMode()) return;
        if (mode) {
            try { robot = new Robot(); recenterMouse(); }
            catch (AWTException ex) { robot = null; }
        } else {
            robot = null;
        }
    }

    public boolean isRelativeMouseMode() { return robot != null; }

    public void clearMap(GameAction action) {
        for (int i = 0; i < keyActions.length;   i++) if (keyActions[i]   == action) keyActions[i]   = null;
        for (int i = 0; i < mouseActions.length; i++) if (mouseActions[i] == action) mouseActions[i] = null;
        action.reset();
    }

    public List<String> getMaps(GameAction action) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < keyActions.length;   i++) if (keyActions[i]   == action) list.add(getKeyName(i));
        for (int i = 0; i < mouseActions.length; i++) if (mouseActions[i] == action) list.add(getMouseName(i));
        return list;
    }

    public void resetAllGameActions() {
        for (GameAction a : keyActions)   if (a != null) a.reset();
        for (GameAction a : mouseActions) if (a != null) a.reset();
    }

    public int getMouseX() { return mouseLocation.x; }
    public int getMouseY() { return mouseLocation.y; }

    public static String getKeyName(int keyCode)     { return KeyEvent.getKeyText(keyCode); }
    public static String getMouseName(int mouseCode) {
        switch (mouseCode) {
            case MOUSE_MOVE_LEFT:  return "Mouse Left";
            case MOUSE_MOVE_RIGHT: return "Mouse Right";
            case MOUSE_MOVE_UP:    return "Mouse Up";
            case MOUSE_MOVE_DOWN:  return "Mouse Down";
            case MOUSE_WHEEL_UP:   return "Mouse Wheel Up";
            case MOUSE_WHEEL_DOWN: return "Mouse Wheel Down";
            case MOUSE_BUTTON_1:   return "Mouse Button 1";
            case MOUSE_BUTTON_2:   return "Mouse Button 2";
            case MOUSE_BUTTON_3:   return "Mouse Button 3";
            default:               return "Unknown mouse code " + mouseCode;
        }
    }

    public static int getMouseButtonCode(MouseEvent e) {
        switch (e.getButton()) {
            case MouseEvent.BUTTON1: return MOUSE_BUTTON_1;
            case MouseEvent.BUTTON2: return MOUSE_BUTTON_2;
            case MouseEvent.BUTTON3: return MOUSE_BUTTON_3;
            default:                 return -1;
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private synchronized void recenterMouse() {
        if (robot != null && comp.isShowing()) {
            centerLocation.x = comp.getWidth()  / 2;
            centerLocation.y = comp.getHeight() / 2;
            SwingUtilities.convertPointToScreen(centerLocation, comp);
            isRecentering = true;
            robot.mouseMove(centerLocation.x, centerLocation.y);
        }
    }

    private GameAction getKeyAction(KeyEvent e) {
        int code = e.getKeyCode();
        return (code < keyActions.length) ? keyActions[code] : null;
    }

    private GameAction getMouseButtonAction(MouseEvent e) {
        int mc = getMouseButtonCode(e);
        return (mc != -1) ? mouseActions[mc] : null;
    }

    private void mouseHelper(int codeNeg, int codePos, int amount) {
        GameAction action = mouseActions[amount < 0 ? codeNeg : codePos];
        if (action != null) { action.press(Math.abs(amount)); action.release(); }
    }

    // -------------------------------------------------------------------------
    // Listener implementations
    // -------------------------------------------------------------------------

    public void keyPressed(KeyEvent e)  { GameAction a = getKeyAction(e); if (a != null) a.press();   e.consume(); }
    public void keyReleased(KeyEvent e) { GameAction a = getKeyAction(e); if (a != null) a.release(); e.consume(); }
    public void keyTyped(KeyEvent e)    { e.consume(); }

    public void mousePressed(MouseEvent e)  { GameAction a = getMouseButtonAction(e); if (a != null) a.press();   }
    public void mouseReleased(MouseEvent e) { GameAction a = getMouseButtonAction(e); if (a != null) a.release(); }
    public void mouseClicked(MouseEvent e)  {}
    public void mouseEntered(MouseEvent e)  { mouseMoved(e); }
    public void mouseExited(MouseEvent e)   { mouseMoved(e); }
    public void mouseDragged(MouseEvent e)  { mouseMoved(e); }

    public synchronized void mouseMoved(MouseEvent e) {
        if (isRecentering && centerLocation.x == e.getX() && centerLocation.y == e.getY()) {
            isRecentering = false;
        } else {
            mouseHelper(MOUSE_MOVE_LEFT,  MOUSE_MOVE_RIGHT, e.getX() - mouseLocation.x);
            mouseHelper(MOUSE_MOVE_UP,    MOUSE_MOVE_DOWN,  e.getY() - mouseLocation.y);
            if (isRelativeMouseMode()) recenterMouse();
        }
        mouseLocation.x = e.getX();
        mouseLocation.y = e.getY();
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        mouseHelper(MOUSE_WHEEL_UP, MOUSE_WHEEL_DOWN, e.getWheelRotation());
    }
}
