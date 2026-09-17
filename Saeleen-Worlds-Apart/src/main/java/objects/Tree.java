package objects;

import java.awt.Rectangle;
import java.io.IOException;

import graphics.Animation;
import graphics.Renderer;
import objects.impassable.Iob;

/**
 * Tree
 */
public class Tree extends Iob {

    private int canopyWidth = 60, canopyHeight = 60, canopyYOffset = -20; // horizontal bar
    private int trunkWidth = 20, trunkHeight = 40, trunkYOffset = 20;    // vertical bar

    private Rectangle canopyBox = new Rectangle();
    private Rectangle trunkBox = new Rectangle();

    public Tree(float posX, float posY) {
        super(posX, posY);

        width = 105;
        height = 132;

        hitboxes = new Rectangle[] { canopyBox, trunkBox };
        updateHitboxes();

        vision = new Rectangle((int) posX - width, (int) posY - height, width * 2, height * 2);
        showDimensions = true;

        Animation anim = new Animation();
        try {
            anim.images.add(Renderer.loadImage("/images/tree.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        animations = new Animation[] { anim };
    }

    private void updateHitboxes() {
        canopyBox.setBounds(
            (int) posX - canopyWidth / 2, (int) posY - canopyHeight / 2 + canopyYOffset,
            canopyWidth, canopyHeight
        );
        trunkBox.setBounds(
            (int) posX - trunkWidth / 2, (int) posY - trunkHeight / 2 + trunkYOffset,
            trunkWidth, trunkHeight
        );
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        updateHitboxes();
    }
}
