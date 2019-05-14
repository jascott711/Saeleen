package objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import javax.swing.JPanel;

import graphics.Animation;
import graphics.Renderer;
import input.Input;
import objects.items.Item;
import objects.npcs.Npc;
import objects.npcs.passive.Tatem;
import world.World;

/**
 * Sprite
 */
public class Sprite {

    public float posX = 0;
    public float posY = 0;
    public int width = 0;
    public int height = 0;

    protected Rectangle dimensions = new Rectangle(0, 0, width, height); // interaction bounds
    protected Rectangle vision = new Rectangle((int) posX - width, (int) posY - height, width * 2, height * 2); // vision
                                                                                                                // bounds

    // public BufferedImage image = null;
    public Animation[] animations;
    public int currentAnimation = 0;


    public boolean isSolid = false;
    public boolean showDimensions = false;
    public boolean showVision = false;

    public boolean isHit = false;

    public Sprite() {
    }

    public Sprite(float posX, float posY) {
        this.posX = posX;
        this.posY = posY;
    }

    //#region getters&setters
    public float getPosX() {
        return this.posX;
    }
    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return this.posY;
    }
    public void setPosY(float posY) {
        this.posY = posY;
    }

    public int getWidth() {
        return this.width;
    }
    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }
    public void setHeight(int height) {
        this.height = height;
    }

    public Rectangle getDimensions() {
        return this.dimensions;
    }
    public void setDimensions(Rectangle dimensions) {
        this.dimensions = dimensions;
    }

    public Rectangle getVision() {
        return this.vision;
    }
    public void setVision(Rectangle vision) {
        this.vision = vision;
    }

    public Sprite posX(float posX) {
        this.posX = posX;
        return this;
    }
    public Sprite posY(float posY) {
        this.posY = posY;
        return this;
    }
    //#endregion
    
    public boolean doesCollide(Sprite sprite){
        float myLeft = getPosX() - width / 2;
        float myRight = getPosX() + width / 2;
        float myUp = getPosY() - height / 2;
        float myDown = getPosY() + height / 2;

        float otherLeft = sprite.posX - sprite.width / 2;
        float otherRight = sprite.posX + sprite.width / 2;
        float otherUp = sprite.posY - sprite.height / 2;
        float otherDown = sprite.posY + sprite.height / 2;

        if (myLeft < otherRight && myRight > otherLeft && myDown > otherUp && myUp < otherDown) {
            return true;
        }       

        return false;
    }

    public boolean inVisionOf(Sprite sprite){
        float myLeft = getPosX() - width / 2;
        float myRight = getPosX() + width / 2;
        float myUp = getPosY() - height / 2;
        float myDown = getPosY() + height / 2;

        float otherLeft = sprite.vision.x;
        float otherRight = sprite.vision.x + sprite.vision.width;
        float otherUp = sprite.vision.y;
        float otherDown = sprite.vision.y + sprite.vision.height;

        if (myLeft < otherRight && myRight > otherLeft && myDown > otherUp && myUp < otherDown) {
            return true;
        }
        
        return false;
    }



    public void update (float deltaTime) {
        //Collision Detection
        if (Input.getKeyDown(KeyEvent.VK_V)) {
            if (showDimensions) {
                showDimensions = false;
            } 
            else if (!showDimensions) {
                showDimensions = true;
            }
            if (showVision) {
                showVision = false;
            } 
            else if (!showVision) {
                showVision = true;
            }
        }
    }

    public void render (Graphics g) {
        //animations[currentAnimation].playAnimation();

        if (animations == null || currentAnimation >= animations.length) {
            return;
        }

        BufferedImage image = animations[currentAnimation].getImage();

        if (image == null) {
            return;
        }
        
        int realX = (int) posX - (image.getWidth() / 2); //center x
        int realY = (int) posY - (image.getHeight() / 2); //center y
        
        //check if player is close to the edge of the map for camera toggle
        if (World.currentPlayer.isNearEdgeOfMapXMin) {
            realX = (int) posX - (image.getWidth() / 2);
        } else if (World.currentPlayer.isNearEdgeOfMapXMax) {
            realX = (int) posX - (image.getWidth() / 2) - (Renderer.gameWidth * 2);
        } else {
            realX = realX - (int)Renderer.camX + Renderer.gameWidth / 2;
        }
        if (World.currentPlayer.isNearEdgeOfMapYMin) {
            realY = (int) posY - (image.getHeight() / 2); 
        } else if (World.currentPlayer.isNearEdgeOfMapYMax) {
            realY = (int) posY - (image.getHeight() / 2) - (Renderer.gameHeight * 2);
        } else {
            realY = realY - (int)Renderer.camY + Renderer.gameHeight / 2;
        }

        //draw sprite
        g.drawImage(image, realX, realY, image.getWidth(), image.getHeight(), null);
        
        // ------------ testing
        if (showDimensions) {
            //draw sprite bounds for collision detection
            g.setColor(Color.RED);
            g.drawRect(realX,realY,dimensions.width,dimensions.height);
        }
        if (showVision) {
            //draw sprite vision for collision detection
            g.setColor(Color.YELLOW);
            g.drawRect(realX - vision.width / 2, realY - vision.height / 2, (int)(vision.width * 1.5), (int)(vision.height * 1.5));        
        }
        
        if(this instanceof Player){
            //System.out.println(this.getClass().toString()+" POS x"+ posX + " y" +posY);
            //System.out.println(this.getClass().toString()+" REAL x"+ realX + " y" +realY);
        }

        
        if(this instanceof Item || this instanceof Tatem){
            if (doesCollide(World.currentPlayer)) {
                g.setColor(Color.WHITE);
                g.drawLine((int)World.currentPlayer.realX + 100, (int)World.currentPlayer.realY + 20, realX + (width / 2), realY);
            }
        }
    }

    
}