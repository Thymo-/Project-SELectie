package nl.saxion.ehi1vsb1;

import nl.saxion.ehi1vsb1.data.Target;
import nl.saxion.ehi1vsb1.data.TargetMap;
import robocode.ScannedRobotEvent;
import robocode.TurnCompleteCondition;

public class CamperBot extends TeamRobot {

    private enum Corner {
        LOWER_LEFT, UPPER_LEFT, LOWER_RIGHT, UPPER_RIGHT
    }

    private Corner corner;

    @Override
    public void run() {
        super.run();

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
            setTurnRadarRight(Double.POSITIVE_INFINITY);
            execute();
            driveAlongsideWall();
        }
    }

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
        shoot();
    }

    private void shoot() {
        Target target = targets.getClosest(getX(), getY());
    }

}