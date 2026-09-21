package world;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import graphics.Renderer;
import input.Input;
import objects.Mob;
import objects.Player;
import objects.Sprite;
import objects.abilities.Slash;
import objects.impassable.Iob;
import objects.items.Coin;
import objects.items.Item;
import objects.npcs.Npc;
import ui.*;

/**
 * World
 */
public class World {
    public static World currentWorld = null;
    public static Player currentPlayer = new objects.Player(1000,1200);

    public static ui.Menu mainMenu = new ui.Menu();
    public static boolean inMenu = true;
    public static boolean showStats = false;

    public static float deathTimer = 0;

    private static long lastTime = System.nanoTime();

    public ArrayList<Sprite> sprites = new ArrayList<Sprite>();
    public ArrayList<Sprite> addSprites = new ArrayList<Sprite>();
    public ArrayList<Sprite> removeSprites = new ArrayList<Sprite>();

    public ArrayList<Npc> npcSprites = new ArrayList<Npc>();

    public ArrayList<Iob> iobSprites = new ArrayList<Iob>();

    public ArrayList<Item> itemSprites = new ArrayList<Item>();
    
    public ArrayList<Coin> coinSprites = new ArrayList<Coin>();

    public ArrayList<UIComponent> uicomponents = new ArrayList<UIComponent>();

    public static BufferedImage backdrop = null;
    private static int backdropX = 0;
    private static int backdropY = 0;
    
    public static void update() {
        float deltaTime = (System.nanoTime() - lastTime) / 1000000000.0f;
        lastTime = System.nanoTime();

        if (inMenu) {
            mainMenu.update(deltaTime);
            return;
        }

        //player death: show the death screen until the player presses enter, then return to the main menu
        if (currentPlayer.health <= 0 && deathTimer == 0) {
            deathTimer = 1;
        }
        if (deathTimer > 0) {
            if (Input.getKeyDown(KeyEvent.VK_ENTER) || Input.getKeyDown(KeyEvent.VK_SPACE)) {
                deathTimer = 0;
                mainMenu.resetToMain();
                inMenu = true;
            }
            return;
        }

        //TAB toggles the stats screen
        if (Input.getKeyDown(KeyEvent.VK_TAB)) {
            showStats = !showStats;
            return;
        }

        //while the stats screen is open the game is paused, ESC closes it
        if (showStats) {
            if (Input.getKeyDown(KeyEvent.VK_ESCAPE)) {
                showStats = false;
            }
            for (UIComponent uicomponent : currentWorld.uicomponents) {
                uicomponent.update(deltaTime);
            }
            return;
        }

        currentPlayer.update(deltaTime);

        for (Npc npcSprite : currentWorld.npcSprites) {
            npcSprite.update(deltaTime);
        }



        for (Sprite sprite : currentWorld.sprites) {
            sprite.update(deltaTime);
        }

        for (Sprite sprite : currentWorld.addSprites) {
            if (!currentWorld.sprites.contains(sprite)) {
                currentWorld.sprites.add(sprite);
            }
        }
        currentWorld.addSprites.clear();

        for (Sprite sprite : currentWorld.removeSprites) {
            if (currentWorld.sprites.contains(sprite)) {
                currentWorld.sprites.remove(sprite);
            }
            else if (currentWorld.npcSprites.contains(sprite)) {
                currentWorld.npcSprites.remove(sprite);
            }
        }
        currentWorld.removeSprites.clear();

        for (Iob iobSprite : currentWorld.iobSprites) {
            iobSprite.update(deltaTime);
        }

        for (Item itemSprite : currentWorld.itemSprites) {
            itemSprite.update(deltaTime);
        }
        
        for (Coin coinSprite : currentWorld.coinSprites) {
            coinSprite.update(deltaTime);
        }

        for (UIComponent uicomponent : currentWorld.uicomponents) {
            uicomponent.update(deltaTime);
        }
    }

    public static void render(Graphics g) {
        if (inMenu) {
            mainMenu.render(g);
            return;
        }

        if (backdrop != null) {
            int x = backdropX - (int) Renderer.camX;
            int y = backdropY - (int) Renderer.camY;

            //draw map image based on where the player is on the map
            if (currentPlayer.isNearEdgeOfMapXMin && currentPlayer.isNearEdgeOfMapYMin) {   
                //top left
                g.drawImage(backdrop, 0, 0 , Renderer.gameWidth * 3, Renderer.gameHeight * 3, null);               
            } else if (currentPlayer.isNearEdgeOfMapXMax && currentPlayer.isNearEdgeOfMapYMin) {    
                //top right
                g.drawImage(backdrop, -Renderer.gameWidth * 2, 0 , Renderer.gameWidth * 3, Renderer.gameHeight * 3, null);                
            } else if (currentPlayer.isNearEdgeOfMapXMin && currentPlayer.isNearEdgeOfMapYMax) {    
                //bottom left
                g.drawImage(backdrop, 0, -Renderer.gameHeight * 2 , Renderer.gameWidth * 3, Renderer.gameHeight * 3, null);              
            } else if (currentPlayer.isNearEdgeOfMapXMax && currentPlayer.isNearEdgeOfMapYMax) {    
                //bottom right
                g.drawImage(backdrop, -Renderer.gameWidth * 2, -Renderer.gameHeight * 2 , Renderer.gameWidth * 3, Renderer.gameHeight * 3, null);
            } else if (currentPlayer.isNearEdgeOfMapYMin) {    
                //top
                g.drawImage(backdrop, x + Renderer.gameWidth / 2, 0 , Renderer.gameWidth * 3, Renderer.gameHeight * 3, null);
            } else if (currentPlayer.isNearEdgeOfMapXMin) {    
                //left
                g.drawImage(backdrop, 0, y + Renderer.gameHeight / 2, Renderer.gameWidth * 3, Renderer.gameHeight * 3, null);
            } else if (currentPlayer.isNearEdgeOfMapXMax) {    
                //right
                g.drawImage(backdrop, -Renderer.gameWidth * 2, y + Renderer.gameHeight / 2, Renderer.gameWidth * 3, Renderer.gameHeight * 3, null);
            } else if (currentPlayer.isNearEdgeOfMapYMax) {    
                //bottom
                g.drawImage(backdrop, x + Renderer.gameWidth / 2, -Renderer.gameHeight * 2, Renderer.gameWidth * 3, Renderer.gameHeight * 3, null);
            } else {
                //center
                g.drawImage(backdrop, x + Renderer.gameWidth / 2, y + Renderer.gameHeight / 2, Renderer.gameWidth * 3, Renderer.gameHeight * 3, null);
            }

        } else {
            try {
                backdrop = Renderer.loadImage("/images/World.png");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (Sprite sprite : currentWorld.sprites) {
            if (!(sprite instanceof Slash)) {
                sprite.render(g);
            }
        }

        for (Npc npcSprite : currentWorld.npcSprites) {
            npcSprite.render(g);
        }

        for (Iob iobSprite : currentWorld.iobSprites) {
            iobSprite.render(g);
        }

        for (Item itemSprite : currentWorld.itemSprites) {
            itemSprite.render(g);
        }

        for (Coin coinSprite : currentWorld.coinSprites) {
            coinSprite.render(g);
        }

        currentPlayer.render(g);

        //melee swings render on top of the player
        for (Sprite sprite : currentWorld.sprites) {
            if (sprite instanceof Slash) {
                sprite.render(g);
            }
        }

        for (UIComponent uicomponent : currentWorld.uicomponents) {
            uicomponent.render(g);
        }

        if (deathTimer > 0) {
            g.setColor(new Color(25, 0, 0, 229));
            g.fillRect(0, 0, Renderer.gameWidth, Renderer.gameHeight);

            int centerX = Renderer.gameWidth / 2;
            int centerY = Renderer.gameHeight / 2;

            g.setFont(new Font("Tahoma", Font.BOLD, 96));
            g.setColor(new Color(200, 30, 30));
            String died = "YOU DIED";
            int diedWidth = g.getFontMetrics().stringWidth(died);
            g.drawString(died, centerX - diedWidth / 2, centerY - 40);

            g.setFont(new Font("Tahoma", Font.PLAIN, 24));
            g.setColor(Color.WHITE);
            String note = "- Press Enter -";
            int noteWidth = g.getFontMetrics().stringWidth(note);
            g.drawString(note, centerX - noteWidth / 2, centerY + 40);
        }
    }

    public void addSprite(Sprite sprite) {
        if (!addSprites.contains(sprite)) {
            addSprites.add(sprite);
        }
    }

    public void removeSprite(Sprite sprite) {
        if (!removeSprites.contains(sprite)) {
            removeSprites.add(sprite);
        }
    }
}