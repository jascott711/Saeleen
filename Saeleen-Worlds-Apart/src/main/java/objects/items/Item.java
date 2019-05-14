package objects.items;

import java.awt.Color;
import java.awt.Graphics;

import objects.Mob;
import objects.Player;
import world.World;

/**
 * Item
 */
public class Item extends Mob {

    public String itemDesc = null;
    public boolean isBeingHeld = false;
    public boolean isConsumable = false;

    public Item(float posX, float posY) {
        super(posX, posY);
    }

    public void pickItemUp(Player player) {
        player.myItems.add(this);
    }
    
    public void useItem(Player player) {

    }

    @Override
    public void render (Graphics g) {
        super.render(g); 
        
        if (doesCollide(World.currentPlayer) && !isBeingHeld) {
            drawAction(g, "Pick Up");
        }
    }
}