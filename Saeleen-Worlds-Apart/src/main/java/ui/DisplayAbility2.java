package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;

import graphics.Animation;
import graphics.Renderer;
import objects.Player;

/**
 * DisplayAbility2
 */
public class DisplayAbility2 extends PlayerStats {

    private Animation wing;
    private BufferedImage abilityImage;
    private float runGauge = 0;

    public DisplayAbility2(Player player) {
        super(player);
        wing = new Animation();
        wing.setFps(4);
        try {
            BufferedImage spriteSheet = Renderer.loadImage("/images/wing.png");
            final int rows = 1;
            final int cols = 4;
            final int frameWidth = spriteSheet.getWidth() / cols;
            final int frameHeight = spriteSheet.getHeight() / rows;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    wing.images.add(spriteSheet.getSubimage(j * frameWidth, i * frameHeight, frameWidth, frameHeight));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void update (float deltaTime) {
        wing.playAnimation();
        abilityImage = wing.getImage();

        //charge gauge rises with the run, and quickly slides back down when shift is released
        float target = player.getRunTimer() / player.getRunDuration();
        if (target > runGauge) {
            runGauge = target;
        } else if (runGauge > 0) {
            runGauge -= deltaTime * 2.5f;
            if (runGauge < 0) {
                runGauge = 0;
            }
        }
    }

    @Override
    public void render(Graphics g) {
        int borderRadius = 20;
        int boxX = rect.x + rect.width + 64;
        int boxWidth = rect.width / 2;
        g.setColor(Color.WHITE);
        g.fillRoundRect(boxX, rect.y, boxWidth, rect.height, borderRadius, borderRadius);
        g.setColor(new Color(230,240,85)); //yellow #e6f055
        g.fillRoundRect(boxX + 1, rect.y + 1, boxWidth - 2, rect.height - 2, borderRadius, borderRadius);
        g.setColor(new Color(25,25,25)); //black #191919
        g.fillRoundRect(boxX + 3, rect.y + 3, boxWidth - 6, rect.height - 6, borderRadius, borderRadius);

        //ability icon
        if (abilityImage != null) {
            int maxIconWidth = boxWidth - 6;
            int maxIconHeight = rect.height - 12;
            float scale = Math.min((float) maxIconWidth / abilityImage.getWidth(),
                    (float) maxIconHeight / abilityImage.getHeight()) * 0.8f;
            int drawWidth = (int) (abilityImage.getWidth() * scale);
            int drawHeight = (int) (abilityImage.getHeight() * scale);
            g.drawImage(abilityImage, boxX + boxWidth / 2 - drawWidth / 2,
                    rect.y + rect.height / 2 - drawHeight / 2, drawWidth, drawHeight, null);
        }

        //run charge mask: fills bottom-up while running, clipped to the window's rounded border
        if (runGauge > 0) {
            int fillHeight = (int) (runGauge * (rect.height - 6));
            Shape oldClip = g.getClip();
            g.setClip(new RoundRectangle2D.Float(boxX + 3, rect.y + 3, boxWidth - 6, rect.height - 6,
                    borderRadius, borderRadius));
            g.setColor(new Color(0, 180, 90, 220));
            g.fillRect(boxX + 3, rect.y + rect.height - 3 - fillHeight, boxWidth - 6, fillHeight);
            g.setClip(oldClip);
        }

        //cooldown mask
        float cooldown = player.getRunCooldown();
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