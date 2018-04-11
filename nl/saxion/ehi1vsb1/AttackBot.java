package nl.saxion.ehi1vsb1;

import nl.saxion.ehi1vsb1.data.Target;
import robocode.ScannedRobotEvent;

/**
 * AttackBot
 *
 * @author Thymo van Beers
 */
public class AttackBot extends TeamRobot {
    @Override
    public void run() {
        super.run();
        setCurrentTarget(targets.getClosest(getX(), getY()).getName());
        setScanMode(SCAN_LOCK);

        // Logic loop
        while (true) {
            radarStep();
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);
        Target target = targets.getTarget(currentTarget);
        setTurnGunRight(getHeading() + target.getBearing() - getGunHeading());
        fire(target.getDistance() / 0.5);
        moveTo(target.getxPos(), target.getyPos());
    }
}
