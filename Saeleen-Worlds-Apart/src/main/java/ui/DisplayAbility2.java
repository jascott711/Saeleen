package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import objects.Player;

/**
 * DisplayAbility2
 */
public class DisplayAbility2 extends PlayerStats {

    private String abilityString;

    public DisplayAbility2(Player player) {
        super(player);
        abilityString = "Run";
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void update (float deltaTime) {
        if (player.spellChoice2 == 0) {
            abilityString = "Run";
        }
    }

    @Override
    public void render(Graphics g) {
        //styles
        int FontSize = 12;
        int lineHeight = 18;
        int borderRadius = 20;
        g.setColor(Color.WHITE);
        g.fillRoundRect(rect.x + rect.width + 64, rect.y, rect.width / 2, rect.height, borderRadius, borderRadius);
        g.setColor(new Color(230,240,85)); //yellow #e6f055
        g.fillRoundRect(rect.x + rect.width + 65, rect.y + 1, rect.width / 2 - 2, rect.height - 2, borderRadius, borderRadius);
        g.setColor(new Color(25,25,25)); //black #191919;
        g.fillRoundRect(rect.x + rect.width + 67, rect.y + 3, rect.width / 2 - 6, rect.height - 6, borderRadius, borderRadius);
        //text
        g.setColor(Color.WHITE);
        g.setFont( new Font("Tahoma", Font.BOLD, FontSize));
        g.drawString("Ability2", rect.x + rect.width + 74, rect.y + lineHeight);
        g.setFont( new Font("Tahoma", Font.ITALIC, FontSize));
        g.drawString(abilityString, rect.x + rect.width + 74, rect.y + lineHeight * 2);
        g.setFont( new Font("Tahoma", Font.PLAIN, 10));
        g.drawString("[ ] to swap", rect.x + rect.width + 74, rect.y + lineHeight * 3);
    }

}