package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import objects.Player;
import objects.abilities.Bolt;
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
            knife.animations[0].playAnimation();
            abilityImage = knife.animations[0].getImage();
        } else if (player.spellChoice == 1) {
            abilityString = "Bolt";
            prevAbilityString = "Knife";
            nextAbilityString = "Knife";
            bolt.animations[0].playAnimation();
            abilityImage = bolt.animations[0].getImage();
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
            int maxIconWidth = boxWidth - 6;
            int maxIconHeight = rect.height - 12;

            //rotated 90 degrees, so the bounding box swaps width/height
            float scale = Math.min((float) maxIconWidth / abilityImage.getHeight(),
                    (float) maxIconHeight / abilityImage.getWidth());
            int drawWidth = (int) (abilityImage.getWidth() * scale);
            int drawHeight = (int) (abilityImage.getHeight() * scale);

            Graphics2D g2 = (Graphics2D) g;
            AffineTransform oldTransform = g2.getTransform();
            g2.translate(boxX + boxWidth / 2, rect.y + rect.height / 2);
            g2.rotate(Math.toRadians(90));
            g2.drawImage(abilityImage, -drawWidth / 2, -drawHeight / 2, drawWidth, drawHeight, null);
            g2.setTransform(oldTransform);
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