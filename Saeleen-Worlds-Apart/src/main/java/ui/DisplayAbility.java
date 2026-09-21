package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import objects.Player;
import objects.abilities.Bolt;
import objects.abilities.Knife;
import objects.abilities.Slash;

/**
 * DisplayAbility
 */
public class DisplayAbility extends PlayerStats {

    private String abilityString;
    private String nextAbilityString;
    private String prevAbilityString;
    private Knife knife;
    private Bolt bolt;
    private Slash slash;
    private BufferedImage abilityImage;
    private float iconTime;

    public DisplayAbility(Player player) {
        super(player);
        abilityString = "Slash";
        nextAbilityString = "Knife";
        prevAbilityString = "Bolt";
        knife = new Knife(0, 0, 0);
        bolt = new Bolt(0, 0, 0);
        slash = new Slash(player);
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void update (float deltaTime) {
        iconTime += deltaTime;
        if (player.spellChoice == 0) {
            abilityString = "Slash";
            prevAbilityString = "Bolt";
            nextAbilityString = "Knife";
            slash.animations[0].playAnimation();
            abilityImage = slash.animations[0].getImage();
        } else if (player.spellChoice == 1) {
            abilityString = "Knife";
            prevAbilityString = "Slash";
            nextAbilityString = "Bolt";
            knife.animations[0].playAnimation();
            abilityImage = knife.animations[0].getImage();
        } else if (player.spellChoice == 2) {
            abilityString = "Bolt";
            prevAbilityString = "Knife";
            nextAbilityString = "Slash";
            bolt.animations[0].playAnimation();
            abilityImage = bolt.animations[0].getImage();
        } else {
            abilityImage = null;
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
        //selected ability icon
        if (abilityImage != null) {
            int boxX = rect.x + rect.width + 2;
            int boxWidth = rect.width / 2;
            int boxHeight = rect.height;

            Graphics2D g2 = (Graphics2D) g;
            if (player.spellChoice == 0) {
                //Slash: knife waves over 20 degrees around the bottom of the box
                AbilityIconAnimations.drawSlashWave(g2, abilityImage, boxX, rect.y, boxWidth, boxHeight, iconTime);
            } else if (player.spellChoice == 1) {
                //Knife: glides up and out the top, clipped to the box
                AbilityIconAnimations.drawGlideUp(g2, abilityImage, boxX, rect.y, boxWidth, boxHeight, iconTime);
            } else {
                //Bolt: same glide, drawn a little smaller so the loop reads like the knife
                AbilityIconAnimations.drawGlideUp(g2, abilityImage, boxX, rect.y, boxWidth, boxHeight, iconTime, 0.8f);
            }
        }

        //cooldown mask
        int boxX = rect.x + rect.width + 2;
        int boxWidth = rect.width / 2;
        float cooldown = player.getAbilityCooldown();
        if (cooldown > 0) {
            g.setColor(new Color(0, 0, 0, 190));
            g.fillRoundRect(boxX, rect.y, boxWidth, rect.height, borderRadius, borderRadius);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Tahoma", Font.BOLD, 16));
            String cooldownText = String.format("%.1f", cooldown);
            int cooldownWidth = g.getFontMetrics().stringWidth(cooldownText);
            g.drawString(cooldownText, boxX + boxWidth / 2 - cooldownWidth / 2, rect.y + rect.height / 2 + 6);
        }
        
    }
    
}