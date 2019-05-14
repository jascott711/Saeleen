package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import graphics.Renderer;
import objects.Mob;
import objects.Player;
import objects.npcs.Npc;
import world.World;

/**
 * ChatWindow
 */
public class ChatWindow extends UIComponent {

    private Player player;
    private Mob object;
    public ArrayList<String> conversation = new ArrayList<String>();
    ArrayList<String> speech = new ArrayList<String>();

    //styles
    int titleFontSize = 24;
    int FontSize = 14;
    int lineHeight = 24;
    int cellHeight = 24;
    int containerWidth;
    int containerHeight;
    int borderRadius = 20;
    Rectangle rect;    

    public ChatWindow(Player player, Mob object) {
        this.player = player;
        this.object = object;

        speech.add("Hi");
        containerWidth = Renderer.gameWidth / 4;
        containerHeight = cellHeight * speech.size() + 10;
        rect = new Rectangle((Renderer.gameWidth / 2) - (containerWidth / 2), Renderer.gameHeight / 2 + 50,  containerWidth, containerHeight);      
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
    
    public void update (float deltaTime) {

    }

    @Override
    public void render(Graphics g) {
        //controls
        
        for (Npc sprite : World.currentWorld.npcSprites) {
            if (World.currentPlayer.inVisionOf(sprite) && sprite.chatSequenceStep <= sprite.conversation.size() + 1) {
                playerChatWindow(g);
            } else {
                g.dispose();
            }
        }
        
    }

    public void objectChatWindow(Graphics g) {
        
    }

    public void playerChatWindow(Graphics g) {
        //draw sprite
        g.drawImage(player.playerChatImage, rect.x - player.playerChatImage.getWidth() / 2, rect.y - player.playerChatImage.getHeight() / 2, player.playerChatImage.getWidth(), player.playerChatImage.getHeight(), null);

        //draw container
        g.setColor(Color.WHITE);
        g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, borderRadius, borderRadius);
        g.setColor(new Color(25,25,25)); //black #191919;
        g.fillRoundRect(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2, borderRadius, borderRadius);
        //text
        g.setColor(Color.WHITE);
        g.setFont( new Font("Tahoma", Font.BOLD, titleFontSize));
        g.drawString("Clara", rect.x, rect.y + lineHeight - 30);
        for (int i = 0; i < speech.size(); i++) {
            g.setFont( new Font("Tahoma", Font.PLAIN, FontSize));
            g.drawString(speech.get(i), rect.x + 10, rect.y + lineHeight + (cellHeight * i ));
            
        }

    }


}