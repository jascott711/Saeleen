package objects.abilities;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import graphics.Animation;
import graphics.Renderer;
import objects.Mob;
import objects.Player;
import objects.npcs.Npc;
import objects.npcs.aggressive.Enemy;
import world.World;

/**
 * Slash
 * A melee swing: a 120-degree arc that sweeps a blade across the player,
 * pivoting at the dead center of the player and opening toward the direction
 * they face.
 */
public class Slash extends Mob {

    public int damage = 15;

    private float elapsed = 0;
    private float swingDuration = 0.35f;
    private int radius = 75;
    private static final int SWING_DEGREES = 120;
    private static final int HALF_SWING = SWING_DEGREES / 2;
    private static final float SWING_PHASE = 45; //tilts the start of the swing
    private float pivotX;
    private float pivotY;
    private int faceDirection;
    private List<Enemy> alreadyHit = new ArrayList<>();

    public Slash(Player player) {
        super(player.getPosX(), player.getPosY());
        faceDirection = player.direction;
        pivotX = player.getPosX();
        pivotY = player.getPosY();
        width = radius * 2;
        height = radius * 2;

        //reuse the knife frames so the slash has an icon in the ability menus
        Animation anim = new Animation();
        anim.setFps(4);
        try {
            BufferedImage spriteSheet = ImageIO.read(new File(getClass().getResource("/images/knife.png").toURI()));
            final int rows = 1;
            final int cols = 4;
            int frameWidth = spriteSheet.getWidth() / cols;
            int frameHeight = spriteSheet.getHeight() / rows;
            for (int j = 0; j < cols; j++) {
                anim.images.add(spriteSheet.getSubimage(j * frameWidth, 0, frameWidth, frameHeight));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        animations = new Animation[] { anim };
    }

    //current center angle of the swing, 0 degrees = east, positive spins toward the sky
    private float centerAngle() {
        float t = Math.min(elapsed / swingDuration, 1f);
        float r = 1 - t; //swing reversed: left to right instead of right to left
        switch (faceDirection) {
            case 0: return 120 + SWING_DEGREES * r + SWING_PHASE; //left, mirror of the facing-right swing
            case 2: return 30 + SWING_DEGREES * r + SWING_PHASE; //up, sweeping across the top
            case 3: return -30 - SWING_DEGREES * r + SWING_PHASE; //down, sweeping across the bottom
            default: return 60 - SWING_DEGREES * r + SWING_PHASE; //right
        }
    }

    @Override
    public void update(float deltaTime) {
        elapsed += deltaTime;

        //the swing stays centered on the player as they move
        pivotX = World.currentPlayer.getPosX();
        pivotY = World.currentPlayer.getPosY();

        //sweep the arc, damaging each enemy inside it once per swing
        for (Npc npc : World.currentWorld.npcSprites) {
            if (!(npc instanceof Enemy) || alreadyHit.contains(npc)) {
                continue;
            }
            Enemy enemy = (Enemy) npc;
            if (enemy.getHealth() <= 0 || !insideSwing(enemy.getPosX(), enemy.getPosY())) {
                continue;
            }
            alreadyHit.add(enemy);
            enemy.damage(damage);
            enemy.knockback(enemy.currentAnimation, 2);
        }

        if (elapsed >= swingDuration) {
            World.currentWorld.removeSprite(this);
        }
    }

    private boolean insideSwing(float x, float y) {
        float dx = x - pivotX;
        float dy = y - pivotY;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        if (dist > radius + 15) {
            return false;
        }
        float enemyAngle = (float) -Math.toDegrees(Math.atan2(dy, dx)); //same space as centerAngle
        float diff = Math.abs(enemyAngle - centerAngle());
        if (diff > 180) {
            diff = 360 - diff;
        }
        return diff <= HALF_SWING;
    }

    @Override
    public void render(Graphics g) {
        if (animations == null || currentAnimation >= animations.length) {
            return;
        }
        animations[currentAnimation].playAnimation();
        BufferedImage image = animations[currentAnimation].getImage();
        if (image == null) {
            return;
        }

        float edgeAngle = centerAngle() - HALF_SWING;
        double radians = Math.toRadians(edgeAngle);
        int centerX = screenX();
        int centerY = screenY();

        //the knife orbits the pivot as it swings
        float dist = 30f;
        int bladeX = (int) (centerX + Math.cos(radians) * dist);
        int bladeY = (int) (centerY - Math.sin(radians) * dist);

        Graphics2D g2 = (Graphics2D) g;
        AffineTransform oldTransform = g2.getTransform();
        g2.translate(bladeX, bladeY);
        g2.rotate(Math.PI - radians);
        g2.drawImage(image, -image.getWidth() / 2, -image.getHeight() / 2, null);
        g2.setTransform(oldTransform);
    }

    private int screenX() {
        int realX = (int) pivotX;
        if (World.currentPlayer.isNearEdgeOfMapXMin) {
            return realX; //west
        }
        if (World.currentPlayer.isNearEdgeOfMapXMax) {
            return realX - (World.mapWidth - Renderer.gameWidth); //east
        }
        return realX - (int) Renderer.camX + Renderer.gameWidth / 2;
    }

    private int screenY() {
        int realY = (int) pivotY;
        if (World.currentPlayer.isNearEdgeOfMapYMin) {
            return realY; //north
        }
        if (World.currentPlayer.isNearEdgeOfMapYMax) {
            return realY - (World.mapHeight - Renderer.gameHeight); //south
        }
        return realY - (int) Renderer.camY + Renderer.gameHeight / 2;
    }
}