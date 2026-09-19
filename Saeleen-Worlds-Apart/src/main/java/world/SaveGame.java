package world;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import objects.Player;
import objects.items.Apple;
import objects.items.Coin;
import objects.items.Item;
import objects.items.ManaPotion;
import objects.npcs.Npc;
import objects.npcs.aggressive.Enemy;
import objects.npcs.aggressive.Soldier;

/**
 * SaveGame
 * saves and loads the current game state to and from a slot file
 */
public class SaveGame {

    private static final String SAVE_DIR = "saves";
    private static final String[] SLOT_FILES = { "slot1.properties", "slot2.properties", "slot3.properties" };

    public static boolean hasSave(int slot) {
        return getSlotFile(slot).exists();
    }

    public static boolean hasAnySave() {
        return lastSaveSlot() >= 0;
    }

    public static int lastSaveSlot() {
        int latest = -1;
        long latestTime = -1;
        for (int i = 0; i < SLOT_FILES.length; i++) {
            File file = getSlotFile(i);
            if (file.exists()) {
                long modified = file.lastModified();
                if (modified > latestTime) {
                    latestTime = modified;
                    latest = i;
                }
            }
        }
        return latest;
    }

    public static boolean save(int slot) {
        Player player = World.currentPlayer;

        Properties props = new Properties();

        //player
        props.setProperty("posX", String.valueOf(player.getPosX()));
        props.setProperty("posY", String.valueOf(player.getPosY()));
        props.setProperty("health", String.valueOf(player.health));
        props.setProperty("maxHealth", String.valueOf(player.maxHealth));
        props.setProperty("mana", String.valueOf(player.mana));
        props.setProperty("maxMana", String.valueOf(player.maxMana));
        props.setProperty("level", String.valueOf(player.level));
        props.setProperty("experience", String.valueOf(player.experience));
        props.setProperty("xpToLevel", String.valueOf(player.xpToLevel));
        props.setProperty("xpToLastLevel", String.valueOf(player.xpToLastLevel));
        props.setProperty("playerGold", String.valueOf(player.playerGold));
        props.setProperty("direction", String.valueOf(player.direction));
        props.setProperty("abilitySlots", String.valueOf(player.abilitySlots));
        props.setProperty("openstMSlots", String.valueOf(player.openstMSlots));
        props.setProperty("spellChoice", String.valueOf(player.spellChoice));
        props.setProperty("spellChoice2", String.valueOf(player.spellChoice2));

        StringBuilder items = new StringBuilder();
        for (Item item : player.myItems) {
            if (items.length() > 0) items.append(',');
            items.append(item.itemDesc);
        }
        props.setProperty("items", items.toString());

        //enemies
        int enemyIndex = 0;
        for (Npc npc : World.currentWorld.npcSprites) {
            if (npc instanceof Enemy) {
                Enemy enemy = (Enemy) npc;
                String prefix = "enemy" + enemyIndex + ".";
                props.setProperty(prefix + "type", enemy.getClass().getSimpleName());
                props.setProperty(prefix + "posX", String.valueOf(enemy.getPosX()));
                props.setProperty(prefix + "posY", String.valueOf(enemy.getPosY()));
                props.setProperty(prefix + "health", String.valueOf(enemy.getHealth()));
                props.setProperty(prefix + "maxHealth", String.valueOf(enemy.getMaxHealth()));
                props.setProperty(prefix + "level", String.valueOf(enemy.getLevel()));
                props.setProperty(prefix + "dmg", String.valueOf(enemy.getDmg()));
                props.setProperty(prefix + "xpGiven", String.valueOf(enemy.getXpGiven()));
                props.setProperty(prefix + "isPlayerInVision", String.valueOf(enemy.isPlayerInVision));
                props.setProperty(prefix + "isWalkingLeft", String.valueOf(enemy.isWalkingLeft));
                props.setProperty(prefix + "startPosX", String.valueOf(enemy.getStartPosX()));
                props.setProperty(prefix + "startPosY", String.valueOf(enemy.getStartPosY()));
                enemyIndex++;
            }
        }
        props.setProperty("enemyCount", String.valueOf(enemyIndex));

        //items on the ground
        int itemIndex = 0;
        for (Item item : World.currentWorld.itemSprites) {
            String prefix = "item" + itemIndex + ".";
            props.setProperty(prefix + "type", item.itemDesc);
            props.setProperty(prefix + "posX", String.valueOf(item.getPosX()));
            props.setProperty(prefix + "posY", String.valueOf(item.getPosY()));
            itemIndex++;
        }
        props.setProperty("itemCount", String.valueOf(itemIndex));

        //coins on the ground
        int coinIndex = 0;
        for (Coin coin : World.currentWorld.coinSprites) {
            String prefix = "coin" + coinIndex + ".";
            props.setProperty(prefix + "posX", String.valueOf(coin.getPosX()));
            props.setProperty(prefix + "posY", String.valueOf(coin.getPosY()));
            props.setProperty(prefix + "value", String.valueOf(coin.value));
            coinIndex++;
        }
        props.setProperty("coinCount", String.valueOf(coinIndex));

        try {
            File dir = new File(SAVE_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            try (FileOutputStream out = new FileOutputStream(getSlotFile(slot))) {
                props.store(out, "Saeleen Worlds Apart save slot " + (slot + 1));
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean load(int slot) {
        File file = getSlotFile(slot);
        if (!file.exists()) {
            return false;
        }

        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(file)) {
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        Player player = World.currentPlayer;

        player.setPosX(Float.parseFloat(props.getProperty("posX")));
        player.setPosY(Float.parseFloat(props.getProperty("posY")));
        player.health = Integer.parseInt(props.getProperty("health"));
        player.maxHealth = Integer.parseInt(props.getProperty("maxHealth", String.valueOf(player.maxHealth)));
        player.mana = Integer.parseInt(props.getProperty("mana"));
        player.maxMana = Integer.parseInt(props.getProperty("maxMana", String.valueOf(player.maxMana)));
        player.level = Integer.parseInt(props.getProperty("level"));
        player.experience = Integer.parseInt(props.getProperty("experience"));
        player.xpToLevel = Integer.parseInt(props.getProperty("xpToLevel"));
        player.xpToLastLevel = Integer.parseInt(props.getProperty("xpToLastLevel", String.valueOf(player.xpToLastLevel)));
        player.playerGold = Integer.parseInt(props.getProperty("playerGold"));
        player.direction = Integer.parseInt(props.getProperty("direction", "0"));
        player.abilitySlots = Integer.parseInt(props.getProperty("abilitySlots", "0"));
        player.openstMSlots = Integer.parseInt(props.getProperty("openstMSlots", "1"));
        player.spellChoice = Integer.parseInt(props.getProperty("spellChoice", "0"));
        player.spellChoice2 = Integer.parseInt(props.getProperty("spellChoice2", "0"));

        player.myItems.clear();
        String items = props.getProperty("items", "");
        if (!items.isEmpty()) {
            for (String itemDesc : items.split(",")) {
                player.myItems.add(createItem(itemDesc, 0, 0));
            }
        }

        //enemies
        List<Npc> enemiesToRemove = new ArrayList<Npc>();
        for (Npc npc : World.currentWorld.npcSprites) {
            if (npc instanceof Enemy) {
                enemiesToRemove.add(npc);
            }
        }
        World.currentWorld.npcSprites.removeAll(enemiesToRemove);

        int enemyCount = Integer.parseInt(props.getProperty("enemyCount", "0"));
        for (int i = 0; i < enemyCount; i++) {
            String prefix = "enemy" + i + ".";

            Enemy enemy = createEnemy(
                props.getProperty(prefix + "type"),
                Float.parseFloat(props.getProperty(prefix + "posX")),
                Float.parseFloat(props.getProperty(prefix + "posY"))
            );
            enemy.setHealth(Integer.parseInt(props.getProperty(prefix + "health")));
            enemy.setMaxHealth(Integer.parseInt(props.getProperty(prefix + "maxHealth")));
            enemy.setLevel(Integer.parseInt(props.getProperty(prefix + "level")));
            enemy.setDmg(Integer.parseInt(props.getProperty(prefix + "dmg")));
            enemy.setXpGiven(Integer.parseInt(props.getProperty(prefix + "xpGiven")));
            enemy.isPlayerInVision = Boolean.parseBoolean(props.getProperty(prefix + "isPlayerInVision", "false"));
            enemy.isWalkingLeft = Boolean.parseBoolean(props.getProperty(prefix + "isWalkingLeft", "true"));
            enemy.isWalkingRight = !enemy.isWalkingLeft;
            enemy.setStartPos(
                Float.parseFloat(props.getProperty(prefix + "startPosX", props.getProperty(prefix + "posX"))),
                Float.parseFloat(props.getProperty(prefix + "startPosY", props.getProperty(prefix + "posY")))
            );
            World.currentWorld.npcSprites.add(enemy);
        }

        //items on the ground
        World.currentWorld.itemSprites.clear();
        int itemCount = Integer.parseInt(props.getProperty("itemCount", "0"));
        for (int i = 0; i < itemCount; i++) {
            String prefix = "item" + i + ".";
            World.currentWorld.itemSprites.add(
                createItem(
                    props.getProperty(prefix + "type"),
                    Float.parseFloat(props.getProperty(prefix + "posX")),
                    Float.parseFloat(props.getProperty(prefix + "posY"))
                )
            );
        }

        //coins on the ground
        World.currentWorld.coinSprites.clear();
        int coinCount = Integer.parseInt(props.getProperty("coinCount", "0"));
        for (int i = 0; i < coinCount; i++) {
            String prefix = "coin" + i + ".";
            Coin coin = new Coin(
                Float.parseFloat(props.getProperty(prefix + "posX")),
                Float.parseFloat(props.getProperty(prefix + "posY"))
            );
            coin.value = Integer.parseInt(props.getProperty(prefix + "value", String.valueOf(coin.value)));
            World.currentWorld.coinSprites.add(coin);
        }

        return true;
    }

    private static Item createItem(String itemDesc, float posX, float posY) {
        if (itemDesc.equals("Apple")) return new Apple(posX, posY);
        return new ManaPotion(posX, posY);
    }

    private static Enemy createEnemy(String type, float posX, float posY) {
        if (type.equals("Soldier")) return new Soldier(posX, posY);
        return new Enemy(posX, posY);
    }

    private static File getSlotFile(int slot) {
        return new File(SAVE_DIR, SLOT_FILES[slot]);
    }
}