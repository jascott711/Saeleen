package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import objects.Player;
import objects.abilities.Bolt;
import objects.abilities.Bullet;
import objects.abilities.Knife;

/**
 * DisplayAbility
 */
public class DisplayAbility extends PlayerStats {

    private String abilityString;
    private String nextAbilityString;
    private String prevAbilityString;
    private Knife knife;
    private Bolt bolt;
    private BufferedImage abilityImage;

    public DisplayAbility(Player player) {
        super(player);
        abilityString = "Knife";
        nextAbilityString = "Bolt";
        prevAbilityString = "Bolt";
        knife = new Knife(0, 0, 0);
        bolt = new Bolt(0, 0, 0);
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void update (float deltaTime) {
        if (player.spellChoice == 0) {
            abilityString = "Knife";
            prevAbilityString = "Bolt";
            nextAbilityString = "Bolt";
            abilityImage = knife.animations[knife.currentAnimation].getImage();
        } else if (player.spellChoice == 1) {
            abilityString = "Bolt";
            prevAbilityString = "Knife";
            nextAbilityString = "Knife";
            abilityImage = bolt.animations[bolt.currentAnimation].getImage();
        }
    }

    @Override
    public void render(Graphics g) {
        //styles
        int FontSize = 12;
        int lineHeight = 18;
        int borderRadius = 20;
        g.setColor(Color.WHITE);
        g.fillRoundRect(rect.x + rect.width + 2, rect.y, rect.width / 2, rect.height, borderRadius, borderRadius);
        g.setColor(new Color(230,240,85)); //yellow #e6f055
        g.fillRoundRect(rect.x + rect.width + 3, rect.y + 1, rect.width / 2- 2, rect.height - 2, borderRadius, borderRadius);
        g.setColor(new Color(25,25,25)); //black #191919;
        g.fillRoundRect(rect.x + rect.width + 5, rect.y + 3, rect.width / 2- 6, rect.height - 6, borderRadius, borderRadius);
        //text
        g.setColor(Color.WHITE);
        g.setFont( new Font("Tahoma", Font.BOLD, FontSize));
        g.drawString("Ability", rect.x + rect.width + 12, rect.y + lineHeight);
        g.setFont( new Font("Tahoma", Font.ITALIC, FontSize));
        g.drawString(abilityString, rect.x + rect.width + 12, rect.y + lineHeight * 2);
        //selected ability icon
        if (abilityImage != null) {
            BufferedImage rotated = Bullet.rotate(abilityImage, Math.toRadians(90));
            int iconWidth = (player.spellChoice == 1) ? 24 : 20;
            g.drawImage(rotated, rect.x + rect.width + 22, rect.y + lineHeight * 3 - 8, iconWidth, 30, null);
        }
        
    }
    
}