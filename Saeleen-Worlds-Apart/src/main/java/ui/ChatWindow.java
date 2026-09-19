package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import graphics.Renderer;
import objects.Mob;
import objects.Player;
import objects.npcs.Npc;

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

        if (object instanceof Npc) {
            speech = ((Npc) object).conversation;
        }
        containerWidth = Renderer.gameWidth / 3;
        containerHeight = lineHeight * 2 + 12;
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
        playerChatWindow(g);
    }

    public void playerChatWindow(Graphics g) {
        Npc npc = (Npc) object;

        int lineIndex = npc.conversationLine;
        String line = (lineIndex >= 0 && lineIndex < speech.size()) ? speech.get(lineIndex) : "";
        String speaker = npc.npcName;
        String text = line;
        if (line.startsWith("C:")) {
            speaker = "Clara";
            text = line.substring(2).trim();
        }

        //draw speaker sprite
        BufferedImage speakerImage = null;
        if (speaker.equals("Clara")) {
            speakerImage = player.playerChatImage;
        } else {
            speakerImage = npc.objectChatImage;
        }
        if (speakerImage != null) {
            g.drawImage(speakerImage, rect.x - speakerImage.getWidth() / 2, rect.y - speakerImage.getHeight() / 2, speakerImage.getWidth(), speakerImage.getHeight(), null);
        }

        //draw container
        g.setColor(Color.WHITE);
        g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, borderRadius, borderRadius);
        g.setColor(new Color(25,25,25)); //black #191919;
        g.fillRoundRect(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2, borderRadius, borderRadius);
        //speaker name
        g.setColor(Color.WHITE);
        g.setFont( new Font("Tahoma", Font.BOLD, titleFontSize));
        g.drawString(speaker, rect.x + 10, rect.y + lineHeight - 4);
        //line
        if (npc.inStory) {
            g.setColor(new Color(255, 182, 193)); //light pink
        } else {
            g.setColor(Color.WHITE);
        }
        g.setFont( new Font("Tahoma", Font.PLAIN, FontSize));
        g.drawString(text, rect.x + 10, rect.y + lineHeight * 2);

    }

}