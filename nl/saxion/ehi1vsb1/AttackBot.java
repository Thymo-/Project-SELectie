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
    private long lastModeSwitch;

    public AttackBot() {
        super();
        this.lastModeSwitch = 0;
    }

    @Override
    public void run() {
        super.run();
        setCurrentTarget(targets.getClosest(getX(), getY()).getName());
        setScanMode(SCAN_LOCK);

        while (true) {
            radarStep();
            determineRadarMode();

            Target target = targets.getTarget(currentTarget);

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

    private void determineRadarMode() {
        long time = getTime();
        if ((getScanMode() == SCAN_LOCK && time - lastModeSwitch > 100) ||
                (getScanMode() == SCAN_SEARCH && time - lastModeSwitch > 15)) {
            lastModeSwitch = time;
            if (getScanMode() == SCAN_SEARCH) {
                setCurrentTarget(targets.getClosest(getX(), getY()).getName());
                setScanMode(SCAN_LOCK);
            } else {
                setScanMode(SCAN_SEARCH);
            }
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);
        fire(2);
    }
}
