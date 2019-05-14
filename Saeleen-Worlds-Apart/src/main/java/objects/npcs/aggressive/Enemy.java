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

    public Enemy() {
    }

    public Enemy(float posX, float posY) {
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



    public void killCheck() {
        if (health <= 0) {
            World.currentWorld.removeSprites.add(this);
            World.currentPlayer.experience += xpGiven;

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

        if (!isPlayerInVision) {
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
            
        } else if (isPlayerInVision) {
            //determine shortest path to player and proceed towards it
        }
        

        setPosX(posX + moveX * deltaTime);
        setPosY(posY + moveY * deltaTime);
        dimensions.x = (int) getPosX() - dimensions.width / 2;
        dimensions.y = (int) getPosY() - dimensions.height / 2;
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
        }
        
        killCheck();
    }

    @Override
    public void render (Graphics g) {
        super.render(g);
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

        dimensions.x = realX;
        dimensions.y = realY;
        vision.x = realX - vision.width / 3;
        vision.y = realY - vision.height / 3;

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
        
    }




    //get and draw chat window
    @Override
    public void speak() {
        //don't talk to bad guys
        return;
    }
}