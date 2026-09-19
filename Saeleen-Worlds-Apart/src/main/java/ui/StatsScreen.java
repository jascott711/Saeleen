package ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import graphics.Renderer;
import objects.Player;
import world.World;

/**
 * StatsScreen
 * fullscreen overlay showing the player's stats, opened with TAB
 */
public class StatsScreen extends UIComponent {

    protected Player player;

    public StatsScreen(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    private void drawBar(Graphics g, int x, int y, int width, int height, int percent, Color color) {
        g.setColor(Color.BLACK);
        g.fillRect(x, y, width, height);
        g.setColor(new Color(25,25,25));
        g.fillRect(x + 1, y + 1, width - 2, height - 2);

        if (percent > 0) {
            g.setColor(color);
            g.fillRect(x + 1, y + 1, (int)((width - 2) * (percent / 100.0f)), height - 2);
        }
    }

    private void drawStat(Graphics g, int textX, int valueX, int currentY, String label, String value, Color labelColor) {
        g.setFont(new Font("Tahoma", Font.BOLD, 14));
        g.setColor(labelColor);
        g.drawString(label, textX, currentY + 6);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Tahoma", Font.PLAIN, 14));
        g.drawString(value, valueX, currentY + 6);
    }

    @Override
    public void render(Graphics g) {
        if (!World.showStats) {
            return;
        }

        //dim the world behind the stats screen
        Graphics2D g2 = (Graphics2D) g;
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        g.setColor(new Color(15,25,35));
        g.fillRect(0, 0, Renderer.gameWidth, Renderer.gameHeight);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        //styles
        int panelWidth = 420;
        int panelHeight = 470;
        int panelX = (Renderer.gameWidth - panelWidth) / 2;
        int panelY = (Renderer.gameHeight - panelHeight) / 2;
        int borderRadius = 20;
        int margin = 30;
        int lineHeight = 28;

        //panel container
        g.setColor(Color.WHITE);
        g.fillRoundRect(panelX - 1, panelY - 1, panelWidth + 2, panelHeight + 2, borderRadius, borderRadius);
        g.setColor(new Color(230,240,85)); //yellow #e6f055
        g.fillRoundRect(panelX, panelY, panelWidth, panelHeight, borderRadius, borderRadius);
        g.setColor(new Color(25,25,25)); //black #191919
        g.fillRoundRect(panelX + 3, panelY + 3, panelWidth - 6, panelHeight - 6, borderRadius, borderRadius);

        int textX = panelX + margin;
        int valueX = panelX + panelWidth - margin - 20;
        int currentY = panelY + margin + 10;

        //title
        g.setFont(new Font("Tahoma", Font.BOLD, 28));
        g.setColor(Color.WHITE);
        g.drawString("Player Stats", textX, currentY);
        currentY += 16;

        //level
        drawStat(g, textX, valueX, currentY, "Level", "" + player.level, new Color(230,240,85));
        currentY += lineHeight;

        //health
        int hc = (int)(((float)player.health / (float)player.maxHealth) * 100);
        drawBar(g, textX, currentY + 2, panelWidth - margin * 2, 18, hc, hb100);
        g.setFont(new Font("Tahoma", Font.PLAIN, 12));
        g.setColor(Color.WHITE);
        g.drawString("Health: " + player.health + "/" + player.maxHealth, textX, currentY);
        currentY += lineHeight + 6;

        //mana
        int mc = (int)(((float)player.mana / (float)player.maxMana) * 100);
        drawBar(g, textX, currentY + 2, panelWidth - margin * 2, 18, mc, mb100);
        g.setFont(new Font("Tahoma", Font.PLAIN, 12));
        g.setColor(Color.WHITE);
        g.drawString("Mana: " + player.mana + "/" + player.maxMana, textX, currentY);
        currentY += lineHeight + 6;

        //experience
        int xc = (int)(((float)(player.experience - player.xpToLastLevel) / (float)(player.xpToLevel - player.xpToLastLevel)) * 100);
        drawBar(g, textX, currentY + 2, panelWidth - margin * 2, 18, xc, xb100);
        g.setFont(new Font("Tahoma", Font.PLAIN, 12));
        g.setColor(Color.WHITE);
        g.drawString("Experience: " + (player.experience - player.xpToLastLevel) + "/" + (player.xpToLevel - player.xpToLastLevel), textX, currentY);
        currentY += lineHeight + 6;

        //gold
        drawStat(g, textX, valueX, currentY, "Gold", "$" + player.playerGold, new Color(230,240,85));
        currentY += lineHeight;

        //ability
        String abilityString = (player.spellChoice == 1) ? "Bolt" : "Knife";
        drawStat(g, textX, valueX, currentY, "Ability", abilityString, new Color(230,240,85));
        currentY += lineHeight;

        //ability slots
        drawStat(g, textX, valueX, currentY, "Ability Slots", "" + player.abilitySlots, new Color(230,240,85));
        currentY += lineHeight;

        //items
        g.setFont(new Font("Tahoma", Font.BOLD, 14));
        g.setColor(new Color(230,240,85));
        g.drawString("Items (" + player.myItems.size() + "/" + player.itemLimit + ")", textX, currentY + 6);
        currentY += 16;

        int itemSize = 32;
        int itemGap = 6;
        for (int i = 0; i < player.myItems.size(); i++) {
            BufferedImage image = player.myItems.get(i).animations[player.myItems.get(i).currentAnimation].getImage();
            g.drawImage(image, textX + (i * (itemSize + itemGap)), currentY, itemSize, itemSize, null);
        }
        currentY += itemSize + 10;

        //hint
        g.setFont(new Font("Tahoma", Font.PLAIN, 12));
        g.setColor(Color.GRAY);
        String hint = "TAB or ESC to exit";
        int hintWidth = g.getFontMetrics().stringWidth(hint);
        g.drawString(hint, panelX + (panelWidth - hintWidth) / 2, panelY + panelHeight - 15);
    }
}
