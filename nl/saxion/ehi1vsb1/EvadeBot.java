package nl.saxion.ehi1vsb1;

import robocode.ScannedRobotEvent;

public class EvadeBot extends TeamRobot {
    /**
     * Main run method
     *
     * @author Sieger van Breugel
     */
    @Override
    public void run() {
        super.run();

        turnRadarLeft(Double.POSITIVE_INFINITY);
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);
        turnRadarLeft(currentTarget.getHeading());

        double energy = currentTarget.getEnergy();
        while (currentTarget.getEnergy() > 0) {
            scan();
            if (currentTarget.getEnergy() < energy) {
                super.evade(currentTarget);
            }
            else {
                fire(100);
            }
        }
    }
}