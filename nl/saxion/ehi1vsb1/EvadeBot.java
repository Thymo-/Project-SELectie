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
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);

        while (true) {
            setTurnRadarRight(Double.POSITIVE_INFINITY);
            waitFor(new RadarTurnCompleteCondition(this));
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
            double followRadar = getHeading() + currentTarget.getBearing() - getRadarHeading();
            double followGun = getHeading() + currentTarget.getBearing() - getGunHeading();
            turnRadarRight(followRadar);
            turnGunRight(followGun);

            if (currentTarget.getEnergy() < energy) {
                super.evade(currentTarget);
                energy = 0;
            } else {
                fire(3);
            }
        }

        if (currentTarget.getEnergy() <= 0) {
            currentTarget = null;
        }
    }

    private double gunPowerForDistance(Target target) {
        double power = target.getDistance();
        return power;
    }
}