package objects.items;

import java.awt.Rectangle;
import java.io.IOException;

import graphics.Animation;
import graphics.Renderer;
import objects.Mob;
import objects.Player;
import world.World;

/**
 * Apple
 */
public class Apple extends Item {
   
    public Apple(float posX, float posY) {
        super(posX, posY);

        itemDesc = "Apple";
        isConsumable = true;

        width = 12;
        height = 12;
        dimensions = new Rectangle(0, 0, width, height);
        vision = new Rectangle((int)posX - width, (int) posY - height, width * 2, height * 2);

        showDimensions = false;

        Animation anim = new Animation();
        try {
            anim.images.add(Renderer.loadImage("/images/apple.png"));
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
        if (player.getHealth() < player.getMaxHealth()) {
            player.setHealth(player.getHealth() + 20);
            if (player.getHealth() > player.getMaxHealth()) {
                player.setHealth(player.getMaxHealth());
            }
        }
        super.useItem(player);
    }
}