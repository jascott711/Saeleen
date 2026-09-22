package world;

import java.awt.Rectangle;

import graphics.Renderer;

/**
 * Cave
 * a neutral zone, no combat happens here
 * its map is one full viewport, the backdrop image stretches to fill it
 */
public class Cave extends NeutralZone {

    public Cave() {
        super("cave", "/images/cave.png");
        mapWidth = Renderer.gameWidth;
        mapHeight = Renderer.gameHeight;
        transitionThickness = 60;

//the cave appears just above the south exit box
        addSpawn("start", mapWidth / 2, mapHeight - transitionThickness - 100);

        //the cave's only exit is its south box, it leads back to the hill
        transitionBoxes.add(new TransitionBox(new Rectangle(0, mapHeight - transitionThickness, mapWidth, transitionThickness), "hyklef-hill", "start"));
    }

    @Override
    protected void buildContent() {
    }
}