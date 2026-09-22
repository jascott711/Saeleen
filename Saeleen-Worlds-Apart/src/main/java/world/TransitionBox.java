package world;

import java.awt.Rectangle;

/**
 * TransitionBox
 * a zone in the world that triggers a transition,
 * each box leads to its own destination zone and spawn point
 */
public class TransitionBox {

    public Rectangle bounds;
    public String destZone;
    public String destSpawnId;

    public TransitionBox(Rectangle bounds, String destZone, String destSpawnId) {
        this.bounds = bounds;
        this.destZone = destZone;
        this.destSpawnId = destSpawnId;
    }
}