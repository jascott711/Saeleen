package world;

/**
 * OpenZone
 * an open area of the game world, combat can happen here
 */
public abstract class OpenZone extends Zone {

    public OpenZone(String id, String backdropPath) {
        super(id, backdropPath);
    }
}