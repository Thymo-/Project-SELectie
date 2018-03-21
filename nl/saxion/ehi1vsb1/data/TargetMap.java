package nl.saxion.ehi1vsb1.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Map containing targets
 *
 * @author Thymo van Beers
 */
public class TargetMap {
    private List<Target> targetList;

    public TargetMap() {
        targetList = new ArrayList<>();
    }

    /**
     * Find the target closest to the given position
     *
     * @param xPos X position
     * @param yPos Y position
     *
     * @return The closest target
     */
    public Target getClosest(double xPos, double yPos) {
        //TODO: Find closest target based on given position
        return null;
    }

    public void addTarget(Target target) {
        targetList.add(target);
    }
}
