package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import graphics.Renderer;
import objects.Player;
import objects.items.Item;

/**
 * DisplayItems
 */
public class DisplayItems extends PlayerStats {

    private int itemCount = 0;
    private int itemCountMax = 6;
    public int itemIconWidth = 24;
    public int itemIconHeight = 24;

    public DisplayItems(Player player) {
        super(player);
        rect = new Rectangle((Renderer.gameWidth+250) / 2, Renderer.gameHeight - 86, ((itemIconWidth + 2) * (itemCountMax / 2)) + 14, (itemIconHeight * (itemCountMax / 3)) + 36);

    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void update (float deltaTime) {
        itemCount = player.myItems.size();
    }

    @Override
    public void render(Graphics g) {
        //styles
        int FontSize = 12;
        int lineHeight = 20;
        int borderRadius = 20;
        //container
        g.setColor(Color.WHITE);
        g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, borderRadius, borderRadius);
        g.setColor(new Color(230,240,85)); //yellow #e6f055
        g.fillRoundRect(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2, borderRadius, borderRadius);
        g.setColor(new Color(25,25,25)); //black #191919;
        g.fillRoundRect(rect.x + 3, rect.y + 3, rect.width - 6, rect.height - 6, borderRadius, borderRadius);
        //text
        g.setColor(Color.WHITE);
        g.setFont( new Font("Tahoma", Font.BOLD, FontSize));
        g.drawString("Items", rect.x + 12, rect.y + lineHeight);

        for (int i = 0; i < itemCount; i++) {
            BufferedImage image = player.myItems.get(i).animations[player.myItems.get(i).currentAnimation].getImage();

            if (i <= (itemCountMax / 2) - 1) {
                g.drawImage(image, (i * (itemIconWidth + 2)) + rect.x + 7, rect.y + 27, itemIconWidth, itemIconHeight, null);
            } else if (i > (itemCountMax / 2) - 1) {
                g.drawImage(image, ((i - (itemCountMax / 2))  * (itemIconWidth + 2)) + rect.x + 7, rect.y + itemIconHeight + 29, itemIconWidth, itemIconHeight, null);                
            }
        }

        for (int i = 0; i < itemCountMax; i++) {
            g.setColor(Color.WHITE);
            g.setFont( new Font("Tahoma", Font.BOLD, 8));
            
            if (i <= (itemCountMax / 2) - 1) {
                g.drawString("" + (i + 1), (i * (itemIconWidth + 2)) + rect.x + 7, rect.y + itemIconHeight + 27);
            } else if (i > (itemCountMax / 2) - 1) {
                g.drawString("" + (i + 1), ((i - (itemCountMax / 2)) * (itemIconWidth + 2)) + rect.x + 7, rect.y + (itemIconHeight * 2) + 29);
            }
        }

    }
}