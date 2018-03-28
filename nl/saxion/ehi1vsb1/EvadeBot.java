package nl.saxion.ehi1vsb1;

import robocode.ScannedRobotEvent;

public class EvadeBot extends TeamRobot {
    @Override
    public void run() {
        super.run();

        turnRadarLeft(360);

    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);
    }
}