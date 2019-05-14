package objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import graphics.Renderer;
import ui.ChatWindow;
import world.World;

/**
 * MOb 
 * movable object
 */
public class Mob extends Sprite {

    protected float runSpeed = 100; //move speed

    public Mob() {
    }

    public Mob(float posX, float posY) {
        super(posX, posY);
    }

    public float getRunSpeed() {
        return this.runSpeed;
    }
    public void setRunSpeed(float runSpeed) {
        this.runSpeed = runSpeed;
    }

    public void drawAction(Graphics g, String actionString) {
        // styles
        int FontSize = 12;
        int lineHeight = 26;
        int borderRadius = 20;
        Rectangle rect = new Rectangle(Renderer.gameWidth / 2 + 20, Renderer.gameHeight / 2 + 43, 80, 20);
        if (World.currentPlayer.isNearEdgeOfMap) {
            rect.x = (int) getPosX() + 20;
            rect.y = (int) getPosY() - 23;
        } else {
            rect.x = Renderer.gameWidth / 2 + 20;
            rect.y = Renderer.gameHeight / 2 - 23;
        }
        g.setColor(Color.WHITE);
        g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, borderRadius, borderRadius);
        g.setColor(new Color(25, 25, 25)); // black #191919;
        g.fillRoundRect(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2, borderRadius, borderRadius);
        g.setColor(Color.WHITE);
        g.fillRoundRect(rect.x + rect.width - 20, rect.y, 20, 20, borderRadius * 2, borderRadius * 2);
        g.setColor(new Color(25, 25, 25)); // black #191919;
        g.setFont(new Font("Tahoma", Font.BOLD, FontSize));
        g.drawString("F", rect.x + rect.width - 12, rect.y + 15);

        // text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Tahoma", Font.BOLD, FontSize));
        g.drawString(actionString, rect.x + 10, rect.y + 15);

        // System.out.println("rect\nx"+rect.x +" y" +rect.y);
        // System.out.println("vision\nx"+vision.x +" y" +vision.y);
        // System.out.println("position\nx"+posX +" y" +posY);    
        // System.out.println("---------------------");
        // System.out.println("X1: " + rect.x + rect.width + "\nY1: " + 
        //     rect.y + "\nX2: " + (int)World.currentPlayer.getPosX() + "\nY2: " + (int)World.currentPlayer.getPosY());
    }
}