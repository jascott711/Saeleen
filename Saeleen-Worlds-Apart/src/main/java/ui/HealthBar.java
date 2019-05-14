package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import objects.Player;

/**
 * HealthBar
 */
public class HealthBar extends PlayerStats {


    public HealthBar(Player player) {
        super(player);
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
        //Draw Health Bar        
        g.setColor(Color.BLACK);
		g.fillRect(rect.x + 7, rect.y + 25, 104, 16);
		g.setColor(Color.DARK_GRAY);
		g.fillRect(rect.x + 8, rect.y + 26, 102, 14);
        
        int hc1;
        hc1 = (int) (((float)player.health / (float)player.maxHealth) * 100);
        
        if(hc1>=60)	g.setColor(hb100);
        else if (hc1<60 && hc1 >=41)	g.setColor(xb100);
    	else if (hc1<=40 && hc1 >=21)	g.setColor(Color.ORANGE);
        else if (hc1<=20)	g.setColor(Color.RED); 
        
		g.fillRect(rect.x + 9, rect.y + 27, hc1, 12);
		//text
		g.setColor(Color.WHITE);
		g.setFont( new Font("Tahoma", Font.PLAIN, 10));
        g.drawString(player.health +"/"+ player.maxHealth, rect.x + 11, rect.y + 36);	
    }
    
}