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

    /**
     * Customized onScannedRobot event that uses the super.onScannedRobot
     *
     * @param event - The scanned robot
     *
     * @author Sieger van Breugel
     */
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
                turnGunLeft(currentTarget.getHeading());
                fire(100);
            }
        }
    }
}