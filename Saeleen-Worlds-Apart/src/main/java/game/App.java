package game;

import java.util.ArrayList;

import graphics.*;
import objects.Player;
import objects.Tree;
import objects.impassable.Iob;
import objects.items.Apple;
import objects.items.Item;
import objects.items.ManaPotion;
import objects.npcs.aggressive.Enemy;
import objects.npcs.passive.Tatem;
import ui.*;
import world.World;

/**
 * App
 */
public final class App {

    public App() {
        Renderer.init();
       
        try {
            World.currentWorld = new world.World();

            //add npcs
            World.currentWorld.npcSprites.add(new Tatem(1250,750));

            //add enemies
            Enemy[] enemies = {
                new Enemy(3000,1500), new Enemy(3030,1600), new Enemy(3020,1700),
                new Enemy(3200,1800), new Enemy(3130,1900), new Enemy(3220,2000)
            };
            for (Enemy sol : enemies) {
                World.currentWorld.npcSprites.add(sol);
            }

            //add impassable objects
            Iob[] iobs = {
                new Tree(3520, 2000), new Tree(3960, 1800), new Tree(1450, 1350)
            };
            for (Iob iob : iobs) {
                World.currentWorld.iobSprites.add(iob);
            }
            
            //add items
            Item[] items = {
                new Apple(3520, 2010), new Apple(3500, 2012), new Apple(3550, 2020), 
                new Apple(3960, 1815), new Apple(3930, 1803), new Apple(3990, 1800), 
            };
            for (Item item : items) {
                World.currentWorld.itemSprites.add(item);
            }

            //add UI
            UIComponent[] uicomponents = {
                new PlayerStats(World.currentPlayer), new HealthBar(World.currentPlayer), new ManaBar(World.currentPlayer),
                new ExperienceBar(World.currentPlayer), new DisplayControls(World.currentPlayer), new DisplayAbility(World.currentPlayer),
                new DisplayItems(World.currentPlayer)
            };
            for (UIComponent uicomponent : uicomponents) {
                World.currentWorld.uicomponents.add(uicomponent);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
