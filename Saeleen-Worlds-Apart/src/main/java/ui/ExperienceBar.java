package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import objects.Player;

/**
 * ExperienceBar
 */
public class ExperienceBar extends PlayerStats {

    public ExperienceBar(Player player) {
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
        //EXP Bar
        g.setColor(Color.BLACK);
		g.fillRect(rect.x + 7, rect.y + 59, 104, 16);
		g.setColor(Color.DARK_GRAY);
		g.fillRect(rect.x + 8, rect.y + 60, 102, 14);
        //calc exp
        int xc1;
        xc1 = (int) (((float)(player.experience-player.xpToLastLevel) / (float)(player.xpToLevel-player.xpToLastLevel)) * 100);
        
        g.setColor(xb100);
		g.fillRect(rect.x + 9, rect.y + 61, (xc1), 12);
        //text
		g.setColor(Color.RED);
		g.setFont( new Font("Tahoma", Font.PLAIN, 10));
        g.drawString((player.experience-player.xpToLastLevel) + "/" + (player.xpToLevel-player.xpToLastLevel), rect.x + 13, rect.y + 70);
        g.drawString("XP:" + player.experience, rect.x + 72, rect.y + 70);
    }
}