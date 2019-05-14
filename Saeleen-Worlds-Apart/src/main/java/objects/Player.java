package objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import game.App;
import graphics.Animation;
import graphics.Renderer;
import input.Input;
import objects.abilities.*;
import objects.impassable.Iob;
import objects.npcs.Npc;
import objects.npcs.aggressive.*;
import objects.npcs.passive.*;
import objects.items.*;
import world.World;

/**
 * Player
 */
public class Player extends Npc implements ActionListener {
    public int realX;
    public int realY;
    public int direction = 0;

    public int maxHealth = 100; // starting max health
    public int health = maxHealth; // current health

    public int maxMana = 50; // starting max mana
    public int mana = maxMana; // current mana
    public int spellChoice = 0;

    public int level = 1; // starting level
    public int experience = 0; // starting experience
    public int xpToLevel = 100; // experience need to gain next level
    public int xpToLastLevel = 0;

    public int abilitySlots = 0, openstMSlots = 1; // ability slots

    public int playerGold = 0;

    public int itemLimit = 6;
    public ArrayList<Item> myItems; // players current items

    public boolean isNearEdgeOfMap = false;
    public boolean isNearEdgeOfMapXMin = false;
    public boolean isNearEdgeOfMapYMin = false;
    public boolean isNearEdgeOfMapXMax = false;
    public boolean isNearEdgeOfMapYMax = false;

    public boolean ableToMoveUp = true, ableToMoveDown = true, ableToMoveLeft = true, ableToMoveRight = true;
    private boolean showAction = false;
    public boolean isInChat = false;
    public boolean isHit = false;

    public Point spritePoint, playerPoint;

    private long whenPlayerWasHit;
    private long ellapsedPlayerHitTime;

    private String actionString = "Chill";

    public Animation animAttackUp = new Animation(), animAttackDown = new Animation(), animAttackLeft = new Animation(),
            animAttackRight = new Animation();


    public Player(float posX, float posY) {
        super(posX, posY);

        runSpeed = 200;

        width = 64;
        height = 64;
        dimensions = new Rectangle(0, 0, width, height);
        vision = new Rectangle((int) posX - width, (int) posY - height, width * 2, height * 2);

        myItems = new ArrayList<Item>();

        showDimensions = false;
        showVision = false;

        try {
            playerChatImage = Renderer.loadImage("/images/clara-chat.png");

            // #region player default
            // import image
            BufferedImage spriteSheet = ImageIO
                    .read(new File(getClass().getResource("/images/clara-walking.png").toURI()));
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

            for (int j = 0; j < cols; j++) {
                spriteSheetTakeDamageFrames[j] = spriteSheetTakeDamage.getSubimage(j * width, 0 * height, width, height);
            }
            for (BufferedImage image : spriteSheetTakeDamageFrames) {
                animTakeDamage.images.add(image);
            }
            animTakeDamage.setFps(8);
            // #endregion

            // #region player attack
            // import image
            BufferedImage spriteAttackSheet = ImageIO
                    .read(new File(getClass().getResource("/images/clara-attack.png").toURI()));
            ;

            // set image cell size
            rows = 1;
            cols = 6;
            BufferedImage[] spriteSheetImagesAttackUp = new BufferedImage[rows * cols];
            BufferedImage[] spriteSheetImagesAttackDown = new BufferedImage[rows * cols];
            BufferedImage[] spriteSheetImagesAttackLeft = new BufferedImage[rows * cols];
            BufferedImage[] spriteSheetImagesAttackRight = new BufferedImage[rows * cols];

            for (int j = 0; j < cols; j++) {
                spriteSheetImagesAttackLeft[j] = spriteAttackSheet.getSubimage(j * width, 1 * height, width, height);
            }
            for (BufferedImage image : spriteSheetImagesAttackLeft) {
                animAttackLeft.images.add(image);
            }
            animAttackLeft.setFps(12);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        animations = new Animation[] { animLeft, animRight, animUp, animDown, // 0, 1, 2, 3
                animAttackLeft // 4, 5, 6, 7
        };

        animationsTakeDamage = new Animation[] {
            animTakeDamage
        };

    }

    // #region getters&setters
    public int getMaxHealth() {
        return this.maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getHealth() {
        return this.health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getMaxMana() {
        return this.maxMana;
    }

    public void setMaxMana(int maxMana) {
        this.maxMana = maxMana;
    }

    public int getMana() {
        return this.mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExperience() {
        return this.experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getXpToLevel() {
        return this.xpToLevel;
    }

    public void setXpToLevel(int xpToLevel) {
        this.xpToLevel = xpToLevel;
    }

    public int getXpToLastLevel() {
        return this.xpToLastLevel;
    }

    public void setXpToLastLevel(int xpToLastLevel) {
        this.xpToLastLevel = xpToLastLevel;
    }

    public int getAbilitySlots() {
        return this.abilitySlots;
    }

    public void setAbilitySlots(int abilitySlots) {
        this.abilitySlots = abilitySlots;
    }
    // #endregion

    public void checkLevelUP() {
        if (getExperience() >= getXpToLevel()) {  
            setLevel(getLevel() + 1);
            setMaxHealth((int)(getMaxHealth() * 1.5));
            setHealth(getMaxHealth());
            setMaxMana((int)(getMaxMana() * 1.5));
            setMana(getMaxMana());
            setXpToLevel(getXpToLevel() * 2);
            if (getLevel() == 3 || getLevel() == 5) {
                setAbilitySlots(getAbilitySlots() + 1);
            }
        }
    }

    @Override
    public void update(float deltaTime) {
        float moveY = 0;
        float moveX = 0;

        //impassable object detection & movespeed
        boolean isIntersectingIob = false;
        int stepback = 1;

        if (Input.getKey(KeyEvent.VK_SHIFT) && !isIntersectingIob) {
            runSpeed = 600;
            for (Animation animation : animations) {
                animation.setFps(16);
            }
        } else {
            runSpeed = 200;            
            for (Animation animation : animations) {
                animation.setFps(8);
            }
        }
        //
        //Controls
        if (Input.getKey(KeyEvent.VK_A)) {
            // west
            direction = 0;
            int checkForKeyA = width / 2;           
            if (isIntersectingIob) {
                //setPosX((getWidth() / 2) - stepback);
            } else if (getDimensions().x >= checkForKeyA) {
                moveX -= runSpeed;
                currentAnimation = 0;
                animations[currentAnimation].playAnimation();
            }
        }
        if (Input.getKey(KeyEvent.VK_D)) {
            // east
            direction = 1;
            int checkForKeyD = Renderer.gameWidth * 3 - 300;                  
            if (isIntersectingIob) {
                //setPosX(getPosX()-(getWidth() / 2) + stepback);
            } else if (getDimensions().x <= checkForKeyD) {
                moveX += runSpeed;
                currentAnimation = 1;
                animations[currentAnimation].playAnimation();
            }
            System.out.println((getWidth() / 2) + stepback);
        }
        if (Input.getKey(KeyEvent.VK_W)) {
            // north
            direction = 2;
            int checkForKeyW = 140;
            if (isIntersectingIob) {
                //setPosY(stepback);
            } else if (getDimensions().y >= checkForKeyW) {
                moveY -= runSpeed;
                currentAnimation = 2;
                animations[currentAnimation].playAnimation();
            }
        }
        if (Input.getKey(KeyEvent.VK_S)) {
            // south
            direction = 3;
            int checkForKeyS = Renderer.gameHeight * 3 - 200;
            if (isIntersectingIob) {
                //setPosY(stepback);
            } else if (getDimensions().y <= checkForKeyS) {
                moveY += runSpeed;
                currentAnimation = 3;
                animations[currentAnimation].playAnimation();
            }
        }
        isIntersectingIob = false;
        //
        for (Iob sprite : World.currentWorld.iobSprites) {
            if (getDimensions().intersects(sprite.getDimensions())) {
                isIntersectingIob = true;
                System.out.println("-------------------");
                //System.out.println("object: " + sprite.getDimensions().getLocation().toString());
                //System.out.println("player: " + getDimensions().getLocation().toString());

                //check left & right
                // if (sprite.getDimensions().getLocation().x > getDimensions().getLocation().x) {
                //     setPosX((sprite.getDimensions().getLocation().x - width/2) - stepback);
                // } else if (sprite.getDimensions().getLocation().x + sprite.getDimensions().width > getDimensions().getLocation().x) {
                //     setPosX((sprite.getDimensions().getLocation().x + sprite.getDimensions().width + width/2) + stepback);
                // }

                //check up & down
                // if (sprite.getDimensions().getLocation().y > getDimensions().getLocation().y) {
                //     setPosY((sprite.getDimensions().getLocation().y - height/2) - stepback);
                // } else if (sprite.getDimensions().getLocation().y + sprite.getDimensions().height > getDimensions().getLocation().y) {
                //     setPosY((sprite.getDimensions().getLocation().y + sprite.getDimensions().height + height/2) + stepback);
                // }


                //measure distance between each side of the two intersecting rectangles
                spritePoint = new Point((int)sprite.getPosX(), (int)sprite.getPosY());
                playerPoint = new Point((int)realX, (int)realY);
                double spriteCenterDistance = Math.sqrt(spritePoint.x * playerPoint.x + spritePoint.y * playerPoint.y);
                double spriteLeftEdgeDistance = Math.sqrt((spritePoint.x - (sprite.getDimensions().width / 2)) * playerPoint.x + spritePoint.y * playerPoint.y);
                double spriteTopEdgeDistance = Math.sqrt(spritePoint.x * playerPoint.x + (spritePoint.y - (sprite.getDimensions().height / 2)) * playerPoint.y);
                double spriteRightEdgeDistance = Math.sqrt((spritePoint.x + (sprite.getDimensions().width / 2)) * playerPoint.x + spritePoint.y * playerPoint.y);
                double spriteBottomEdgeDistance = Math.sqrt(spritePoint.x * playerPoint.x + (spritePoint.y + (sprite.getDimensions().height / 2)) * playerPoint.y);


                System.out.println("spriteCenterDistance: " + spriteCenterDistance);
                System.out.println("spriteLeftEdgeDistance: " + spriteLeftEdgeDistance);
                System.out.println("spriteTopEdgeDistance: " + spriteTopEdgeDistance );
                System.out.println("spriteRightEdgeDistance: " + spriteRightEdgeDistance);
                System.out.println("spriteBottomEdgeDistance: " + spriteBottomEdgeDistance );

                // float wy = (getDimensions().width() + sprite.getDimensions().width()) * (getDimensions().centerY() - sprite.getDimensions().centerY());
                // float hx = (getDimensions().height() + sprite.getDimensions().height()) * (getDimensions().centerX() - sprite.getDimensions().centerX());

                // if (wy > hx) {
                //     if (wy > -hx) {
                //          /* top */
                //     }            
                //     else {
                //         /* left */
                //     }
                // }       
                // else {
                //     if (wy > -hx) {}
                //         /* right */
                //     else {
                //        /* bottom */
                //     }
                // }
            }
        }


        

        if (Input.getKeyDown(KeyEvent.VK_F)) {
            //npc interaction
            for (Npc sprite : World.currentWorld.npcSprites) {
                if (sprite == this) {
                    continue;
                }
                if (doesCollide(sprite)) {
                }
                if (inVisionOf(sprite)) {    
                    sprite.speak();
                    isInChat = true;
                } else {
                    sprite.clearSpeak();
                    isInChat = false;
                }
                //System.out.println(World.currentWorld.uicomponents.indexOf(sprite.newCW));
                //System.out.println("Chat step: " + sprite.chatSequenceStep);
            }
            //item pick up
            int itemIndex = -1;
            for (Item sprite : World.currentWorld.itemSprites) {
                if (doesCollide(sprite)) {
                    if (myItems.size() < itemLimit) {                    
                        sprite.pickItemUp(this);
                    }
                    itemIndex = World.currentWorld.itemSprites.indexOf(sprite);
                    break;
                }
            }
            if (itemIndex >= 0 && myItems.size() < itemLimit) {
                World.currentWorld.itemSprites.remove(itemIndex);
            }
           
        }
        

        setPosX(posX + moveX * deltaTime);
        setPosY(posY + moveY * deltaTime);
        dimensions.x = (int) getPosX() - dimensions.width / 2;
        dimensions.y = (int) getPosY() - dimensions.height / 2;
        vision.x = (int) getPosX() - width / 2;
        vision.y = (int) getPosY();

        // check if player is close to the edge of the map for camera toggle
        if (getPosX() < Renderer.gameWidth / 2) {
            // west
            isNearEdgeOfMapXMin = true;
        } else {
            isNearEdgeOfMapXMin = false;
        }
        if (getPosX() > Renderer.gameWidth * 3 - (Renderer.gameWidth / 2)) {
            // east
            isNearEdgeOfMapXMax = true;
        } else {
            isNearEdgeOfMapXMax = false;
        }
        if (getPosY() < Renderer.gameHeight / 2) {
            // north
            isNearEdgeOfMapYMin = true;
        } else {
            isNearEdgeOfMapYMin = false;
        }
        if (getPosY() > Renderer.gameHeight * 3 - (Renderer.gameHeight / 2)) {
            // south
            isNearEdgeOfMapYMax = true;
        } else {
            isNearEdgeOfMapYMax = false;
        }
        if (isNearEdgeOfMapXMin || isNearEdgeOfMapXMax || isNearEdgeOfMapYMin || isNearEdgeOfMapYMax) {
            // if near any direction
            isNearEdgeOfMap = true;
        } else {
            isNearEdgeOfMap = false;
        }

        // Bullet Collision Detection
        for (Sprite sprite : World.currentWorld.sprites) {
            if (sprite == this) {
                continue;
            }
            if (doesCollide(sprite)) {
            }
        }
        //
        // NPC Collision Detection
        if (!isHit) {
            for (Npc sprite : World.currentWorld.npcSprites) {
                if (sprite == this) {
                    continue;
                }
                if (doesCollide(sprite)) {
                    // System.out.println("Collision Detected");
                    // System.out.println(sprite.getClass().toString());
                    if (sprite instanceof Enemy) {
                        this.health -= ((Enemy)sprite).dmg;    
                        isHit = true;      
                        whenPlayerWasHit = System.nanoTime();       
                    } 
                }
                if (inVisionOf(sprite)) {    
                    showAction = true;
    
                    if (sprite instanceof Tatem) {
                        actionString = "Speak";
                    }
    
                    if (showAction) {
                        //continue;
                    }
    
                } else {
                    isInChat = false;
                    sprite.clearSpeak();
                }
            }
        } else if (isHit) {
            //set a 1 second timer for taking damage
            if ((System.nanoTime() / 1000000000) > (whenPlayerWasHit / 1000000000) + 1) {
                isHit = false;
            }
        }
        //coin pick up
        int coinIndex = -1;
        for (Coin sprite : World.currentWorld.coinSprites) {
            if (doesCollide(sprite)) {
                sprite.pickItemUp(this);
                coinIndex = World.currentWorld.coinSprites.indexOf(sprite);
                break;
            }
        }
        if (coinIndex >= 0) {
            World.currentWorld.coinSprites.remove(coinIndex);
            coinIndex = -1;
        }


        // ------------ testing
        if (Input.getKey(KeyEvent.VK_L)) {
            //currentAnimation = 4;

            // float timer = System.nanoTime();
            // float playTime = 1000000000 + System.nanoTime();

            // if (timer < playTime) {
            // animations[currentAnimation].playAnimation();
            // }

            //animations[currentAnimation].playAnimationOnce();

        }
        
        if (Input.getKeyDown(KeyEvent.VK_O)) {
        	spellChoice--;
        	if(spellChoice < 0)	{
                spellChoice = abilitySlots;
            }
        }

        if (Input.getKeyDown(KeyEvent.VK_P)) {
        	spellChoice++;
        	if(spellChoice > abilitySlots)	{
                spellChoice = 0;
            }
        }

        // if (Input.getKeyDown(KeyEvent.VK_K)) {     
        //     if (myItems.size() > 0) {
        //         if (myItems.get(0).isUseable) {   
        //             myItems.get(0).useItem(this);   
        //             myItems.remove(0);             
        //         }
        //     }
        //     System.out.println(myItems.size());
        // }

        if (Input.getKeyDown(KeyEvent.VK_L)) {
            //attack, fire bolt
            if (!isInChat) {
				switch (spellChoice) {
					case 0: {
                        Knife knife = new Knife(World.currentPlayer.getPosX(), World.currentPlayer.getPosY(), direction);
                        World.currentWorld.addSprite(knife);
						break;
					}
					case 1: {
						if (level >= 3 && mana > 0) {
                            System.out.println("Before bolt mana = "+World.currentPlayer.getMana());

							Bolt bolt = new Bolt(World.currentPlayer.getPosX(), World.currentPlayer.getPosY(), direction);
                            World.currentWorld.addSprite(bolt);
                            
                            int currentMana = World.currentPlayer.getMana();
                            World.currentPlayer.setMana(currentMana - 2);
                            
							if (World.currentPlayer.getMana() < 0) {
                                World.currentPlayer.setMana(0);
                            }

                            System.out.println("After bolt mana = "+World.currentPlayer.getMana());
                            System.out.println("currentMana = "+ currentMana );
                            break;
						}
					}
				}
			}	

        }

        
        if (Input.getKeyDown(KeyEvent.VK_1)) {
            if (myItems.size() >= 1) {
                if (myItems.get(0).isConsumable) {   
                    myItems.get(0).useItem(this);   
                    myItems.remove(0);
                } else { 
                    myItems.get(0).useItem(this);
                }
            }
        }
        if (Input.getKeyDown(KeyEvent.VK_2)) {
            if (myItems.size() >= 2) {
                if (myItems.get(1).isConsumable) {   
                    myItems.get(1).useItem(this);   
                    myItems.remove(1);
                } else {                
                    myItems.get(1).useItem(this);  
                }
            }
        }
        if (Input.getKeyDown(KeyEvent.VK_3)) {
            if (myItems.size() >= 3) {
                if (myItems.get(2).isConsumable) {   
                    myItems.get(2).useItem(this);   
                    myItems.remove(2);
                } else {                
                    myItems.get(2).useItem(this);  
                }
            }
        }
        if (Input.getKeyDown(KeyEvent.VK_4)) {
            if (myItems.size() >= 4) {
                if (myItems.get(3).isConsumable) {   
                    myItems.get(3).useItem(this);   
                    myItems.remove(3);
                } else {                
                    myItems.get(3).useItem(this);  
                }
            }
        }
        if (Input.getKeyDown(KeyEvent.VK_5)) {
            if (myItems.size() >= 5) {
                if (myItems.get(4).isConsumable) {   
                    myItems.get(4).useItem(this);   
                    myItems.remove(4);
                } else {                
                    myItems.get(4).useItem(this);  
                }
            }
        }
        if (Input.getKeyDown(KeyEvent.VK_6)) {
            if (myItems.size() >= 6) {
                if (myItems.get(5).isConsumable) {   
                    myItems.get(5).useItem(this);   
                    myItems.remove(5);             
                } else {                
                    myItems.get(5).useItem(this);  
                }
            }
        }

        //set Camera position based on players movement
        Renderer.camX = getPosX();
        Renderer.camY = getPosY();

        checkLevelUP();

        // Testing
        if (Input.getKeyDown(KeyEvent.VK_V)) {
            if (showDimensions) {
                showDimensions = false;
            } else if (!showDimensions) {
                showDimensions = true;
            }
            if (showVision) {
                showVision = false;
            } else if (!showVision) {
                showVision = true;
            }
        }
        // Exit game
        if (Input.getKeyDown(KeyEvent.VK_ESCAPE)) {
            App.quit();
        }
    }

    @Override
    public void render(Graphics g) {
        super.render(g);
            
        BufferedImage playerTakeDamageImage = animTakeDamage.getImage();

        if (playerTakeDamageImage == null) {
            return;
        }

        realX = (int) posX - (playerTakeDamageImage.getWidth() / 2); //center x
        realY = (int) posY - (playerTakeDamageImage.getHeight() / 2); //center y
        
        //check if player is close to the edge of the map for camera toggle
        if (World.currentPlayer.isNearEdgeOfMapXMin) {
            realX = (int) posX - (playerTakeDamageImage.getWidth() / 2);
        } else if (World.currentPlayer.isNearEdgeOfMapXMax) {
            realX = (int) posX - (playerTakeDamageImage.getWidth() / 2) - (Renderer.gameWidth * 2);
        } else {
            realX = realX - (int)Renderer.camX + Renderer.gameWidth / 2;
        }
        if (World.currentPlayer.isNearEdgeOfMapYMin) {
            realY = (int) posY - (playerTakeDamageImage.getHeight() / 2); 
        } else if (World.currentPlayer.isNearEdgeOfMapYMax) {
            realY = (int) posY - (playerTakeDamageImage.getHeight() / 2) - (Renderer.gameHeight * 2);
        } else {
            realY = realY - (int)Renderer.camY + Renderer.gameHeight / 2;
        }

        if (isHit) {
            animTakeDamage.playAnimation();
            g.drawImage(playerTakeDamageImage, realX, realY, playerTakeDamageImage.getWidth(), playerTakeDamageImage.getHeight(), null);

            ellapsedPlayerHitTime += (System.nanoTime() / 1000000000) / 1000000;

            if ((System.nanoTime() / 1000000000) > (whenPlayerWasHit / 1000000000) + 1) {
                //ellapsedPlayerHitTime = System.nanoTime();
            }

            int fontSize = (int)(ellapsedPlayerHitTime / 8);
            int widthGrowth = (int)(ellapsedPlayerHitTime / 16);
            int heightGrowth = (int)(ellapsedPlayerHitTime / 8);
            
            g.setColor(Color.RED);
            g.setFont( new Font("Tahoma", Font.BOLD, 20 + fontSize));
            g.drawString("HIT", realX + 8 - widthGrowth, realY - heightGrowth);

            g.drawLine(spritePoint.x, spritePoint.y, playerPoint.x, playerPoint.y);
        } else if (!isHit) {
            ellapsedPlayerHitTime = 0;            
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
         
    }
}