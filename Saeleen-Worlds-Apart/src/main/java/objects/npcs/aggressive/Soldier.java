package objects.npcs.aggressive;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

import graphics.Animation;
import graphics.Renderer;
import objects.Sprite;
import objects.abilities.Bullet;
import objects.npcs.Npc;
import world.World;

/**
 * Soldier
 */
public class Soldier extends Enemy {
	   
    private float startPosX;
    private float startPosY;

    public Soldier() {
    }

    public Soldier(float posX, float posY) {
        super(posX, posY);

        startPosX = posX;
        startPosY = posY;

        width = 64;
        height = 64;
        dimensions = new Rectangle(0, 0, width, height);
        vision = new Rectangle((int) posX - width, (int) posY - height, width * 2, height * 2);

        showDimensions = false;
        showVision = false;

        try {
            playerChatImage = Renderer.loadImage("/images/clara-chat.png");

            // #region player default
            // import image
            BufferedImage spriteSheet = ImageIO
                    .read(new File(getClass().getResource("/images/soldier-walking.png").toURI()));
            ;

            // set image cell size
            int rows = 1;
            int cols = 8;
            BufferedImage[] spriteSheetImagesUp = new BufferedImage[rows * cols];
            BufferedImage[] spriteSheetImagesDown = new BufferedImage[rows * cols];
            BufferedImage[] spriteSheetImagesLeft = new BufferedImage[rows * cols];
            BufferedImage[] spriteSheetImagesRight = new BufferedImage[rows * cols];

            // add each cell to an array as per each direction
            for (int j = 0; j < cols; j++) {
                spriteSheetImagesUp[j] = spriteSheet.getSubimage(j * width, 0 * height, width, height);
            }
            for (BufferedImage image : spriteSheetImagesUp) {
                animUp.images.add(image);
            }
            animUp.setFps(8);

            for (int j = 0; j < cols; j++) {
                spriteSheetImagesLeft[j] = spriteSheet.getSubimage(j * width, 1 * height, width, height);
            }
            for (BufferedImage image : spriteSheetImagesLeft) {
                animLeft.images.add(image);
            }
            animLeft.setFps(8);

            for (int j = 0; j < cols; j++) {
                spriteSheetImagesDown[j] = spriteSheet.getSubimage(j * width, 2 * height, width, height);
            }
            for (BufferedImage image : spriteSheetImagesDown) {
                animDown.images.add(image);
            }
            animDown.setFps(8);

            for (int j = 0; j < cols; j++) {
                spriteSheetImagesRight[j] = spriteSheet.getSubimage(j * width, 3 * height, width, height);
            }
            for (BufferedImage image : spriteSheetImagesRight) {
                animRight.images.add(image);
            }
            animRight.setFps(8);
            // #endregion

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        animations = new Animation[] {
            animLeft, animRight, animUp, animDown, // 0, 1, 2, 3
        };


        //stats
    	level = 1;
    	health = 50;
		dmg = 10;
		xpGiven = 50;

    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    public void render (Graphics g) {
        super.render(g);    
    }




    //get and draw chat window
    @Override
    public void speak() {
        //don't talk to bad guys
        return;
    }
}