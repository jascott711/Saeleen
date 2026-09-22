package world;

import java.awt.Rectangle;

import objects.Tree;
import objects.impassable.Iob;
import objects.items.Apple;
import objects.items.Item;
import objects.npcs.aggressive.Soldier;
import objects.npcs.passive.Tatem;

/**
 * HyklefHill
 * an open zone, combat can happen here
 * its map is the full size of its backdrop image
 */
public class HyklefHill extends OpenZone {

    public HyklefHill() {
        super("hyklef-hill", "/images/hykleff-hill.png");
        mapWidth = 3840;
        mapHeight = 2160;
        transitionThickness = 230;

        addSpawn("start", 1000, 1200);
        //the cave's south exit leads back here, right outside the cave portal box
        addSpawn("cave-exit", 2405, 888);

        //a portal on the hill leads into the cave
        transitionBoxes.add(new TransitionBox(new Rectangle(2386, 800, 38, 48), "cave", "start"));
        transitionBoxes.add(new TransitionBox(new Rectangle(0, mapHeight - transitionThickness, mapWidth, transitionThickness), "hyklef-hill", "start"));
        transitionBoxes.add(new TransitionBox(new Rectangle(0, 0, transitionThickness, mapHeight), "hyklef-hill", "start"));
        transitionBoxes.add(new TransitionBox(new Rectangle(mapWidth - transitionThickness, 0, transitionThickness, mapHeight), "hyklef-hill", "start"));
    }

    @Override
    protected void buildContent() {
        //add npcs
        World.currentWorld.npcSprites.add(new Tatem(1250, 750));

        //add enemies
        Soldier[] enemies = {
            new Soldier(3000, 1500), new Soldier(3030, 1600), new Soldier(3020, 1700),
            new Soldier(3200, 1800), new Soldier(3130, 1900), new Soldier(3220, 2000)
        };
        for (Soldier sol : enemies) {
            World.currentWorld.npcSprites.add(sol);
        }

        //add impassable objects
        Iob[] iobs = {
            new Tree(3520, 2000), new Tree(3700, 1800), new Tree(1450, 1350)
        };
        for (Iob iob : iobs) {
            World.currentWorld.iobSprites.add(iob);
        }

        //add items
        Item[] items = {
            new Apple(3520, 2010), new Apple(3500, 2012), new Apple(3550, 2020),
            new Apple(3700, 1815), new Apple(3670, 1803), new Apple(3730, 1800)
        };
        for (Item item : items) {
            World.currentWorld.itemSprites.add(item);
        }
    }
}
