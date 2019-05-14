package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import objects.Player;

/**
 * ManaBar
 */
public class ManaBar extends PlayerStats {

    public ManaBar(Player player) {
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
        //Draw ManaBar Bar        
        g.setColor(Color.BLACK);
		g.fillRect(rect.x + 7, rect.y + 42, 104, 16);
		g.setColor(Color.DARK_GRAY);
		g.fillRect(rect.x + 8, rect.y + 43, 102, 14);
        
        int mc1;
        mc1 = (int) (((float)player.mana / (float)player.maxMana) * 100);
        
        if(mc1>=80)	g.setColor(mb100);
        else if (mc1<80 && mc1 >=51)	g.setColor(Color.BLUE);
    	else if (mc1<=50 && mc1 >=21)	g.setColor(mb50);
        else if (mc1<=20)	g.setColor(mb0);
        
		g.fillRect(rect.x + 9, rect.y + 44, mc1, 12);
		//text
		g.setColor(Color.WHITE);
		g.setFont( new Font("Tahoma", Font.PLAIN, 10));
        g.drawString(player.mana + "/" + player.maxMana, rect.x + 13, rect.y + 53);
    }
    
}