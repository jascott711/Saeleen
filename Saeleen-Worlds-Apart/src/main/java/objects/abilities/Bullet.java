package objects.abilities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import graphics.Renderer;
import objects.Mob;
import objects.Player;
import objects.npcs.Npc;
import objects.npcs.aggressive.Enemy;
import world.World;

/**
 * Bullet
 */
public class Bullet extends Mob {

    public int direction = 0; //0 = left, 1 = right, 2 = up, 3 = down
    public float speed = 400.0f;
    public int damage = 10;

    public Bullet(float posX, float posY, int direction) {
        super(posX, posY);
        this.direction = direction;
        width = 10;
        height = 10;
    }


    public float getDamage() {
        return this.damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    
    public void update (float deltaTime) {
        float moveX = 0;
        float moveY = 0;

        if (direction == 0 ) {
            //west
            moveX -= speed;
        } else if (direction == 1 ) {
            //east
            moveX += speed;
        } else if (direction == 2 ) {
            //north
            moveY -= speed;
            dimensions.width = height;
            dimensions.height = width;
        } else if (direction == 3 ) {
            //south
            moveY += speed;
            dimensions.width = height;
            dimensions.height = width;
        } 

        posX += moveX * deltaTime;
        posY += moveY * deltaTime;
        dimensions.x = (int) getPosX() - dimensions.width / 2;
        dimensions.y = (int) getPosY() - dimensions.height / 2;

        // System.out.println("I exist at " + posX + ", " + posY);
    }

    @Override
    public void render (Graphics g) {
        animations[currentAnimation].playAnimation();

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
            //west
            realX = (int) posX - (image.getWidth() / 2);
        } else if (World.currentPlayer.isNearEdgeOfMapXMax) {
            //east
            realX = (int) posX - (image.getWidth() / 2) - (Renderer.gameWidth * 2);
        } else {
            realX = realX - (int)Renderer.camX + Renderer.gameWidth / 2;
        }
        if (World.currentPlayer.isNearEdgeOfMapYMin) {
            //north
            realY = (int) posY - (image.getHeight() / 2); 
        } else if (World.currentPlayer.isNearEdgeOfMapYMax) {
            //south
            realY = (int) posY - (image.getHeight() / 2) - (Renderer.gameHeight * 2);
        } else {
            realY = realY - (int)Renderer.camY + Renderer.gameHeight / 2;
        }

        //draw sprite
        if (direction == 0 ) {  
            //west
            g.drawImage(image, realX, realY, image.getWidth(), image.getHeight(), null);
        } else if (direction == 1 ) {
            //east
            g.drawImage(rotate(image, Math.toRadians(180)), realX, realY, image.getWidth(), image.getHeight(), null);
        } else if (direction == 2 ) {
            //north
            g.drawImage(rotate(image, Math.toRadians(90)), realX, realY, image.getWidth(), image.getWidth(), null);
        } else if (direction == 3 ) {
            //south
            g.drawImage(rotate(image, Math.toRadians(-90)), realX, realY, image.getWidth(), image.getWidth(), null);
        }

        // ------------ testing
        if (World.currentPlayer.showDimensions) {
            g.setColor(Color.RED);
            if (direction == 2 || direction == 3) {
                //north
                g.drawRect(realX + 10,realY,dimensions.width,dimensions.height);
            } else {
                g.drawRect(realX,realY,dimensions.width,dimensions.height);
            }
        }        
    }

    public static BufferedImage rotate(BufferedImage image, double angle) {
        int w = image.getWidth(), h = image.getHeight();
        GraphicsConfiguration gc = getDefaultConfiguration();
        BufferedImage result = gc.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
        Graphics2D g = result.createGraphics();
        g.rotate(angle, w / 2, h / 2);
        g.drawRenderedImage(image, null);
        g.dispose();
        return result;
    }
    
    private static GraphicsConfiguration getDefaultConfiguration() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        return gd.getDefaultConfiguration();
    }
}