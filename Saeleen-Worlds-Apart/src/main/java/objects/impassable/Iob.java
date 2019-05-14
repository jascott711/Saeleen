package objects.impassable;

import java.awt.Rectangle;
import java.io.IOException;

import graphics.Animation;
import graphics.Renderer;
import objects.Sprite;
import world.World;

/**
 * IOb
 * impassable object
 */
public class Iob extends Sprite {

    public Iob(float posX, float posY) {
        super(posX, posY);
    }

    @Override
    public void update (float deltaTime) {
        super.update(deltaTime);
        
        dimensions.x = (int) getPosX() - dimensions.width / 2;
        dimensions.y = (int) getPosY() - dimensions.height / 2;
    }
    
}