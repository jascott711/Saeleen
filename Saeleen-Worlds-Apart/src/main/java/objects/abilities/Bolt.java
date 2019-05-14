package objects.abilities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

import graphics.Animation;
import graphics.Renderer;

/**
 * Bolt
 */
public class Bolt extends Bullet {

    public Bolt(float posX, float posY, int direction) {
        super(posX, posY, direction);
        
        damage = 25;

        width = 36;
        height = 20;
        dimensions = new Rectangle(0, 0, width, height);

        showDimensions = false;

        Animation anim = new Animation();
        anim.setFps(4);
        try {
            // anim.images.add(Renderer.loadImage("/images/clara.png"));
            BufferedImage spriteSheet = ImageIO
                    .read(new File(getClass().getResource("/images/bolt.png").toURI()));
            ;
            // The above line throws an checked IOException which must be caught.

            final int rows = 1;
            final int cols = 4;
            BufferedImage[] spriteSheetImages = new BufferedImage[rows * cols];

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    spriteSheetImages[(i * cols) + j] = spriteSheet.getSubimage(j * width, i * height, width, height);
                }
            }

            for (BufferedImage image : spriteSheetImages) {
                anim.images.add(image);
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        animations = new Animation [] {
            anim
        };
    }
}