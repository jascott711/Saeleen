package game;

import java.util.HashMap;

import graphics.*;
import objects.Player;
import objects.items.ManaPotion;

import ui.*;
import world.Cave;
import world.HyklefHill;
import world.SaveGame;
import world.World;
import world.Zone;

/**
 * App
 */
public final class App {

    private static HashMap<String, Zone> zones = new HashMap<String, Zone>();

    public App() {
        Renderer.init();

        Renderer.start();
    }

    public static void startNewGame() {
        resetPlayer();
        loadZone("hyklef-hill", "start");
    }

    //loads a zone: the zone loads its own assets, then the player is spawned
    //at the zone's spawn point referenced by spawnId
    public static void loadZone(String zoneId, String spawnId) {
        initZones();

        Zone zone = zones.get(zoneId);
        World.currentWorld = new world.World();
        zone.load();
        World.currentWorld.transitionBoxes.addAll(zone.transitionBoxes);

        addGameUI();

        int[] spawn = zone.spawnPoints.get(spawnId);
        World.currentPlayer.setPosX(spawn[0]);
        World.currentPlayer.setPosY(spawn[1]);
        World.currentPlayer.refreshPosition();
    }

    //registers every zone type in the game, each zone loads its own assets
    private static void initZones() {
        if (!zones.isEmpty()) {
            return;
        }

        zones.put("hyklef-hill", new HyklefHill());
        zones.put("cave", new Cave());
    }

    private static void addGameUI() {
        //add UI
        UIComponent[] uicomponents = {
            new PlayerStats(World.currentPlayer), new HealthBar(World.currentPlayer), new ManaBar(World.currentPlayer),
            new ExperienceBar(World.currentPlayer), new DisplayAbility(World.currentPlayer),
            new DisplayAbility2(World.currentPlayer),
            new DisplayItems(World.currentPlayer), new StatsScreen(World.currentPlayer)
        };
        for (UIComponent uicomponent : uicomponents) {
            World.currentWorld.uicomponents.add(uicomponent);
        }
    }

    public static void loadGame(int slot) {
        startNewGame();
        SaveGame.load(slot);
    }

    private static void resetPlayer() {
        Player player = World.currentPlayer;
        player.setPosX(1000);
        player.setPosY(1200);
        player.health = player.maxHealth;
        player.mana = player.maxMana;
        player.level = 1;
        player.experience = 0;
        player.xpToLevel = 100;
        player.playerGold = 0;
        player.myItems.clear();
        player.direction = 0;
        player.isHit = false;
        player.isInChat = false;
        player.xpFlashTimer = 0;
    }

	public static void quit() {
        System.exit(0);
    }
    
    
    /**
     * Runs App.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        new App();
    }

}
