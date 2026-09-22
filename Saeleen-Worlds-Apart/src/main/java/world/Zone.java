package world;

import java.util.ArrayList;
import java.util.HashMap;

import graphics.Renderer;

/**
 * Zone
 * base class for every area of the game world
 * the zone instance loads its own assets: its backdrop image,
 * its map size, and its content (npcs, enemies, objects, items)
 */
public abstract class Zone {

    public String id;
    public String backdropPath;
    public int mapWidth;
    public int mapHeight;
    public int transitionThickness;
    public HashMap<String, int[]> spawnPoints = new HashMap<String, int[]>();
    public ArrayList<TransitionBox> transitionBoxes = new ArrayList<TransitionBox>();

    public Zone(String id, String backdropPath) {
        this.id = id;
        this.backdropPath = backdropPath;
    }

    //registers a named spawn point at the given map coordinates
    public void addSpawn(String spawnId, int x, int y) {
        spawnPoints.put(spawnId, new int[] { x, y });
    }

    //loads this zone: its backdrop image, its map size, and its content
    public void load() {
        try {
            World.backdrop = Renderer.loadImage(backdropPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        World.mapWidth = mapWidth;
        World.mapHeight = mapHeight;

        try {
            buildContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //adds this zone's npcs, enemies, objects, and items to the current world
    protected abstract void buildContent();
}