package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import graphics.Renderer;
import objects.Player;
import world.World;

/**
 * DisplayControls
 */
public class DisplayControls extends UIComponent {
    
    private Player player;

    public DisplayControls(Player player) {
        this.player = player;
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
        //controls
        //styles
        ArrayList<String> controls = new ArrayList<String>();
        controls.add("Controls");
        controls.add("ESC - Quit");
        controls.add("W A S D - Move");
        controls.add("Hold Shift - Run");
        controls.add("L - Attack");
        controls.add("O - Prev Attack");
        controls.add("P - Next Attack");
        controls.add("1/6 - Use Item");
        int titleFontSize = 18;
        int FontSize = 14;
        int lineHeight = 24;
        int cellHeight = 24;
        int containerWidth = 120;
        int containerHeight = cellHeight * controls.size() + 10;
        int borderRadius = 20;
        Rectangle rect = new Rectangle(Renderer.gameWidth - containerWidth, Renderer.gameHeight - containerHeight, containerWidth, containerHeight);
        g.setColor(Color.WHITE);
        g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, borderRadius, borderRadius);
        g.setColor(new Color(25,25,25)); //black #191919;
        g.fillRoundRect(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2, borderRadius, borderRadius);

        g.setColor(Color.WHITE);
        for (int i = 0; i < controls.size(); i++) {
            if (i < 1) {
                g.setFont( new Font("Tahoma", Font.BOLD, titleFontSize));
            } else {
                g.setFont( new Font("Tahoma", Font.PLAIN, FontSize));
            }
            g.drawString(controls.get(i), rect.x + 10, rect.y + lineHeight + (cellHeight * i));
        }

    }
}