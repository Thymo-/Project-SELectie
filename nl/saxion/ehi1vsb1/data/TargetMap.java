package nl.saxion.ehi1vsb1.data;

import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Map containing targets
 *
 * @author Thymo van Beers
 * @author Tim Hofman
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
     *
     * @author Tim Hofman
     */
    public Target getClosest(double xPos, double yPos) {
        Target closestTarget = null;

        System.out.println(targetList.size());
        for (int i = 0; i < targetList.size(); i++) {
            if (i == 0) {
                closestTarget = targetList.get(i);

            } else {
                double distanceToClosestTargetXPos = distanceToTarget(xPos, closestTarget.getxPos());
                double distanceToClosestTargetYPos = distanceToTarget(yPos, closestTarget.getyPos());
                double distanceToTargetXPos = distanceToTarget(xPos, targetList.get(i).getxPos());
                double distanceToTargetYPos = distanceToTarget(yPos, targetList.get(i).getyPos());

                if (distanceToTargetXPos < distanceToClosestTargetXPos && distanceToTargetYPos < distanceToClosestTargetYPos) {
                    closestTarget = targetList.get(i);
                    System.out.println(closestTarget.getName());
                } else if (distanceToTargetXPos < distanceToClosestTargetXPos) {
                    if (distanceToTargetXPos + distanceToTargetYPos < distanceToClosestTargetXPos + distanceToClosestTargetYPos) {
                        closestTarget = targetList.get(i);
                    }
                } else if (distanceToTargetYPos < distanceToClosestTargetYPos) {
                    if (distanceToTargetXPos + distanceToTargetYPos < distanceToClosestTargetXPos + distanceToClosestTargetYPos) {
                        closestTarget = targetList.get(i);
                    }
                }
            }
        }

        return closestTarget;
    }

    /**
     * Get the X or Y distance to the Target
     *
     * @param pos X or Y position
     * @param targetPos X or Y position of Target
     *
     * @return The X or Y distance to the Target
     *
     * @author Tim Hofman
     */
    private double distanceToTarget(double pos, double targetPos) {
        if (pos >= targetPos) {
            return (pos - targetPos);
        } else {
            return (targetPos - pos);
        }
    }

    /**
     * Adds a Target to the list with targets
     *
     * @param target The target that needs to be added to the list
     *
     * @author Tim Hofman
     */
    public void addTarget(Target target) {
        Target targetToRemove = null;

        if (exists(target)) {
            for (Target targetToCheck : targetList) {
                if (target.getName().equals(targetToCheck.getName())) {
                    targetToRemove = targetToCheck;
                    break;
                }
            }

            for (int i = 0; i < targetList.size(); i++) {
                if (target.getTurn() > targetList.get(i).getTurn()) {
                    targetList.add(target);
                    if (targetToRemove != null) {
                        targetList.remove(targetToRemove);
                    }
                }
            }
        } else {
            targetList.add(target);
        }

        printTargets();
    }

    /**
     * Prints all targets from the list with targets.
     *
     * @author Tim Hofman
     */
    private void printTargets() {
        if (targetList.size() > 0) {
            System.out.println("\n--------\nTargets");
            for (Target target : targetList) {
                System.out.println("  Target: "   + target.getName()
                                    + "\n    X Position: " + target.getxPos()
                                    + "\n    Y Position: " + target.getyPos()
                                    + "\n    Friendly:   " + target.isFriendly()
                                    + "\n    Turn:       " + target.getTurn()
                                    + "\n");
            }
        }
    }

    /**
     * Check if target is already in map
     *
     * @param target Target to find
     * @return true - target exists; false - target does not exist
     * @author Thymo van Beers
     */
    private boolean exists(Target target) {
        for (Target targetToCheck : targetList) {
            if (target.getName().equals(targetToCheck.getName())) {
                return true;
            }
        }

        return false;
    }
}
