package nl.saxion.ehi1vsb1;

import nl.saxion.ehi1vsb1.data.Target;
import robocode.RadarTurnCompleteCondition;
import robocode.ScannedRobotEvent;

public class EvadeBot extends TeamRobot {
    private Target currentTarget;
    private double energy = 0;

    /**
     * Main run method
     *
     * @author Sieger van Breugel
     */
    @Override
    public void run() {
        super.run();
        setCurrentTarget(currentTarget.getName());
        setScanMode(SCAN_LOCK);

        while (true) {
            super.radarStep();
        }
    }

    /**
     * Customized onScannedRobot event that uses the super.onScannedRobot
     *
     * @param event - The scanned robot
     * @author Sieger van Breugel
     */
    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);

        currentTarget = targets.getTarget(event.getName());

        if (energy == 0) {
            energy = currentTarget.getEnergy();
        }

        if (!currentTarget.isFriendly()) {
            double followGun = getHeading() + currentTarget.getBearing() - getGunHeading();
            turnGunRight(followGun);


            if (currentTarget.getEnergy() < energy) {
                super.evade(currentTarget);
                energy = 0;
            } else {
                fire(gunPowerForDistance(currentTarget));
            }
        }

        if (currentTarget.getEnergy() <= 0) {
            currentTarget = null;
        }

        execute();
    }

    /**
     * Method that returns the required firepower based on dinstance to the target
     *
     * @param target - currentTarget
     * @return double power - Firepower
     * @author Sieger van Breugel
     */
    private double gunPowerForDistance(Target target) {
        return target.getDistance() / 0.5;
    }
}