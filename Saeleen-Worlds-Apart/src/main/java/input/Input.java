package input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Input
 */
public class Input implements KeyListener {

    private static boolean[] lastKeys = new boolean[196];
    private static boolean[] currentKeys = new boolean[196];


    public static boolean getKey(int keyCode) {
        return currentKeys[keyCode];
    }

    public static boolean getKeyDown(int keyCode) {
        return currentKeys[keyCode] && !lastKeys[keyCode];
    }

    public static boolean getKeyUp(int keyCode) {
        return !currentKeys[keyCode] && lastKeys[keyCode];
    }

    public static void finishInput() {
        lastKeys = currentKeys.clone();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        currentKeys[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        currentKeys[e.getKeyCode()] = false;
    }

}