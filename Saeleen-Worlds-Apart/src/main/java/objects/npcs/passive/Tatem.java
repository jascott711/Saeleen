package objects.npcs.passive;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
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

    private int boundsWidth = 30;
    private int boundsHeight = 20;
    private int boundsYOffset = 0;

    public Tatem(float posX, float posY) {
        super(posX, posY);
        width = 50;
        height = 60;
        dimensions = new Rectangle(0, 0, boundsWidth, boundsHeight);
        updateBounds();
        vision = new Rectangle((int)posX - width, (int) posY - height, width * 2, height * 2);

        showDimensions = true;
        showVision = true;

        Animation anim = new Animation();
        try {
            BufferedImage raw = Renderer.loadImage("/images/tatem.png");
            BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            scaled.createGraphics().drawImage(raw, 0, 0, width, height, null);
            anim.images.add(scaled);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        animations = new Animation [] {
            anim
        };

        npcName = "Tatem";

        ArrayList<String> story = new ArrayList<String>();
        story.add("Lorem ipsum dolor sit amet, consectetur");
        story.add("C: dolor sit amet, consectetur?");
        story.add("adipiscing elit, sed do eiusmod tempor");
        storyBlocks.add(story);

        ArrayList<String> storyFollowUp = new ArrayList<String>();
        storyFollowUp.add("C: incididunt ut labore et dolore magna");
        storyFollowUp.add("aliqua. Ut enim ad minim veniam");
        storyFollowUp.add("C: quis nostrud exercitation?");
        storyBlocks.add(storyFollowUp);

        ArrayList<String> firstDialog = new ArrayList<String>();
        firstDialog.add("Lorem ipsum dolor sit amet");
        firstDialog.add("consectetur adipiscing elit");
        dialogBlocks.add(firstDialog);

        ArrayList<String> secondDialog = new ArrayList<String>();
        secondDialog.add("Sed do eiusmod tempor incididunt");
        secondDialog.add("ut labore et dolore magna aliqua");
        dialogBlocks.add(secondDialog);

        ArrayList<String> thirdDialog = new ArrayList<String>();
        thirdDialog.add("Ut enim ad minim veniam");
        thirdDialog.add("quis nostrud exercitation ullamco");
        dialogBlocks.add(thirdDialog);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        updateBounds();
    }

    private void updateBounds() {
        dimensions.setBounds(
            (int) posX - dimensions.width / 2,
            (int) posY - dimensions.height / 2 + boundsYOffset,
            dimensions.width, dimensions.height
        );
    }

    @Override
    public void render (Graphics g) {
        super.render(g);

        if (doesCollide(World.currentPlayer)) {
            drawAction(g, "Speak");
        }
    }


    //get and draw chat window
    @Override
    public void speak() {
        super.speak();
    }
}
