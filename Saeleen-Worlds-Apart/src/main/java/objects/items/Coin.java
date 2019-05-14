package objects.items;

import java.awt.Rectangle;
import java.io.IOException;

import graphics.Animation;
import graphics.Renderer;
import objects.Mob;
import objects.Player;
import world.World;

/**
 * Coin
 */
public class Coin extends Mob {

    public int value;

    public Coin(float posX, float posY) {
        super(posX, posY);

        value = 10;

        width = 12;
        height = 12;
        dimensions = new Rectangle(0, 0, width, height);
        vision = new Rectangle((int)posX - width, (int) posY - height, width * 2, height * 2);

        showDimensions = false;

        Animation anim = new Animation();
        try {
            anim.images.add(Renderer.loadImage("/images/coin.png"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        animations = new Animation [] {
            anim
        };
    }

    public void pickItemUp(Player player) {
        World.currentPlayer.playerGold += value;
    }
    
}
