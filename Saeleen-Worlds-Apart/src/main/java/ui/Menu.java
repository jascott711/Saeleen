package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

import game.App;
import graphics.Renderer;
import input.Input;
import world.SaveGame;
import world.World;

/**
 * Menu
 * start menu with new game, continue, load games, and settings
 */
public class Menu extends UIComponent {

    private static final int SCREEN_MAIN = 0;
    private static final int SCREEN_LOAD = 1;
    private static final int SCREEN_SETTINGS = 2;
    private static final int SCREEN_PAUSE = 3;
    private static final int SCREEN_SAVE = 4;
    private static final int SCREEN_SAVE_CONFIRM = 5;
    private static final int SCREEN_CONTROLS = 6;

    private static final String[] MAIN_OPTIONS = { "New Game", "Continue", "Load Games", "Settings", "Quit" };
    private static final String[] LOAD_OPTIONS = { "Slot 1", "Slot 2", "Slot 3", "Back" };
    private static final String[] SETTINGS_OPTIONS = { "Back" };
    private static final String[] PAUSE_OPTIONS = { "Resume", "Controls", "Load Game", "Save Game", "Quit" };
    private static final String[] SAVE_OPTIONS = { "Slot 1", "Slot 2", "Slot 3", "Back" };
    private static final String[] SAVE_CONFIRM_OPTIONS = { "Save", "Save and Quit", "Cancel" };
    private static final String[] CONTROLS_OPTIONS = { "Back" };
    private static final String[] CONTROLS_LIST = {
        "ESC - Menu",
        "W A S D - Move",
        "Hold Shift - Run",
        "L - Attack",
        "O - Prev Attack",
        "P - Next Attack",
        "1/6 - Use Item"
    };

    private int screen = SCREEN_MAIN;
    private int selection = 0;
    private int pendingSlot = 0;
    private int returnScreen = SCREEN_MAIN;

    private String[] getOptions() {
        if (screen == SCREEN_LOAD) return LOAD_OPTIONS;
        if (screen == SCREEN_SETTINGS) return SETTINGS_OPTIONS;
        if (screen == SCREEN_PAUSE) return PAUSE_OPTIONS;
        if (screen == SCREEN_SAVE) return SAVE_OPTIONS;
        if (screen == SCREEN_SAVE_CONFIRM) return SAVE_CONFIRM_OPTIONS;
        if (screen == SCREEN_CONTROLS) return CONTROLS_OPTIONS;
        return MAIN_OPTIONS;
    }

    public void openPause() {
        screen = SCREEN_PAUSE;
        selection = 0;
    }

    @Override
    public void update(float deltaTime) {
        String[] options = getOptions();

        if (Input.getKeyDown(KeyEvent.VK_UP) || Input.getKeyDown(KeyEvent.VK_W)) {
            selection--;
            if (selection < 0) selection = options.length - 1;
        } else if (Input.getKeyDown(KeyEvent.VK_DOWN) || Input.getKeyDown(KeyEvent.VK_S)) {
            selection++;
            if (selection >= options.length) selection = 0;
        } else if (Input.getKeyDown(KeyEvent.VK_ENTER) || Input.getKeyDown(KeyEvent.VK_SPACE)
                || Input.getKeyDown(KeyEvent.VK_RIGHT) || Input.getKeyDown(KeyEvent.VK_D)) {
            select();
        } else if (Input.getKeyDown(KeyEvent.VK_ESCAPE) || Input.getKeyDown(KeyEvent.VK_LEFT)
                || Input.getKeyDown(KeyEvent.VK_A)) {
            goBack();
        }
    }

    private void select() {
        if (screen == SCREEN_MAIN) {
            if (selection == 0) {
                App.startNewGame();
                World.inMenu = false;
            } else if (selection == 1) {
                int slot = SaveGame.lastSaveSlot();
                if (slot >= 0) {
                    App.loadGame(slot);
                    World.inMenu = false;
                }
            } else if (selection == 2) {
                returnScreen = SCREEN_MAIN;
                screen = SCREEN_LOAD;
                selection = 0;
            } else if (selection == 3) {
                screen = SCREEN_SETTINGS;
                selection = 0;
            } else if (selection == 4) {
                App.quit();
            }
        } else if (screen == SCREEN_LOAD) {
            if (selection == LOAD_OPTIONS.length - 1) {
                screen = returnScreen;
                selection = 0;
            } else if (!isDisabled(selection)) {
                App.loadGame(selection);
                World.inMenu = false;
            }
        } else if (screen == SCREEN_SETTINGS) {
            if (selection == SETTINGS_OPTIONS.length - 1) {
                screen = SCREEN_MAIN;
                selection = 0;
            }
        } else if (screen == SCREEN_PAUSE) {
            if (selection == 0) {
                World.inMenu = false;
            } else if (selection == 1) {
                screen = SCREEN_CONTROLS;
                selection = 0;
            } else if (selection == 2) {
                returnScreen = SCREEN_PAUSE;
                screen = SCREEN_LOAD;
                selection = 0;
            } else if (selection == 3) {
                screen = SCREEN_SAVE;
                selection = 0;
            } else if (selection == 4) {
                App.quit();
            }
        } else if (screen == SCREEN_CONTROLS) {
            screen = SCREEN_PAUSE;
            selection = 0;
        } else if (screen == SCREEN_SAVE) {
            if (selection == SAVE_OPTIONS.length - 1) {
                screen = SCREEN_PAUSE;
                selection = 0;
            } else {
                pendingSlot = selection;
                screen = SCREEN_SAVE_CONFIRM;
                selection = 0;
            }
        } else if (screen == SCREEN_SAVE_CONFIRM) {
            if (selection == 0) {
                SaveGame.save(pendingSlot);
                screen = SCREEN_PAUSE;
                selection = 0;
            } else if (selection == 1) {
                SaveGame.save(pendingSlot);
                App.quit();
            } else if (selection == 2) {
                screen = SCREEN_SAVE;
                selection = 0;
            }
        }
    }

    private void goBack() {
        if (screen == SCREEN_LOAD || screen == SCREEN_SETTINGS) {
            screen = returnScreen;
            selection = 0;
        } else if (screen == SCREEN_SAVE) {
            screen = SCREEN_PAUSE;
            selection = 0;
        } else if (screen == SCREEN_SAVE_CONFIRM) {
            screen = SCREEN_SAVE;
            selection = 0;
        } else if (screen == SCREEN_CONTROLS) {
            screen = SCREEN_PAUSE;
            selection = 0;
        } else if (screen == SCREEN_PAUSE) {
            World.inMenu = false;
        }
    }

    private boolean isDisabled(int optionIndex) {
        if (screen == SCREEN_MAIN) {
            return optionIndex == 1 && !SaveGame.hasAnySave();
        }
        if (screen == SCREEN_LOAD) {
            return optionIndex < LOAD_OPTIONS.length - 1 && !SaveGame.hasSave(optionIndex);
        }
        return false;
    }

    @Override
    public void render(Graphics g) {
        g.setColor(new Color(15, 25, 35));
        g.fillRect(0, 0, Renderer.gameWidth, Renderer.gameHeight);

        int centerX = Renderer.gameWidth / 2;

        String title = "Saeleen";
        g.setFont(new Font("Tahoma", Font.BOLD, 64));
        g.setColor(new Color(230, 240, 85));
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, centerX - titleWidth / 2, Renderer.gameHeight / 3);

        String subtitle = "Worlds Apart";
        g.setFont(new Font("Tahoma", Font.BOLD, 36));
        g.setColor(new Color(230, 240, 85));
        int subtitleWidth = g.getFontMetrics().stringWidth(subtitle);
        g.drawString(subtitle, centerX - subtitleWidth / 2, (Renderer.gameHeight / 3) + 36);

        if (screen == SCREEN_CONTROLS) {
            g.setFont(new Font("Tahoma", Font.BOLD, 32));
            g.setColor(new Color(230, 240, 85));
            String controlsTitle = "Controls";
            int controlsTitleWidth = g.getFontMetrics().stringWidth(controlsTitle);
            int titleBaseline = (Renderer.gameHeight / 3) + 90;
            g.drawString(controlsTitle, centerX - controlsTitleWidth / 2, titleBaseline);

            //place the list below the title and size the spacing so it never
            //overlaps the title or the hint at the bottom
            int listStart = titleBaseline + 40;
            int hintTop = Renderer.gameHeight - 56;
            int available = hintTop - listStart;
            int spacing = Math.min(36, available / CONTROLS_LIST.length);

            g.setFont(new Font("Tahoma", Font.PLAIN, 24));
            for (int i = 0; i < CONTROLS_LIST.length; i++) {
                g.setColor(Color.WHITE);
                int textWidth = g.getFontMetrics().stringWidth(CONTROLS_LIST[i]);
                g.drawString(CONTROLS_LIST[i], centerX - textWidth / 2, listStart + i * spacing);
            }

            g.setFont(new Font("Tahoma", Font.PLAIN, 16));
            g.setColor(Color.GRAY);
            String hint = "Press ESC to go back";
            int hintWidth = g.getFontMetrics().stringWidth(hint);
            g.drawString(hint, centerX - hintWidth / 2, Renderer.gameHeight - 40);
            return;
        }

        String[] options = getOptions();
        int optionHeight = 40;
        int startY = Renderer.gameHeight / 2 - (options.length / 2) * optionHeight + 20;

        if (screen == SCREEN_SAVE_CONFIRM) {
            String prompt = "Save game to " + SAVE_OPTIONS[pendingSlot] + "?";
            g.setFont(new Font("Tahoma", Font.PLAIN, 22));
            g.setColor(Color.WHITE);
            int promptWidth = g.getFontMetrics().stringWidth(prompt);
            g.drawString(prompt, centerX - promptWidth / 2, startY - 50);
        }

        g.setFont(new Font("Tahoma", Font.PLAIN, 28));
        for (int i = 0; i < options.length; i++) {
            int y = startY + i * optionHeight;

            boolean selected = i == selection;
            boolean disabled = isDisabled(i);

            if (selected && !disabled) {
                g.setColor(new Color(230, 240, 85));
                g.fillRect(centerX - 150, y - 26, 300, 34);
                g.setColor(new Color(25, 25, 25));
            } else if (selected) {
                g.setColor(new Color(15, 25, 35));
                g.fillRect(centerX - 150, y - 26, 300, 34);
                g.setColor(new Color(230, 240, 85));
                g.drawRect(centerX - 150, y - 26, 299, 33);
                g.setColor(Color.DARK_GRAY);
            } else if (disabled) {
                g.setColor(Color.DARK_GRAY);
            } else {
                g.setColor(Color.WHITE);
            }

            int textWidth = g.getFontMetrics().stringWidth(options[i]);
            g.drawString(options[i], centerX - textWidth / 2, y);
        }

        g.setFont(new Font("Tahoma", Font.PLAIN, 16));
        g.setColor(Color.GRAY);
        String hint = "W/S or Up/Down to navigate, Enter/Right/D to select, ESC/Left/A to go back";
        int hintWidth = g.getFontMetrics().stringWidth(hint);
        g.drawString(hint, centerX - hintWidth / 2, Renderer.gameHeight - 40);
    }
}
