package nl.saxion.ehi1vsb1;

import robocode.ScannedRobotEvent;

/**
 * AttackBot
 *
 * @author Thymo van Beers
 */
public class AttackBot extends TeamRobot {
    private boolean firstBotDetected;

    @Override
    public void run() {
        super.run();

        while (!firstBotDetected)
            doNothing();

        // Logic loop
        while (true) {
            radarStep();

            execute();
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);

        if (!firstBotDetected)
            firstBotDetected = true;
    }
}
