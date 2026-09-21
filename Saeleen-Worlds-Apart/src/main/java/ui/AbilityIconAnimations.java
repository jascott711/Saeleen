package ui;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * AbilityIconAnimations
 * reusable per-ability icon motions for the ability window and the TAB menu,
 * so future abilities can reuse the same animations easily
 */
public final class AbilityIconAnimations {

    private static final float WAVE_PERIOD_SECONDS = 1.2f;
    private static final float GLIDE_LOOP_SECONDS = 3f;
    private static final int MAX_ICON_WIDTH = 54;
    private static final int MAX_ICON_HEIGHT = 72;

    //art is stored rotated 90 degrees, this un-rotates it like the rest of the UI
    private static final float ICON_TURN_DEGREES = 90f;

    private AbilityIconAnimations() {
    }

    /**
     * Glides the icon upward and out the top of the box, then loops.
     * Clipped 3px in from the edges so it cuts off cleanly at the border.
     */
    public static void drawGlideUp(Graphics2D g2, BufferedImage img, int boxX, int boxY, int boxW, int boxH, float time) {
        drawGlideUp(g2, img, boxX, boxY, boxW, boxH, time, 1f);
    }

    /**
     * Glides the icon upward and out the top of the box, then loops.
     * Clipped 3px in from the edges so it cuts off cleanly at the border.
     * sizeFactor shrinks the icon (e.g. 0.8f) when the art fits too large.
     */
    public static void drawGlideUp(Graphics2D g2, BufferedImage img, int boxX, int boxY, int boxW, int boxH, float time, float sizeFactor) {
        if (img == null) {
            return;
        }

        //fit the icon into the box, then shrink it so it has room to travel
        int fitW = (int) (fitWidth(boxW, boxH, img) * sizeFactor);
        int fitH = (int) (fitHeight(boxW, boxH, img) * sizeFactor);
        float travelScale = Math.min(1f, (boxH - 16f) / fitH);
        int drawW = (int) (fitW * travelScale);
        int drawH = (int) (fitH * travelScale);

        double t = (time % GLIDE_LOOP_SECONDS) / GLIDE_LOOP_SECONDS;
        int startY = boxY + boxH + drawH / 2;
        int endY = boxY - drawH / 2 + 4;
        int centerX = boxX + boxW / 2;
        int centerY = startY + (int) ((endY - startY) * t);

        AffineTransform old = g2.getTransform();
        g2.setClip(new Rectangle(boxX + 3, boxY + 3, boxW - 6, boxH - 6));
        g2.translate(centerX, centerY);
        g2.rotate(Math.toRadians(ICON_TURN_DEGREES));
        g2.drawImage(img, -drawW / 2, -drawH / 2, drawW, drawH, null);
        g2.setTransform(old);
        g2.setClip(null);
    }

    /**
     * Waves the icon back and forth over 20 degrees (10 either way),
     * with the rotation origin at the bottom-center of the box.
     */
    public static void drawSlashWave(Graphics2D g2, BufferedImage img, int boxX, int boxY, int boxW, int boxH, float time) {
        if (img == null) {
            return;
        }

        int drawW = fitWidth(boxW, boxH, img);
        int drawH = fitHeight(boxW, boxH, img);
        double wave = Math.sin(time * 2 * Math.PI / WAVE_PERIOD_SECONDS) * 10;
        int pivotX = boxX + boxW / 2;
        int pivotY = boxY + boxH;

        AffineTransform old = g2.getTransform();
        g2.translate(pivotX, pivotY);
        g2.rotate(Math.toRadians(wave));
        g2.translate(0, -boxH / 2);
        g2.rotate(Math.toRadians(ICON_TURN_DEGREES));
        g2.drawImage(img, -drawW / 2, -drawH / 2, drawW, drawH, null);
        g2.setTransform(old);
    }

    //rotated 90 degrees, so the bounding box swaps width/height
    private static int fitWidth(int boxW, int boxH, BufferedImage img) {
        int maxIconWidth = Math.min(boxW - 6, MAX_ICON_WIDTH);
        int maxIconHeight = Math.min(boxH - 6, MAX_ICON_HEIGHT);
        float scale = Math.min((float) maxIconWidth / img.getHeight(),
                (float) maxIconHeight / img.getWidth());
        return (int) (img.getWidth() * scale);
    }

    private static int fitHeight(int boxW, int boxH, BufferedImage img) {
        int maxIconWidth = Math.min(boxW - 6, MAX_ICON_WIDTH);
        int maxIconHeight = Math.min(boxH - 6, MAX_ICON_HEIGHT);
        float scale = Math.min((float) maxIconWidth / img.getHeight(),
                (float) maxIconHeight / img.getWidth());
        return (int) (img.getHeight() * scale);
    }
}
