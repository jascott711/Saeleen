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
    public int spellChoice2 = 0;
    public String[] abilityCycle1 = new String[]{"Slash", "Knife", "Bolt"}; // ability 1 cycle (3 slots)
    public String[] abilityCycle2 = new String[]{"Dash", "", ""};       // ability 2 cycle (3 slots)
    private float manaDrainAccum = 0;
    private float runTimer = 0;
    private float runCooldown = 0;
    private float runDuration = 2.0f;
    private float abilityCooldown = 0;

    public int level = 1; // starting level
    public int experience = 0; // starting experience
    public int xpToLevel = 100; // experience need to gain next level
    public int xpToLastLevel = 0;

    public int abilitySlots = 0, openstMSlots = 1; // ability slots

    public int playerGold = 0;

    public int itemLimit = 6;
    public ArrayList<Item> myItems; // players current items
    public float itemsFullTimer = 0; // shows the "ITEMS FULL" message while > 0

    public boolean isNearEdgeOfMap = false;
    public boolean isNearEdgeOfMapXMin = false;
    public boolean isNearEdgeOfMapYMin = false;
    public boolean isNearEdgeOfMapXMax = false;
    public boolean isNearEdgeOfMapYMax = false;

    public boolean ableToMoveUp = true, ableToMoveDown = true, ableToMoveLeft = true, ableToMoveRight = true;
    private boolean showAction = false;
    public boolean isInChat = false;
    public boolean isHit = false;

    public float healFlashTimer = 0; // green tint over the sprite while recovering HP
    public float manaFlashTimer = 0; // blue tint over the sprite while recovering MP
    public float xpFlashTimer = 0; // xp gain flash over the sprite while > 0
    private float lastHealth = -1;
    private float lastMana = -1;

    public Point spritePoint, playerPoint;

    private long whenPlayerWasHit;
    private long ellapsedPlayerHitTime;

    private int boundsWidth = 30;
    private int boundsHeight = 30;

    private float knockbackSpeed = 200;
    private float knockbackVelX = 0;
    private float knockbackVelY = 0;

    private String actionString = "Chill";

    public Animation animAttackUp = new Animation(), animAttackDown = new Animation(), animAttackLeft = new Animation(),
            animAttackRight = new Animation();

    public Animation animHealFlash = new Animation();
    public Animation animManaFlash = new Animation();
    public Animation animXpFlash = new Animation();


    public Player(float posX, float posY) {
        super(posX, posY);

        runSpeed = 200;

        width = 64;
        height = 64;
        dimensions = new Rectangle(0, 0, boundsWidth, boundsHeight);
        vision = new Rectangle((int) posX - width, (int) posY - height, width * 2, height * 2);

        myItems = new ArrayList<Item>();

        showDimensions = true;
        showVision = true;

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

            BufferedImage spriteSheetHealFlash = ImageIO
                    .read(new File(getClass().getResource("/images/recover-hp-anim.png").toURI()));
            ;
            BufferedImage[] spriteSheetHealFlashFrames = new BufferedImage[rows * cols];
            for (int j = 0; j < cols; j++) {
                spriteSheetHealFlashFrames[j] = spriteSheetHealFlash.getSubimage(j * width, 0 * height, width, height);
            }
            for (BufferedImage image : spriteSheetHealFlashFrames) {
                animHealFlash.images.add(image);
            }
            animHealFlash.setFps(8);

            BufferedImage spriteSheetManaFlash = ImageIO
                    .read(new File(getClass().getResource("/images/recover-mp-anim.png").toURI()));
            ;
            BufferedImage[] spriteSheetManaFlashFrames = new BufferedImage[rows * cols];
            for (int j = 0; j < cols; j++) {
                spriteSheetManaFlashFrames[j] = spriteSheetManaFlash.getSubimage(j * width, 0 * height, width, height);
            }
            for (BufferedImage image : spriteSheetManaFlashFrames) {
                animManaFlash.images.add(image);
            }
            animManaFlash.setFps(8);

            BufferedImage spriteSheetXpFlash = ImageIO
                    .read(new File(getClass().getResource("/images/gain-xp-anim.png").toURI()));
            ;
            BufferedImage[] spriteSheetXpFlashFrames = new BufferedImage[rows * cols];
            for (int j = 0; j < cols; j++) {
                spriteSheetXpFlashFrames[j] = spriteSheetXpFlash.getSubimage(j * width, 0 * height, width, height);
            }
            for (BufferedImage image : spriteSheetXpFlashFrames) {
                animXpFlash.images.add(image);
            }
            animXpFlash.setFps(8);
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

    public float getRunCooldown() {
        return runCooldown;
    }

    public float getRunTimer() {
        return runTimer;
    }

    public float getRunDuration() {
        return runDuration;
    }

    public float getAbilityCooldown() {
        return abilityCooldown;
    }

    //level required to use an ability (unlocked at level 1 by default)
    public int abilityLevelRequired(String ability) {
        if ("Bolt".equals(ability)) {
            return 3;
        }
        return 1;
    }

    public boolean isAbilityUnlocked(String ability) {
        return level >= abilityLevelRequired(ability);
    }

    //moves to the next valid (non-empty, unlocked) slot in a cycle, wrapping around and skipping locked or empty slots
    public int nextValidIndex(String[] cycle, int current, int step) {
        int size = cycle.length;
        for (int i = 1; i <= size; i++) {
            int test = ((current + step * i) % size + size) % size;
            if (cycle[test] != null && !cycle[test].isEmpty() && isAbilityUnlocked(cycle[test])) {
                return test;
            }
        }
        return current;
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

        float runCooldownTime = 2.0f;

        boolean isRunning = Input.getKey(KeyEvent.VK_SHIFT) && !isIntersectingIob && spellChoice2 == 0 && mana > 0 && runCooldown <= 0;

        if (isRunning) {
            runSpeed = 600;
            runTimer += deltaTime;
            for (Animation animation : animations) {
                animation.setFps(16);
            }
        } else {
            runSpeed = 200;
            runTimer = 0;
            for (Animation animation : animations) {
                animation.setFps(8);
            }
        }
        //running is Ability2 and consumes mana while active
        if (isRunning) {
            //starting a run costs 1 mana immediately
            if (Input.getKeyDown(KeyEvent.VK_SHIFT)) {
                mana -= 1;
                if (mana < 0) {
                    mana = 0;
                }
            }
            manaDrainAccum += 5 * deltaTime;
            if (manaDrainAccum >= 1) {
                int drain = (int) manaDrainAccum;
                mana -= drain;
                manaDrainAccum -= drain;
                if (mana < 0) {
                    mana = 0;
                }
            }
            if (runTimer >= runDuration) {
                runTimer = 0;
                runCooldown = runCooldownTime;
            }
        } else {
            manaDrainAccum = 0;
            if (runCooldown > 0) {
                runCooldown -= deltaTime;
                if (runCooldown < 0) {
                    runCooldown = 0;
                }
            }
        }

        if (abilityCooldown > 0) {
            abilityCooldown -= deltaTime;
            if (abilityCooldown < 0) {
                abilityCooldown = 0;
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
                    } else {
                        itemsFullTimer = 2.0f;
                    }
                    itemIndex = World.currentWorld.itemSprites.indexOf(sprite);
                    break;
                }
            }
            if (itemIndex >= 0 && myItems.size() < itemLimit) {
                World.currentWorld.itemSprites.remove(itemIndex);
            }

        }

        float newX = posX + moveX * deltaTime;
        float newY = posY + moveY * deltaTime;

        //knockback from taking damage (reuses the collision checks below)
        newX += knockbackVelX * deltaTime;
        newY += knockbackVelY * deltaTime;
        knockbackVelX *= 0.8f;
        knockbackVelY *= 0.8f;

        //keep the player inside the map bounds
        if (newX < dimensions.width / 2) {
            newX = dimensions.width / 2;
        } else if (newX > Renderer.gameWidth * 3 - dimensions.width / 2) {
            newX = Renderer.gameWidth * 3 - dimensions.width / 2;
        }
        if (newY < dimensions.height / 2) {
            newY = dimensions.height / 2;
        } else if (newY > Renderer.gameHeight * 3 - dimensions.height / 2) {
            newY = Renderer.gameHeight * 3 - dimensions.height / 2;
        }

        Rectangle testDimensionsX = new Rectangle(
            (int) newX - dimensions.width / 2, dimensions.y, dimensions.width, dimensions.height
        );
        boolean blockedX = false;
        for (Iob sprite : World.currentWorld.iobSprites) {
            for (Rectangle box : sprite.getHitboxes()) {
                if (testDimensionsX.intersects(box)) {
                    blockedX = true;
                    break;
                }
            }
            if (blockedX) break;
        }
        for (Npc sprite : World.currentWorld.npcSprites) {
            if (!sprite.isSolid) continue;
            for (Rectangle box : sprite.getHitboxes()) {
                if (testDimensionsX.intersects(box)) {
                    blockedX = true;
                    break;
                }
            }
            if (blockedX) break;
        }
        if (!blockedX) setPosX(newX);

        Rectangle testDimensionsY = new Rectangle(
            dimensions.x, (int) newY - dimensions.height / 2, dimensions.width, dimensions.height
        );
        boolean blockedY = false;
        for (Iob sprite : World.currentWorld.iobSprites) {
            for (Rectangle box : sprite.getHitboxes()) {
                if (testDimensionsY.intersects(box)) {
                    blockedY = true;
                    break;
                }
            }
            if (blockedY) break;
        }
        for (Npc sprite : World.currentWorld.npcSprites) {
            if (!sprite.isSolid) continue;
            for (Rectangle box : sprite.getHitboxes()) {
                if (testDimensionsY.intersects(box)) {
                    blockedY = true;
                    break;
                }
            }
            if (blockedY) break;
        }
        if (!blockedY) setPosY(newY);

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
                        if (this.health < 0) {
                            this.health = 0;
                        }
                        isHit = true;
                        whenPlayerWasHit = System.nanoTime();

                        float knockX = getPosX() - sprite.getPosX();
                        float knockY = getPosY() - sprite.getPosY();
                        float knockDist = (float) Math.sqrt(knockX * knockX + knockY * knockY);
                        if (knockDist > 0) {
                            knockbackVelX = (knockX / knockDist) * knockbackSpeed;
                            knockbackVelY = (knockY / knockDist) * knockbackSpeed;
                        }
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
            if ((System.nanoTime() / 1000000000) > (whenPlayerWasHit / 1000000000) + 0.5) {
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
        	spellChoice = nextValidIndex(abilityCycle1, spellChoice, -1);
        }

        if (Input.getKeyDown(KeyEvent.VK_P)) {
        	spellChoice = nextValidIndex(abilityCycle1, spellChoice, 1);
        }

        if (Input.getKeyDown(KeyEvent.VK_OPEN_BRACKET)) {
        	spellChoice2 = nextValidIndex(abilityCycle2, spellChoice2, -1);
        }

        if (Input.getKeyDown(KeyEvent.VK_CLOSE_BRACKET)) {
        	spellChoice2 = nextValidIndex(abilityCycle2, spellChoice2, 1);
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
						if (abilityCooldown <= 0 && isAbilityUnlocked(abilityCycle1[0])) {
							Slash slash = new Slash(World.currentPlayer);
							World.currentWorld.addSprite(slash);
							abilityCooldown = 0.4f;
						}
						break;
					}
					case 1: {
                        if (abilityCooldown <= 0) {
                            Knife knife = new Knife(World.currentPlayer.getPosX(), World.currentPlayer.getPosY(), direction);
                            World.currentWorld.addSprite(knife);
                            abilityCooldown = 1.0f;
                        }
						break;
					}
					case 2: {
						if (abilityCooldown <= 0 && isAbilityUnlocked(abilityCycle1[2]) && mana > 0) {
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
                            abilityCooldown = 3.0f;
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
        // Pause menu
        if (Input.getKeyDown(KeyEvent.VK_ESCAPE)) {
            World.mainMenu.openPause();
            World.inMenu = true;
        }

        if (itemsFullTimer > 0) {
            itemsFullTimer -= deltaTime;
            if (itemsFullTimer < 0) {
                itemsFullTimer = 0;
            }
        }

        //recovery flashes: green for HP gained, blue for MP gained
        if (lastHealth >= 0 && health > lastHealth) {
            healFlashTimer = 0.6f;
        }
        if (lastMana >= 0 && mana > lastMana) {
            manaFlashTimer = 0.6f;
        }
        lastHealth = health;
        lastMana = mana;
        if (healFlashTimer > 0) {
            healFlashTimer -= deltaTime;
            if (healFlashTimer < 0) {
                healFlashTimer = 0;
            }
        }
        if (manaFlashTimer > 0) {
            manaFlashTimer -= deltaTime;
            if (manaFlashTimer < 0) {
                manaFlashTimer = 0;
            }
        }
        if (xpFlashTimer > 0) {
            xpFlashTimer -= deltaTime;
            if (xpFlashTimer < 0) {
                xpFlashTimer = 0;
            }
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

            playerEffectText(g, "HIT", Color.RED);

            //g.drawLine(spritePoint.x, spritePoint.y, playerPoint.x, playerPoint.y);
        } else if (!isHit) {
            ellapsedPlayerHitTime = 0;
        }

        if (itemsFullTimer > 0) {
            g.setColor(Color.RED);
            g.setFont(new Font("Tahoma", Font.BOLD, 20));
            String itemsFullString = "ITEMS FULL";
            int itemsFullWidth = g.getFontMetrics().stringWidth(itemsFullString);
            g.drawString(itemsFullString, realX + playerTakeDamageImage.getWidth() / 2 - itemsFullWidth / 2, realY - 8);
        }

        //recovery/xp flash animations (assets: recover-hp-anim / recover-mp-anim / gain-xp-anim)
        if (healFlashTimer > 0 || manaFlashTimer > 0 || xpFlashTimer > 0) {
            if (healFlashTimer > 0) {
                animHealFlash.playAnimation();
                BufferedImage healFlashImage = animHealFlash.getImage();
                if (healFlashImage != null) {
                    g.drawImage(healFlashImage, realX, realY, healFlashImage.getWidth(), healFlashImage.getHeight(), null);
                }
                playerEffectText(g, "HP RECOVERED", ui.UIComponent.hb100);
            }
            if (manaFlashTimer > 0) {
                animManaFlash.playAnimation();
                BufferedImage manaFlashImage = animManaFlash.getImage();
                if (manaFlashImage != null) {
                    g.drawImage(manaFlashImage, realX, realY, manaFlashImage.getWidth(), manaFlashImage.getHeight(), null);
                }
                playerEffectText(g, "MP RECOVERED", ui.UIComponent.mb100);
            }
            if (xpFlashTimer > 0) {
                animXpFlash.playAnimation();
                BufferedImage xpFlashImage = animXpFlash.getImage();
                if (xpFlashImage != null) {
                    g.drawImage(xpFlashImage, realX, realY, xpFlashImage.getWidth(), xpFlashImage.getHeight(), null);
                }
            }
        }
    }

    //draws effect text centered above the sprite with a 1px white outline
    private void playerEffectText(Graphics g, String text, Color color) {
        g.setFont(new Font("Tahoma", Font.BOLD, 20));
        int textWidth = g.getFontMetrics().stringWidth(text);
        int x = realX + animTakeDamage.getImage().getWidth() / 2 - textWidth / 2;
        int y = realY - 8;
        g.setColor(Color.WHITE);
        g.drawString(text, x - 1, y - 1);
        g.drawString(text, x + 1, y - 1);
        g.drawString(text, x - 1, y + 1);
        g.drawString(text, x + 1, y + 1);
        g.drawString(text, x, y - 1);
        g.drawString(text, x, y + 1);
        g.drawString(text, x - 1, y);
        g.drawString(text, x + 1, y);
        g.setColor(color);
        g.drawString(text, x, y);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
