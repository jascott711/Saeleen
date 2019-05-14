package graphics;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Animation
 */
public class Animation {

    public ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
    public int currentImage = 0;

    public int fps = 1;

    private long lastTime = 0;


    public ArrayList<BufferedImage> getImages() {
        return this.images;
    }
    public void setImages(ArrayList<BufferedImage> images) {
        this.images = images;
    }

    public int getCurrentImage() {
        return this.currentImage;
    }
    public void setCurrentImage(int currentImage) {
        this.currentImage = currentImage;
    }

    public int getFps() {
        return this.fps;
    }
    public void setFps(int fps) {
        this.fps = fps;
    }

    public long getLastTime() {
        return this.lastTime;
    }
    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }


    public void playAnimation () {
        if (System.nanoTime() > lastTime + (1000000000) / fps) {
            currentImage++;
            if (currentImage >= images.size()) {
                currentImage = 0;
            }
            lastTime = System.nanoTime();
        }
    }
    public void playAnimationOnce () {
        if (System.nanoTime() > lastTime + (1000000000) / fps) {
            currentImage++;
            if (currentImage >= images.size()) {
                currentImage = 0;
            }
            lastTime = System.nanoTime();
        }
    }

    public BufferedImage getImage() {
        if (images.size() > currentImage) {
            return images.get(currentImage);
        }
        return null;
    }

}