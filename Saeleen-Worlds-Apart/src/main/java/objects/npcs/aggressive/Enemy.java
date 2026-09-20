package objects.npcs.aggressive;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import graphics.Animation;
import graphics.Renderer;
import objects.Sprite;
import objects.abilities.Bullet;
import objects.items.Coin;
import objects.items.Item;
import objects.items.ManaPotion;
import objects.npcs.Npc;
import world.World;

/**
 * Enemy
 */
public class Enemy extends Npc {
    Color hb100 = new Color(0,206,110);
	Color mb100 = new Color(0,163,225);
	Color mb50 = new Color(127,0,110);
	Color mb0 = new Color(190,0,82);
	Color xb100 = new Color(255,244,104);

	public int level;
	public int health;
	public int maxHealth;
	public int dmg;
    public int xpGiven;

    private float startPosX;
    private float startPosY;

    public boolean isWalkingLeft = true;
    public boolean isWalkingRight = false;

    public boolean isPlayerInVision = false;
    public boolean isHit = false;

    private boolean isReturningToStart = false;

    public Rectangle outerVision;

    private long whenPlayerWasHit;
    private long ellapsedPlayerHitTime;

    protected int boundsWidth = 30;
    protected int boundsHeight = 30;

    public Enemy() {
    }

    public Enemy(float posX, float posY) {
        super(posX, posY);

        startPosX = posX;
        startPosY = posY;

        width = 64;
        height = 64;
        dimensions = new Rectangle(0, 0, boundsWidth, boundsHeight);
        vision = new Rectangle((int) posX - width, (int) posY - height, width * 4, height * 4);
        outerVision = new Rectangle((int) posX - width * 6, (int) posY - height * 6, width * 12, height * 12);

        showDimensions = true;
        showVision = true;

        try {
            playerChatImage = Renderer.loadImage("/images/clara-chat.png");

            // #region player default
            // import image
            BufferedImage spriteSheet = ImageIO
                    .read(new File(getClass().getResource("/images/soldier-walking.png").toURI()));
            ;
            BufferedImage spriteSheetTakeDamage = ImageIO
                    .read(new File(getClass().getResource("/images/take-damage-anim.png").toURI()));
            ;

            // set image cell size
            int rows = 1;
            int cols = 8;
            BufferedImage[] spriteSheetImagesUp = new BufferedImage[rows * cols];
            BufferedImage[] spriteSheetImagesDown = new BufferedImage[rows * cols];
            BufferedImage[] spriteSheetImagesLeft = new BufferedImage[rows * cols];
            BufferedImage[] spriteSheetImagesRight = new BufferedImage[rows * cols];
            BufferedImage[] spriteSheetTakeDamageFrames = new BufferedImage[rows * cols];

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

            for (int j = 0; j < cols; j++) {
                spriteSheetTakeDamageFrames[j] = spriteSheetTakeDamage.getSubimage(j * width, 0 * height, width, height);
            }
            for (BufferedImage image : spriteSheetTakeDamageFrames) {
                animTakeDamage.images.add(image);
            }
            animTakeDamage.setFps(8);

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
    	// level = 1;
    	// health = 50;
		// dmg = 10;
        // xpGiven = 50;
        setLevel(1);
        setMaxHealth(50);
        setHealth(getMaxHealth());
        setDmg(10);
        setXpGiven(50);

    }




    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getHealth() {
        return this.health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getMaxHealth() {
        return this.maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getDmg() {
        return this.dmg;
    }

    public void setDmg(int dmg) {
        this.dmg = dmg;
    }

    public int getXpGiven() {
        return this.xpGiven;
    }

    public void setXpGiven(int xpGiven) {
        this.xpGiven = xpGiven;
    }

    public float getStartPosX() {
        return this.startPosX;
    }

    public float getStartPosY() {
        return this.startPosY;
    }

    public void setStartPos(float startPosX, float startPosY) {
        this.startPosX = startPosX;
        this.startPosY = startPosY;
    }



    public void killCheck() {
        if (health <= 0) {
            World.currentWorld.removeSprites.add(this);
            World.currentPlayer.experience += xpGiven;
            World.currentPlayer.xpFlashTimer = 2.0f;
            World.currentPlayer.animXpFlash.setCurrentImage(0);
            World.currentPlayer.animXpFlash.setLastTime(System.nanoTime());

            //item drop
            Random dropRate = new Random();
            int drop = dropRate.nextInt(100);
            if (drop < 50) {
                World.currentWorld.itemSprites.add(new ManaPotion((int)getPosX(),(int)getPosY()));
            }

            //coin drop
            Random coinDropRate = new Random();
            int coinDrop = coinDropRate.nextInt(60);

            List<Coin> coins = new ArrayList<Coin>();
            coins.add(new Coin((int)getPosX()-20,(int)getPosY()-20));

            if (coinDrop >= 40) {
                coins.add(new Coin((int)getPosX()+20,(int)getPosY()+40));
                coins.add(new Coin((int)getPosX()-40,(int)getPosY()+20));
            } else if (coinDrop < 40 && coinDrop > 20) {
                coins.add(new Coin((int)getPosX()-40,(int)getPosY()+20));
            }

            for (Coin coin : coins) {
                World.currentWorld.coinSprites.add(coin);
            }

            //World.currentWorld.itemSprites.add(new Coin((int)getPosX(),(int)getPosY()));

            System.out.println("Drop was "+drop);
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        float moveY = 0;
        float moveX = 0;

        boolean wasPlayerInVision = isPlayerInVision;

        int visionWidth = (int)(vision.width * 1.5);
        int visionHeight = (int)(vision.height * 1.5);
        Rectangle visionArea = new Rectangle(
            (int) getPosX() - visionWidth / 2,
            (int) getPosY() - visionHeight / 2,
            visionWidth, visionHeight
        );
        isPlayerInVision = isPlayerInVision || visionArea.intersects(World.currentPlayer.getDimensions());

        //once the player leaves the outer vision, stop chasing
        outerVision.x = (int) getPosX() - outerVision.width / 2;
        outerVision.y = (int) getPosY() - outerVision.height / 2;
        if (!outerVision.intersects(World.currentPlayer.getDimensions())) {
            isPlayerInVision = false;
        }

        //lost sight of the player, walk back to the starting position
        if (wasPlayerInVision && !isPlayerInVision) {
            isReturningToStart = true;
        }

        if (!isPlayerInVision) {

            if (isReturningToStart) {
                //walk back to starting position
                float deltaX = startPosX - posX;
                float deltaY = startPosY - posY;

                if (Math.abs(deltaX) > 1 || Math.abs(deltaY) > 1) {
                    if (Math.abs(deltaX) > Math.abs(deltaY)) {
                        if (deltaX < 0) {
                            moveX -= runSpeed;
                            currentAnimation = 0;
                        } else {
                            moveX += runSpeed;
                            currentAnimation = 1;
                        }
                    } else {
                        if (deltaY < 0) {
                            moveY -= runSpeed;
                            currentAnimation = 2;
                        } else {
                            moveY += runSpeed;
                            currentAnimation = 3;
                        }
                    }
                    animations[currentAnimation].playAnimation();
                } else {
                    //reached the starting position, begin the walk pattern anew
                    setPosX(startPosX);
                    setPosY(startPosY);
                    isReturningToStart = false;
                    isWalkingLeft = true;
                    isWalkingRight = false;
                    currentAnimation = 0;
                }

            } else {
                //default walking pattern
                if ((posX > startPosX - 800) && isWalkingLeft) {
                    isWalkingRight = false;
                    moveX -= runSpeed;
                    currentAnimation = 0;
                    animations[currentAnimation].playAnimation();
                } else {
                    isWalkingRight = true;
                }

                if ((posX < startPosX + 800) && isWalkingRight) {
                    isWalkingLeft = false;
                    moveX += runSpeed;
                    currentAnimation = 1;
                    animations[currentAnimation].playAnimation();
                } else {
                    isWalkingLeft = true;
                }
            }

        } else {
            //chase the player along the shortest axis
            float chaseX = World.currentPlayer.getPosX() - getPosX();
            float chaseY = World.currentPlayer.getPosY() - getPosY();

            if (Math.abs(chaseX) > Math.abs(chaseY)) {
                if (chaseX < 0) {
                    moveX -= runSpeed;
                    currentAnimation = 0;
                } else {
                    moveX += runSpeed;
                    currentAnimation = 1;
                }
            } else {
                if (chaseY < 0) {
                    moveY -= runSpeed;
                    currentAnimation = 2;
                } else {
                    moveY += runSpeed;
                    currentAnimation = 3;
                }
            }
            animations[currentAnimation].playAnimation();
        }


        float newX = posX + moveX * deltaTime;
        float newY = posY + moveY * deltaTime;

        Rectangle testX = new Rectangle(
            (int) newX - boundsWidth / 2, dimensions.y, boundsWidth, boundsHeight
        );
        boolean blockedX = false;
        for (Rectangle box : World.currentPlayer.getHitboxes()) {
            if (testX.intersects(box)) {
                blockedX = true;
                break;
            }
        }
        if (!blockedX) setPosX(newX);

        Rectangle testY = new Rectangle(
            dimensions.x, (int) newY - boundsHeight / 2, boundsWidth, boundsHeight
        );
        boolean blockedY = false;
        for (Rectangle box : World.currentPlayer.getHitboxes()) {
            if (testY.intersects(box)) {
                blockedY = true;
                break;
            }
        }
        if (!blockedY) setPosY(newY);

        dimensions.setBounds(
            (int) getPosX() - boundsWidth / 2,
            (int) getPosY() - boundsHeight / 2,
            boundsWidth, boundsHeight
        );
        vision.x = (int) getPosX() - width / 2;
        vision.y = (int) getPosY();


        //collision detection
        int i = 0;
        boolean wasHit = false;
        for (Sprite sprite : World.currentWorld.sprites) {
            if (doesCollide(sprite) && sprite instanceof Bullet) {
                wasHit = true;
                health -= ((Bullet) sprite).getDamage();
                i = World.currentWorld.sprites.indexOf(sprite);
            }
        }
        if (wasHit) {
            World.currentWorld.sprites.remove(i);
            isHit = true;
            whenPlayerWasHit = System.nanoTime();
        } else if (isHit) {
            //set a 1 second timer for taking damage
            if ((System.nanoTime() / 1000000000) > (whenPlayerWasHit / 1000000000) + 1) {
                isHit = false;
            }
        }

        killCheck();
    }

    @Override
    public void render (Graphics g) {
        super.render(g);
        BufferedImage playerTakeDamageImage = animTakeDamage.getImage();
        animations[currentAnimation].playAnimation();

        if (animations == null || currentAnimation >= animations.length) {
            return;
        }

        BufferedImage image = animations[currentAnimation].getImage();

        if (image == null) {
            return;
        }

        int realX = (int) posX - (image.getWidth() / 2); //center x
        int realY = (int) posY - (image.getHeight() / 2); //center y

        //check if player is close to the edge of the map for camera toggle
        if (World.currentPlayer.isNearEdgeOfMapXMin) {
            //west
            realX = (int) posX - (image.getWidth() / 2);
        } else if (World.currentPlayer.isNearEdgeOfMapXMax) {
            //east
            realX = (int) posX - (image.getWidth() / 2) - (Renderer.gameWidth * 2);
        } else {
            realX = realX - (int)Renderer.camX + Renderer.gameWidth / 2;
        }
        if (World.currentPlayer.isNearEdgeOfMapYMin) {
            //north
            realY = (int) posY - (image.getHeight() / 2);
        } else if (World.currentPlayer.isNearEdgeOfMapYMax) {
            //south
            realY = (int) posY - (image.getHeight() / 2) - (Renderer.gameHeight * 2);
        } else {
            realY = realY - (int)Renderer.camY + Renderer.gameHeight / 2;
        }

        vision.x = realX - vision.width / 3;
        vision.y = realY - vision.height / 3;

        //draw outer vision bounds
        if (showVision) {
            g.setColor(Color.BLUE);
            g.drawRect(realX + image.getWidth() / 2 - outerVision.width / 2, realY + image.getHeight() / 2 - outerVision.height / 2, outerVision.width, outerVision.height);
        }

        //draw sprite
        g.drawImage(image, realX, realY, image.getWidth(), image.getHeight(), null);

        //draw health bar container
        Rectangle rect = new Rectangle(realX, realY - 10, getMaxHealth() + 8, 15);
        g.setColor(Color.WHITE);
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
        g.setColor(new Color(25,25,25)); //black #191919;
        g.fillRect(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2);

		int hc1;
        hc1 = (int) (((float)health / (float)maxHealth) * 100);

        if(hc1>=60)	g.setColor(hb100);
        else if (hc1<60 && hc1 >=41)	g.setColor(xb100);
    	else if (hc1<=40 && hc1 >=21)	g.setColor(Color.ORANGE);
		else if (hc1<=20)	g.setColor(Color.RED);

        //draw health bar
		g.fillRect(rect.x + 4, rect.y + 3, health, rect.height - 6);

        //draw health text
        g.setColor(Color.WHITE);
        g.setFont( new Font("Tahoma", Font.BOLD, 10));
        g.drawString("" + health, rect.x + 10, rect.y + 11);

        if (isHit) {
            animTakeDamage.playAnimation();
            g.drawImage(playerTakeDamageImage, realX, realY, playerTakeDamageImage.getWidth(), playerTakeDamageImage.getHeight(), null);

            ellapsedPlayerHitTime += (System.nanoTime() / 1000000000) / 1000000;

            if ((System.nanoTime() / 1000000000) > (whenPlayerWasHit / 1000000000) + 1) {
                //ellapsedPlayerHitTime = System.nanoTime();
            }

            //g.drawLine(spritePoint.x, spritePoint.y, playerPoint.x, playerPoint.y);
        } else if (!isHit) {
            ellapsedPlayerHitTime = 0;
        }
    }




    //get and draw chat window
    @Override
    public void speak() {
        //don't talk to bad guys
        return;
    }
}
