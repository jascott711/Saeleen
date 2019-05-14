package objects.npcs;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import graphics.Animation;
import graphics.Renderer;
import objects.Mob;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import ui.ChatWindow;
import world.World;

/**
 * Npc
 */
public class Npc extends Mob {
    
    public Animation animUp = new Animation(), animDown = new Animation(), animLeft = new Animation(),
            animRight = new Animation(), animTakeDamage = new Animation();

    //chat
    public ChatWindow newCW;
    public ArrayList<String> conversation = new ArrayList<String>();
    public ArrayList<String> objectSpeech = new ArrayList<String>();
    public ArrayList<String> playerSpeech = new ArrayList<String>();
    public BufferedImage playerChatImage = null;
    public BufferedImage objectChatImage = null;
    public int chatSequenceStep = 0;


    public Animation[] animationsTakeDamage;

    public Npc() {
    }

    public Npc(float posX, float posY) {
        super(posX, posY);
    }

    //get and draw chat window
    public void speak() {
        if (playerChatImage == null || objectChatImage == null) {
            //System.out.println("Can't draw Player or Object chat image");
            //return;
        }

        chatSequenceStep++;
        if (chatSequenceStep > conversation.size() + 1) {
            return;            
        }

        newCW = new ChatWindow(World.currentPlayer,this);
        World.currentWorld.uicomponents.add(newCW);

    }  

    public void clearSpeak() {
        chatSequenceStep = 0;
        World.currentWorld.uicomponents.remove(newCW);
    }

    // public void drawAction(Graphics g, String actionString) {
    //     // styles
    //     int FontSize = 18;
    //     int lineHeight = 26;
    //     int borderRadius = 20;
    //     Rectangle rect = new Rectangle(Renderer.gameWidth / 2 + 20, Renderer.gameHeight / 2 + 23, 120, 40);
    //     if (World.currentPlayer.isNearEdgeOfMap) {
    //         rect.x = (int) getPosX() + 40;
    //         rect.y = (int) getPosY() - 23;
    //     } else {
    //         rect.x = Renderer.gameWidth / 2 + 40;
    //         rect.y = Renderer.gameHeight / 2 - 23;
    //     }
    //     g.setColor(Color.WHITE);
    //     g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, borderRadius, borderRadius);
    //     g.setColor(new Color(25, 25, 25)); // black #191919;
    //     g.fillRoundRect(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2, borderRadius, borderRadius);
    //     g.setColor(Color.WHITE);
    //     g.fillRoundRect(rect.x + rect.width - 37, rect.y + 5, 30, 30, borderRadius * 2, borderRadius * 2);
    //     g.setColor(new Color(25, 25, 25)); // black #191919;
    //     g.setFont(new Font("Tahoma", Font.BOLD, FontSize));
    //     g.drawString("F", rect.x + rect.width - 27, rect.y + 27);

    //     // text
    //     g.setColor(Color.WHITE);
    //     g.setFont(new Font("Tahoma", Font.BOLD, FontSize));
    //     g.drawString(actionString, rect.x + 10, rect.y + lineHeight);

    //     // System.out.println("rect\nx"+rect.x +" y" +rect.y);
    //     // System.out.println("vision\nx"+vision.x +" y" +vision.y);
    //     // System.out.println("position\nx"+posX +" y" +posY);
    // }

}