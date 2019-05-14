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

    public Tree(float posX, float posY) {
        super(posX, posY);

        width = 105;
        height = 132;
        dimensions = new Rectangle(0, 0, width, height);
        vision = new Rectangle((int)posX - width, (int) posY - height, width * 2, height * 2);

        showDimensions = false;

        Animation anim = new Animation();
        try {
            anim.images.add(Renderer.loadImage("/images/tree.png"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        animations = new Animation [] {
            anim
        };
    }



}