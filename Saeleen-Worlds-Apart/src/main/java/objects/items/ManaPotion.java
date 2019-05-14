package objects.items;

import java.awt.Rectangle;
import java.io.IOException;

import graphics.Animation;
import graphics.Renderer;
import objects.Mob;
import objects.Player;
import world.World;

/**
 * ManaPotion
 */
public class ManaPotion extends Item {

    public ManaPotion(float posX, float posY) {
        super(posX, posY);

        itemDesc = "Mana Potion";
        isConsumable = true;

        width = 16;
        height = 16;
        dimensions = new Rectangle(0, 0, width, height);
        vision = new Rectangle((int)posX - width, (int) posY - height, width * 2, height * 2);

        showDimensions = false;

        Animation anim = new Animation();
        try {
            anim.images.add(Renderer.loadImage("/images/potion.png"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        animations = new Animation [] {
            anim
        };
    }


    
    @Override
    public void useItem(Player player) {
        if (player.getMana() < player.getMaxMana()) {
            player.setMana(player.getMana() + 50);
            if (player.getMana() > player.getMaxMana()) {
                player.setMana(player.getMaxMana());
            }
        }
        super.useItem(player);
    }
    
}