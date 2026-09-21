package ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import graphics.Animation;
import graphics.Renderer;
import input.Input;
import objects.Player;
import objects.abilities.Bolt;
import objects.abilities.Knife;
import objects.items.Item;
import world.World;

/**
 * StatsScreen
 * fullscreen overlay showing the player's stats, opened with TAB
 */
public class StatsScreen extends UIComponent {

    protected Player player;

    private Map<String, BufferedImage> abilityIcons = new HashMap<String, BufferedImage>();
    private Map<String, Animation> abilityAnims = new HashMap<String, Animation>();
    private Map<String, String> abilityDescriptions = new HashMap<String, String>();
    private Map<String, String[]> abilityStats = new HashMap<String, String[]>();
    private int selectedRow = 0; //0 = Ability 1, 1 = Ability 2
    private int selectedItem = 0;
    private int currentTab = 0; //0 = Stats, 1 = Abilities, 2 = Items
    private static final int TAB_COUNT = 3;

    public StatsScreen(Player player) {
        this.player = player;
        loadAbilityIcons();
        loadAbilityAnims();
        loadAbilityDescriptions();
    }

    private void loadAbilityIcons() {
        abilityIcons.put("Knife", new Knife(0, 0, 0).animations[0].getImage());
        abilityIcons.put("Bolt", new Bolt(0, 0, 0).animations[0].getImage());
        try {
            BufferedImage wingSheet = Renderer.loadImage("/images/wing.png");
            abilityIcons.put("Dash", wingSheet.getSubimage(0, 0, wingSheet.getWidth() / 4, wingSheet.getHeight()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAbilityAnims() {
        abilityAnims.put("Knife", new Knife(0, 0, 0).animations[0]);
        abilityAnims.put("Bolt", new Bolt(0, 0, 0).animations[0]);
        try {
            BufferedImage wingSheet = Renderer.loadImage("/images/wing.png");
            Animation runAnim = new Animation();
            int frameWidth = wingSheet.getWidth() / 4;
            int frameHeight = wingSheet.getHeight();
            for (int j = 0; j < 4; j++) {
                runAnim.images.add(wingSheet.getSubimage(j * frameWidth, 0, frameWidth, frameHeight));
            }
            runAnim.setFps(8);
            abilityAnims.put("Dash", runAnim);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAbilityDescriptions() {
        abilityDescriptions.put("Knife", "Throw a swift blade at your foe in the direction you face.");
        abilityDescriptions.put("Bolt", "Unleash a magical bolt that blasts your enemies in the direction you face.");
        abilityDescriptions.put("Dash", "Hold SHIFT to dash into a burst of speed. Drains mana while moving.");

        abilityStats.put("Knife", new String[] {"10 dmg", "0 mp", "1s cd"});
        abilityStats.put("Bolt", new String[] {"25 dmg", "2 mp", "3s cd"});
        abilityStats.put("Dash", new String[] {"3x speed", "1 mp", "2s cd"});
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public void update(float deltaTime) {
        if (!World.showStats) {
            return;
        }

        if (player.myItems.isEmpty()) {
            selectedItem = 0;
        } else if (selectedItem > player.myItems.size() - 1) {
            selectedItem = player.myItems.size() - 1;
        }

        //cycle between menu tabs
        if (Input.getKeyDown(KeyEvent.VK_Q)) {
            currentTab = (currentTab - 1 + TAB_COUNT) % TAB_COUNT;
        }
        if (Input.getKeyDown(KeyEvent.VK_E)) {
            currentTab = (currentTab + 1) % TAB_COUNT;
        }

        if (currentTab == 1) { //Abilities tab
            if (Input.getKeyDown(KeyEvent.VK_UP) || Input.getKeyDown(KeyEvent.VK_W)) {
                selectedRow = 0;
            }
            if (Input.getKeyDown(KeyEvent.VK_DOWN) || Input.getKeyDown(KeyEvent.VK_S)) {
                selectedRow = 1;
            }
            if (Input.getKeyDown(KeyEvent.VK_LEFT) || Input.getKeyDown(KeyEvent.VK_A)) {
                if (selectedRow == 0) {
                    player.spellChoice = player.nextValidIndex(player.abilityCycle1, player.spellChoice, -1);
                } else {
                    player.spellChoice2 = player.nextValidIndex(player.abilityCycle2, player.spellChoice2, -1);
                }
            }
            if (Input.getKeyDown(KeyEvent.VK_RIGHT) || Input.getKeyDown(KeyEvent.VK_D)) {
                if (selectedRow == 0) {
                    player.spellChoice = player.nextValidIndex(player.abilityCycle1, player.spellChoice, 1);
                } else {
                    player.spellChoice2 = player.nextValidIndex(player.abilityCycle2, player.spellChoice2, 1);
                }
            }
            tickAbilityPreview();
        } else if (currentTab == 2) { //Items tab
            if ((Input.getKeyDown(KeyEvent.VK_LEFT) || Input.getKeyDown(KeyEvent.VK_A)) && !player.myItems.isEmpty()) {
                selectedItem = (selectedItem - 1 + player.myItems.size()) % player.myItems.size();
            }
            if ((Input.getKeyDown(KeyEvent.VK_RIGHT) || Input.getKeyDown(KeyEvent.VK_D)) && !player.myItems.isEmpty()) {
                selectedItem = (selectedItem + 1) % player.myItems.size();
            }
            if (Input.getKeyDown(KeyEvent.VK_ENTER) && !player.myItems.isEmpty()) {
                Item item = player.myItems.get(selectedItem);
                if (item.isConsumable) {
                    item.useItem(player);
                    player.myItems.remove(selectedItem);
                } else {
                    item.useItem(player);
                }
            }
        }
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

    private void drawAbilitySlot(Graphics g, int x, int y, int width, int height, String abilityName, boolean selected, boolean active) {
        boolean locked = abilityName != null && !abilityName.isEmpty() && !player.isAbilityUnlocked(abilityName);
        g.setColor(active ? new Color(230,240,85) : Color.WHITE);
        g.fillRoundRect(x - 1, y - 1, width + 2, height + 2, 10, 10);
        if (active) {
            g.setColor(new Color(230,240,85));
            g.drawRoundRect(x - 2, y - 2, width + 4, height + 4, 12, 12);
        }
        if (locked) {
            g.setColor(new Color(200, 30, 30));
        } else if (selected) {
            g.setColor(new Color(35,35,35));
        } else {
            g.setColor(new Color(25,25,25));
        }
        g.fillRoundRect(x, y, width, height, 10, 10);
        BufferedImage icon = abilityIcons.get(abilityName);
        if (locked) {
            String label = "LOCKED";
            g.setColor(Color.WHITE);
            g.setFont(new Font("Tahoma", Font.BOLD, 12));
            int labelWidth = g.getFontMetrics().stringWidth(label);
            g.drawString(label, x + width / 2 - labelWidth / 2, y + height / 2 + 4);
        } else if (icon == null || abilityName == null || abilityName.isEmpty()) {
            String label = "EMPTY";
            g.setColor(selected ? new Color(230,240,85) : Color.GRAY);
            g.setFont(new Font("Tahoma", Font.BOLD, 12));
            int labelWidth = g.getFontMetrics().stringWidth(label);
            g.drawString(label, x + width / 2 - labelWidth / 2, y + height / 2 + 4);
        } else {
            int maxIconWidth = width - 8;
            int maxIconHeight = height - 8;
            float scale = Math.min((float) maxIconWidth / icon.getWidth(), (float) maxIconHeight / icon.getHeight());
            int drawWidth = (int) (icon.getWidth() * scale);
            int drawHeight = (int) (icon.getHeight() * scale);
            g.drawImage(icon, x + width / 2 - drawWidth / 2, y + height / 2 - drawHeight / 2, drawWidth, drawHeight, null);
        }
    }

    private void drawItemSlot(Graphics g, int x, int y, int size, BufferedImage image, boolean selected, boolean active) {
        g.setColor(active ? new Color(230,240,85) : Color.WHITE);
        g.fillRoundRect(x - 2, y - 2, size + 4, size + 4, 8, 8);
        if (active) {
            g.setColor(new Color(230,240,85));
            g.drawRoundRect(x - 3, y - 3, size + 6, size + 6, 10, 10);
        }
        g.setColor(new Color(25,25,25));
        g.fillRoundRect(x, y, size, size, 8, 8);
        g.drawImage(image, x, y, size, size, null);
    }

    private void drawTabs(Graphics g, int textX, int y, int availableWidth) {
        String[] tabNames = {"Stats", "Abilities", "Items"};
        int tabGap = 4;
        int tabHeight = 26;
        int tabWidth = (availableWidth - tabGap * (TAB_COUNT - 1)) / TAB_COUNT;
        for (int t = 0; t < TAB_COUNT; t++) {
            int tx = textX + t * (tabWidth + tabGap);
            boolean active = (t == currentTab);
            g.setColor(active ? new Color(230,240,85) : Color.WHITE);
            g.fillRoundRect(tx - 1, y - 1, tabWidth + 2, tabHeight + 2, 8, 8);
            g.setColor(active ? new Color(230,240,85) : new Color(35,35,35));
            g.fillRoundRect(tx, y, tabWidth, tabHeight, 8, 8);
            g.setFont(new Font("Tahoma", Font.BOLD, 12));
            g.setColor(active ? new Color(15,25,35) : Color.WHITE);
            int labelWidth = g.getFontMetrics().stringWidth(tabNames[t]);
            g.drawString(tabNames[t], tx + tabWidth / 2 - labelWidth / 2, y + tabHeight / 2 + 4);
        }
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
        int availableWidth = panelWidth - margin * 2;
        int valueX = panelX + panelWidth - margin - 20;
        int currentY = panelY + margin + 10;

        //title
        g.setFont(new Font("Tahoma", Font.BOLD, 28));
        g.setColor(Color.WHITE);
        g.drawString("Player Stats", textX, currentY);
        currentY += 16;

        //tabs
        drawTabs(g, textX, currentY, availableWidth);
        currentY += 44;

        if (currentTab == 0) { //Stats tab
            //level
            drawStat(g, textX, valueX, currentY, "Level", "" + player.level, new Color(230,240,85));
            currentY += lineHeight;

            //health
            int hc = (int)(((float)player.health / (float)player.maxHealth) * 100);
            drawBar(g, textX, currentY + 2, availableWidth, 18, hc, hb100);
            g.setFont(new Font("Tahoma", Font.PLAIN, 12));
            g.setColor(Color.WHITE);
            g.drawString("Health: " + player.health + "/" + player.maxHealth, textX, currentY);
            currentY += lineHeight + 6;

            //mana
            int mc = (int)(((float)player.mana / (float)player.maxMana) * 100);
            drawBar(g, textX, currentY + 2, availableWidth, 18, mc, mb100);
            g.setFont(new Font("Tahoma", Font.PLAIN, 12));
            g.setColor(Color.WHITE);
            g.drawString("Mana: " + player.mana + "/" + player.maxMana, textX, currentY);
            currentY += lineHeight + 6;

            //experience
            int xc = (int)(((float)(player.experience - player.xpToLastLevel) / (float)(player.xpToLevel - player.xpToLastLevel)) * 100);
            drawBar(g, textX, currentY + 2, availableWidth, 18, xc, xb100);
            g.setFont(new Font("Tahoma", Font.PLAIN, 12));
            g.setColor(Color.WHITE);
            g.drawString("Experience: " + (player.experience - player.xpToLastLevel) + "/" + (player.xpToLevel - player.xpToLastLevel), textX, currentY);
            currentY += lineHeight + 6;

            //gold
            drawStat(g, textX, valueX, currentY, "Gold", "$" + player.playerGold, new Color(230,240,85));
            currentY += lineHeight;
        } else if (currentTab == 1) { //Abilities tab
            //ability cycles (kept separate: ability 1 and ability 2)
            int slotCount = Math.max(player.abilityCycle1.length, player.abilityCycle2.length);
            int slotGap = 8;
            int slotWidth = (availableWidth - slotGap * (slotCount - 1)) / slotCount;
            int slotHeight = 30;

            g.setFont(new Font("Tahoma", Font.BOLD, 14));
            g.setColor(new Color(230,240,85));
            g.drawString((selectedRow == 0 ? "> " : "  ") + "Ability 1", textX, currentY + 6);
            currentY += 20;
            for (int i = 0; i < player.abilityCycle1.length; i++) {
                drawAbilitySlot(g, textX + i * (slotWidth + slotGap), currentY, slotWidth, slotHeight, player.abilityCycle1[i], player.spellChoice == i, selectedRow == 0 && player.spellChoice == i);
            }
            currentY += slotHeight + 16;

            g.setFont(new Font("Tahoma", Font.BOLD, 14));
            g.setColor(new Color(230,240,85));
            g.drawString((selectedRow == 1 ? "> " : "  ") + "Ability 2", textX, currentY + 6);
            currentY += 20;
            for (int i = 0; i < player.abilityCycle2.length; i++) {
                drawAbilitySlot(g, textX + i * (slotWidth + slotGap), currentY, slotWidth, slotHeight, player.abilityCycle2[i], player.spellChoice2 == i, selectedRow == 1 && player.spellChoice2 == i);
            }
            currentY += slotHeight + 10;

            String previewAbility = (selectedRow == 0) ? player.abilityCycle1[player.spellChoice] : player.abilityCycle2[player.spellChoice2];
            int descY = currentY + 8;
            int descHeight = (panelY + panelHeight - 32) - descY;
            drawAbilityDescription(g, textX, descY, availableWidth, descHeight, previewAbility);
        } else { //Items tab
            g.setFont(new Font("Tahoma", Font.BOLD, 14));
            g.setColor(new Color(230,240,85));
            g.drawString("Items (" + player.myItems.size() + "/" + player.itemLimit + ")", textX, currentY + 6);
            currentY += 16;

            int itemSize = 32;
            int itemGap = 6;
            for (int i = 0; i < player.myItems.size(); i++) {
                BufferedImage image = player.myItems.get(i).animations[player.myItems.get(i).currentAnimation].getImage();
                drawItemSlot(g, textX + (i * (itemSize + itemGap)), currentY, itemSize, image, selectedItem == i, selectedItem == i);
            }
            currentY += itemSize + 10;
        }

        //hint
        g.setFont(new Font("Tahoma", Font.PLAIN, 12));
        g.setColor(Color.GRAY);
        String hint = "Q/E tabs | Arrows to select | ENTER to use item | TAB or ESC to exit";
        int hintWidth = g.getFontMetrics().stringWidth(hint);
        g.drawString(hint, panelX + (panelWidth - hintWidth) / 2, panelY + panelHeight - 15);
    }

    private void tickAbilityPreview() {
        Animation anim = abilityAnims.get(getSelectedAbilityName());
        if (anim != null && !anim.getImages().isEmpty()) {
            if (System.nanoTime() > anim.getLastTime() + (1000000000L) / anim.getFps()) {
                anim.setCurrentImage((anim.getCurrentImage() + 1) % anim.getImages().size());
                anim.setLastTime(System.nanoTime());
            }
        }
    }

    private String getSelectedAbilityName() {
        if (currentTab != 1) {
            return null;
        }
        return (selectedRow == 0) ? player.abilityCycle1[player.spellChoice] : player.abilityCycle2[player.spellChoice2];
    }

    private void drawAbilityStatLine(Graphics g, String abilityName, int x, int y) {
        g.setFont(new Font("Tahoma", Font.PLAIN, 12));
        if (abilityName == null || abilityName.isEmpty()) {
            return;
        }
        if (!player.isAbilityUnlocked(abilityName)) {
            g.setColor(Color.WHITE);
            g.drawString("Unlocks at level " + player.abilityLevelRequired(abilityName), x, y);
            return;
        }
        String[] segments = abilityStats.get(abilityName);
        if (segments == null) {
            return;
        }
        int cursor = x;
        for (int i = 0; i < segments.length; i++) {
            if (i > 0) {
                g.setColor(Color.WHITE);
                g.drawString(" - ", cursor, y);
                cursor += g.getFontMetrics().stringWidth(" - ");
            }
            g.setColor(statSegmentColor(segments[i]));
            g.drawString(segments[i], cursor, y);
            cursor += g.getFontMetrics().stringWidth(segments[i]);
        }
    }

    private Color statSegmentColor(String segment) {
        if (segment.contains("dmg")) {
            return Color.RED;
        }
        if (segment.contains("mp")) {
            return UIComponent.mb100;
        }
        return Color.WHITE;
    }

    private String getAbilityDescription(String abilityName) {
        if (abilityName == null || abilityName.isEmpty()) {
            return "Empty slot. Equip an ability here.";
        }
        return abilityDescriptions.get(abilityName);
    }

    private void drawAbilityDescription(Graphics g, int x, int y, int width, int height, String abilityName) {
        //box
        g.setColor(Color.WHITE);
        g.fillRoundRect(x - 1, y - 1, width + 2, height + 2, 12, 12);
        g.setColor(new Color(25,25,25));
        g.fillRoundRect(x, y, width, height, 12, 12);

        //ability name header
        g.setFont(new Font("Tahoma", Font.BOLD, 14));
        if (abilityName == null || abilityName.isEmpty()) {
            g.setColor(Color.GRAY);
            g.drawString("No Ability", x + 12, y + 22);
        } else {
            g.setColor(new Color(230,240,85));
            g.drawString(abilityName, x + 12, y + 22);
        }

        //full-width stat cell below the name
        int statY = y + 28;
        int statHeight = 30;
        g.setColor(Color.GRAY);
        g.drawLine(x + 8, statY + statHeight - 4, x + width - 8, statY + statHeight - 4);
        drawAbilityStatLine(g, abilityName, x + 12, statY + 18);

        //content below the stat cell: first third sprite, remaining two thirds description
        int contentY = statY + statHeight;
        int contentHeight = height - (contentY - y);
        int spriteWidth = width / 3;
        Animation anim = abilityAnims.get(abilityName);
        BufferedImage img = (anim == null) ? null : anim.getImage();
        if (img != null) {
            int maxIconWidth = 54;
            int maxIconHeight = 72;
            float scale;
            if ("Dash".equals(abilityName)) {
                scale = Math.min((float) maxIconWidth / img.getWidth(), (float) maxIconHeight / img.getHeight()) * 0.8f;
            } else {
                scale = Math.min((float) maxIconWidth / img.getHeight(), (float) maxIconHeight / img.getWidth());
            }
            int drawWidth = (int) (img.getWidth() * scale);
            int drawHeight = (int) (img.getHeight() * scale);
            if ("Dash".equals(abilityName)) {
                g.drawImage(img, x + spriteWidth / 2 - drawWidth / 2, contentY + contentHeight / 2 - drawHeight / 2, drawWidth, drawHeight, null);
            } else {
                Graphics2D g2 = (Graphics2D) g;
                AffineTransform oldTransform = g2.getTransform();
                g2.translate(x + spriteWidth / 2, contentY + contentHeight / 2);
                g2.rotate(Math.toRadians(90));
                g2.drawImage(img, -drawWidth / 2, -drawHeight / 2, drawWidth, drawHeight, null);
                g2.setTransform(oldTransform);
            }
        } else {
            g.setFont(new Font("Tahoma", Font.PLAIN, 12));
            g.setColor(Color.GRAY);
            String placeholder = (abilityName == null || abilityName.isEmpty()) ? "No Ability" : "LOCKED";
            int pw = g.getFontMetrics().stringWidth(placeholder);
            g.drawString(placeholder, x + spriteWidth / 2 - pw / 2, contentY + contentHeight / 2 + 4);
        }

        //remaining two thirds: description, vertically centered
        int textX = x + spriteWidth + 12;
        int textWidth = width - spriteWidth - 12 - 30;
        String[][] lines = wrapText(g, colorify(getAbilityDescription(abilityName)), textWidth);
        int lineHeight = 16;
        int startY = contentY + (contentHeight - lines.length * lineHeight) / 2 + 13;
        drawColoredLines(g, lines, textX, startY, lineHeight);
    }

    private void drawColoredLines(Graphics g, String[][] lines, int x, int y, int lineHeight) {
        g.setFont(new Font("Tahoma", Font.PLAIN, 12));
        int startY = y;
        for (String[] line : lines) {
            int cursor = x;
            for (String token : line) {
                if (token.contains(COLOR_SEP)) {
                    String[] parts = token.split(COLOR_SEP);
                    g.setColor("mp".equals(parts[1]) ? UIComponent.mb100 : Color.RED);
                    g.drawString(parts[0], cursor, startY);
                    cursor += g.getFontMetrics().stringWidth(parts[0]);
                    g.drawString(" ", cursor, startY);
                    cursor += g.getFontMetrics().stringWidth(" ");
                    g.drawString(parts[1], cursor, startY);
                    cursor += g.getFontMetrics().stringWidth(parts[1]);
                } else {
                    g.setColor(Color.WHITE);
                    g.drawString(token, cursor, startY);
                    cursor += g.getFontMetrics().stringWidth(token);
                }
                cursor += g.getFontMetrics().stringWidth(" ");
            }
            startY += lineHeight;
        }
    }

    private static final String COLOR_SEP = "\u0001";

    private String colorify(String text) {
        return text.replaceAll("([0-9]+) (dmg|mp)", "$1" + COLOR_SEP + "$2");
    }

    private String[][] wrapText(Graphics g, String text, int maxWidth) {
        if (text == null) {
            return new String[][] { new String[] { "" } };
        }
        java.util.ArrayList<String[]> lines = new java.util.ArrayList<String[]>();
        java.util.ArrayList<String> line = new java.util.ArrayList<String>();
        StringBuilder lineStr = new StringBuilder();
        for (String word : text.split(" ")) {
            String trial = lineStr.length() == 0 ? word : lineStr + " " + word;
            if (g.getFontMetrics().stringWidth(trial) > maxWidth && lineStr.length() > 0) {
                lines.add(line.toArray(new String[0]));
                line = new java.util.ArrayList<String>();
                lineStr.setLength(0);
            }
            if (lineStr.length() > 0) {
                lineStr.append(" ");
            }
            lineStr.append(word);
            line.add(word);
        }
        if (lineStr.length() > 0) {
            lines.add(line.toArray(new String[0]));
        }
        return lines.toArray(new String[0][]);
    }
}
