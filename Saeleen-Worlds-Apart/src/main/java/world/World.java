package world;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import graphics.Renderer;
import objects.Mob;
import objects.Player;
import objects.Sprite;
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

    private static long lastTime = 0;

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
            sprite.render(g);
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

        for (UIComponent uicomponent : currentWorld.uicomponents) {
            uicomponent.render(g);
        }
    }

    public void addSprite(Sprite sprite) {
        if (!addSprites.contains(sprite)) {
            addSprites.add(sprite);
        }
    }

    public void removeSprite(Sprite sprite) {
        if (!removeSprites.contains(sprite)) {
            removeSprites.remove(sprite);
        }
    }
}