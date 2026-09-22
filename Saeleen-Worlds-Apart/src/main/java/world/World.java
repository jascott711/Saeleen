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
import game.App;
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
    public static boolean isLoading = false;

    public static TransitionBox pendingTransition = null;

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

    public ArrayList<TransitionBox> transitionBoxes = new ArrayList<TransitionBox>();

    public static BufferedImage backdrop = null;
    private static int backdropX = 0;
    private static int backdropY = 0;

    //the current zone's map size in world units, set when a zone loads
    public static int mapWidth = 0;
    public static int mapHeight = 0;

    public World() {
    }
    
    public static void update() {
        float deltaTime = (System.nanoTime() - lastTime) / 1000000000.0f;
        lastTime = System.nanoTime();

        //while a zone is loading the game is paused and nothing updates;
        //the new zone is built here on the next frame, then the loading screen clears
        if (isLoading) {
            if (pendingTransition != null) {
                App.loadZone(pendingTransition.destZone, pendingTransition.destSpawnId);
                pendingTransition = null;
                isLoading = false;
            }
            return;
        }

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

        //zone transition: standing in exactly one transition box queues the zone it leads to
        //(overlapping boxes cancel out and trigger nothing)
        //the build runs on the next update while the loading screen is showing
        TransitionBox transitionBox = getTransitionBox(currentPlayer.getDimensions());
        if (transitionBox != null) {
            isLoading = true;
            pendingTransition = transitionBox;
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

        //while a zone loads on the background thread show a loading screen
        //(nothing is drawn from the world while it is being swapped in)
        if (isLoading) {
            g.setColor(new Color(0, 0, 0, 220));
            g.fillRect(0, 0, Renderer.gameWidth, Renderer.gameHeight);

            g.setFont(new Font("Tahoma", Font.BOLD, 56));
            g.setColor(Color.WHITE);
            String loading = "Loading...";
            int loadingWidth = g.getFontMetrics().stringWidth(loading);
            g.drawString(loading, Renderer.gameWidth / 2 - loadingWidth / 2, Renderer.gameHeight / 2);
            return;
        }

        if (backdrop != null) {
            int x = backdropX - (int) Renderer.camX;
            int y = backdropY - (int) Renderer.camY;

            //when the camera is pinned to an edge the map shifts by the visible width
            int mapOffsetX = Renderer.gameWidth - mapWidth;
            int mapOffsetY = Renderer.gameHeight - mapHeight;

            //draw map image based on where the player is on the map
            if (currentPlayer.isNearEdgeOfMapXMin && currentPlayer.isNearEdgeOfMapYMin) {   
                //top left
                g.drawImage(backdrop, 0, 0 , mapWidth, mapHeight, null);               
            } else if (currentPlayer.isNearEdgeOfMapXMax && currentPlayer.isNearEdgeOfMapYMin) {    
                //top right
                g.drawImage(backdrop, mapOffsetX, 0 , mapWidth, mapHeight, null);                
            } else if (currentPlayer.isNearEdgeOfMapXMin && currentPlayer.isNearEdgeOfMapYMax) {    
                //bottom left
                g.drawImage(backdrop, 0, mapOffsetY , mapWidth, mapHeight, null);              
            } else if (currentPlayer.isNearEdgeOfMapXMax && currentPlayer.isNearEdgeOfMapYMax) {    
                //bottom right
                g.drawImage(backdrop, mapOffsetX, mapOffsetY , mapWidth, mapHeight, null);
            } else if (currentPlayer.isNearEdgeOfMapYMin) {    
                //top
                g.drawImage(backdrop, x + Renderer.gameWidth / 2, 0 , mapWidth, mapHeight, null);
            } else if (currentPlayer.isNearEdgeOfMapXMin) {    
                //left
                g.drawImage(backdrop, 0, y + Renderer.gameHeight / 2, mapWidth, mapHeight, null);
            } else if (currentPlayer.isNearEdgeOfMapXMax) {    
                //right
                g.drawImage(backdrop, mapOffsetX, y + Renderer.gameHeight / 2, mapWidth, mapHeight, null);
            } else if (currentPlayer.isNearEdgeOfMapYMax) {    
                //bottom
                g.drawImage(backdrop, x + Renderer.gameWidth / 2, mapOffsetY, mapWidth, mapHeight, null);
            } else {
                //center
                g.drawImage(backdrop, x + Renderer.gameWidth / 2, y + Renderer.gameHeight / 2, mapWidth, mapHeight, null);
            }

        } else {
            try {
                backdrop = Renderer.loadImage("/images/World.png");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        drawTransitionBoxes(g);

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

    //draws the transition boxes (purple borders, for testing) mapped to screen space
    private static void drawTransitionBoxes(Graphics g) {
        g.setColor(new Color(155, 0, 255));
        for (TransitionBox box : currentWorld.transitionBoxes) {
            int x = mapToScreenX(box.bounds.x);
            int y = mapToScreenY(box.bounds.y);
            int width = mapToScreenX(box.bounds.x + box.bounds.width) - x;
            int height = mapToScreenY(box.bounds.y + box.bounds.height) - y;
            g.drawRect(x, y, width, height);
        }
    }

    //returns the single transition box containing the area,
    //or null when the area is in no box or in overlapping boxes (they cancel out)
    public static TransitionBox getTransitionBox(Rectangle area) {
        TransitionBox found = null;
        for (TransitionBox box : currentWorld.transitionBoxes) {
            if (box.bounds.intersects(area)) {
                if (found != null) {
                    return null;
                }
                found = box;
            }
        }
        return found;
    }

    private static int mapToScreenX(int mapX) {
        if (currentPlayer.isNearEdgeOfMapXMin) {
            return mapX;
        } else if (currentPlayer.isNearEdgeOfMapXMax) {
            return mapX - mapWidth + Renderer.gameWidth;
        } else {
            return mapX - (int) Renderer.camX + Renderer.gameWidth / 2;
        }
    }

    private static int mapToScreenY(int mapY) {
        if (currentPlayer.isNearEdgeOfMapYMin) {
            return mapY;
        } else if (currentPlayer.isNearEdgeOfMapYMax) {
            return mapY - mapHeight + Renderer.gameHeight;
        } else {
            return mapY - (int) Renderer.camY + Renderer.gameHeight / 2;
        }
    }
}