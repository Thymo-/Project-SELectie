package nl.saxion.ehi1vsb1;

import robocode.HitRobotEvent;
import robocode.MoveCompleteCondition;
import robocode.ScannedRobotEvent;

/**
 * Implementation of the CamperBot
 *
 * @author Tim Hofman
 */
public class CamperBot extends TeamRobot {

    private enum Corner {
        LOWER_LEFT, UPPER_LEFT, LOWER_RIGHT, UPPER_RIGHT
    }

    private Corner corner;

    @Override
    public void run() {
        setTurnLeft(getHeading() % 90);
        setTurnGunLeft(90);

        execute();
        waitFor(new MoveCompleteCondition(this));

        double x = getX();
        double y = getY();
        double battleFieldWidth = getBattleFieldWidth();
        double battleFieldHeight = getBattleFieldHeight();

        if (x < battleFieldWidth/2) {
            if (y < battleFieldHeight/2) {
                corner = Corner.LOWER_LEFT;
            } else {
                corner = Corner.UPPER_LEFT;
            }
        } else {
            if (y < battleFieldHeight/2) {
                corner = Corner.LOWER_RIGHT;
            } else {
                corner = Corner.UPPER_RIGHT;
            }
        }

        while (true) {
            scan();
            driveAlongsideWall();
        }
    }

    /**
     * Moves the robot alongside the walls based
     * on the current position.
     *
     * @author Tim Hofman
     */
    private void driveAlongsideWall() {
        double battleFieldWidth = getBattleFieldWidth();
        double battleFieldHeight = getBattleFieldHeight();

        if (corner == Corner.LOWER_LEFT) {
            moveTo(0,0);
            corner = Corner.LOWER_RIGHT;
        } else if (corner == Corner.UPPER_LEFT) {
            moveTo(0, battleFieldHeight);
            corner = Corner.LOWER_LEFT;
        } else if (corner == Corner.LOWER_RIGHT) {
            moveTo(battleFieldWidth, 0);
            corner = Corner.UPPER_RIGHT;
        } else if (corner == Corner.UPPER_RIGHT) {
            moveTo(battleFieldWidth, battleFieldHeight);
            corner = Corner.UPPER_LEFT;
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);

        double angleToEnemy = event.getBearing();

        double angle = Math.toRadians((getHeading() + angleToEnemy % 360));

        double enemyX = (getX() + Math.sin(angle) * event.getDistance());
        double enemyY = (getY() + Math.cos(angle) * event.getDistance());

        if (!targets.getTarget(event.getName()).isFriendly()) {
            if (calcDistance(enemyX, enemyY) < 500) {
                fire(0.5);
            } else if (calcDistance(enemyX, enemyY) < 400) {
                fire(1);
            } else if (calcDistance(enemyX, enemyY) < 300) {
                fire(1.5);
            } else if (calcDistance(enemyX, enemyY) < 200) {
                fire(2);
            } else if (calcDistance(enemyX, enemyY) < 150) {
                fire(2.5);
            } else if (calcDistance(enemyX, enemyY) < 100) {
                fire(3);
            }
        }
     }

}