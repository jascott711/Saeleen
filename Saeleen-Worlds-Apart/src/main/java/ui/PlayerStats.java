package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

import graphics.Renderer;
import objects.Player;

/**
 * PlayerStats
 * display player level and is the container 
 * to hold the health, mana, and exp bar
 */
public class PlayerStats extends UIComponent {


    protected Player player;
    protected Rectangle rect = new Rectangle((Renderer.gameWidth-120)/2,Renderer.gameHeight-86,120,84);

    public PlayerStats(Player player) {
        this.player = player;
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
        //player stats
        //styles
        int FontSize = 12;
        int lineHeight = 20;
        int borderRadius = 20;
        g.setColor(Color.WHITE);
        g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, borderRadius, borderRadius);
        g.setColor(new Color(230,240,85)); //yellow #e6f055
        g.fillRoundRect(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2, borderRadius, borderRadius);
        g.setColor(new Color(25,25,25)); //black #191919;
        g.fillRoundRect(rect.x + 3, rect.y + 3, rect.width - 6, rect.height - 6, borderRadius, borderRadius);
        //text
        g.setColor(Color.WHITE);
        g.setFont( new Font("Tahoma", Font.BOLD, FontSize));
        g.drawString("Level "+ player.level, rect.x + 12, rect.y + lineHeight);

    }
    
}