package world;

/**
 * NeutralZone
 * a safe area of the game world, no combat happens here
 */
public abstract class NeutralZone extends Zone {

    public NeutralZone(String id, String backdropPath) {
        super(id, backdropPath);
    }
}