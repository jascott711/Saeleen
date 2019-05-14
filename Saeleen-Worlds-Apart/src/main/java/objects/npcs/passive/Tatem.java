package objects.npcs.passive;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;

import graphics.Animation;
import graphics.Renderer;
import objects.npcs.Npc;
import world.World;

/**
 * Tatem
 */
public class Tatem extends Npc {

    public Tatem(float posX, float posY) {
        super(posX, posY);
        width = 40;
        height = 45;
        dimensions = new Rectangle(0,0,width,height);
        vision = new Rectangle((int)posX - width, (int) posY - height, width * 2, height * 2);

        showDimensions = false;
        showVision = false;

        Animation anim = new Animation();
        try {
            anim.images.add(Renderer.loadImage("/images/tatem.png"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        animations = new Animation [] {
            anim
        };

        playerSpeech.add("Hello");
        playerSpeech.add("Where is Noah?");
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    public void render (Graphics g) {
        super.render(g); 
        
        if (inVisionOf(World.currentPlayer)) {
            drawAction(g, "Speak");
        }
    }

    
    //get and draw chat window
    @Override
    public void speak() {
        super.speak();
    }
}