package nl.saxion.ehi1vsb1;

import nl.saxion.ehi1vsb1.data.Target;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

/**
 * AttackBot
 *
 * @author Thymo van Beers
 */
public class AttackBot extends TeamRobot {
    // Timer to track how long it's been since the last radar mode switch
    private long lastModeSwitch;

    public AttackBot() {
        super();
        this.lastModeSwitch = 0;
    }

    /**
     * AttackBot behaviour
     *
     * @author Thymo van Beers
     */
    @Override
    public void run() {
        super.run();

        while (true) {
            radarStep();
            determineRadarMode();

            setCurrentTarget(targets.getClosest(getX(), getY()).getName());
            setScanMode(SCAN_LOCK);
            Target target = targets.getTarget(currentTarget);

            if (target == null)
                return;

            // Nicely borrowed from the radar :-)
            double gunTurn =
                    getHeading() + target.getBearing()
                    - getGunHeading();
            double gunCmd = 1.0 * Utils.normalRelativeAngleDegrees(gunTurn);
            setTurnGunRight(gunCmd);

            moveTo(target.getxPos(), target.getyPos());
            execute();
        }
    }

    /**
     * Determine the next radar mode based on mode and last switch time
     *
     * @author Thymo van Beers
     */
    private void determineRadarMode() {
        long time = getTime();
        if ((getScanMode() == SCAN_LOCK && time - lastModeSwitch > 100) ||
                (getScanMode() == SCAN_SEARCH && time - lastModeSwitch > 15)) {
            lastModeSwitch = time;
            if (getScanMode() == SCAN_SEARCH) {
                if (targets.getClosest(getX(), getY()) != null) {
                    setCurrentTarget(targets.getClosest(getX(), getY()).getName());
                    setScanMode(SCAN_LOCK);
                }
            } else {
                setScanMode(SCAN_SEARCH);
            }
        }
    }

    /**
     * Event handler for radar, mostly handled by superclass
     * Determines distance to target and fire's with a power of 2
     * if range <100, otherwise fire with a power of 1
     *
     * @param event - The scanned robot
     *
     * @author Thymo van Beers
     */
    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);

        if (event.getDistance() < 100) {
            fire(2);
        } else {
            fire(1);
        }
    }
}
