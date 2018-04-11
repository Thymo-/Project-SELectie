package nl.saxion.ehi1vsb1;

import nl.saxion.ehi1vsb1.messages.SideMessage;
import robocode.*;

import java.io.IOException;
import java.util.Scanner;

/**
 * Implementation of the CamperBot
 *
 * @author Tim Hofman
 */
public class CamperBot extends TeamRobot {

    public enum Side {
        LEFT_SIDE, RIGHT_SIDE
    }
    private enum Corner {
        LOWER_LEFT, UPPER_LEFT, LOWER_RIGHT, UPPER_RIGHT
    }

    private Side side;
    private Corner corner;

    @Override
    public void run() {
        double x = getX();
        double y = getY();
        double battleFieldWidth = getBattleFieldWidth();
        double battleFieldHeight = getBattleFieldHeight();

        if (x < battleFieldWidth/2) {
            side = Side.LEFT_SIDE;
            if (y < battleFieldHeight/2) {
                corner = Corner.LOWER_LEFT;
            } else {
                corner = Corner.UPPER_LEFT;
            }
        } else {
            side = Side.RIGHT_SIDE;
            if (y < battleFieldHeight/2) {
                corner = Corner.LOWER_RIGHT;
            } else {
                corner = Corner.UPPER_RIGHT;
            }
        }

        try {
            sendMessage("nl.saxion.ehi1vsb1.CamperBot* (2)", new SideMessage(this.side));
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.run();

        setCurrentTarget(targets.getClosest(x, y).getName());
        setScanMode(SCAN_LOCK);

        while (true) {
            super.radarStep();
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

        if (side == Side.LEFT_SIDE) {
            if (corner == Corner.LOWER_LEFT) {
                moveTo(0,0);
                corner = Corner.UPPER_LEFT;
            } else if (corner == Corner.UPPER_LEFT) {
                moveTo(0, battleFieldHeight);
                corner = Corner.LOWER_LEFT;
            }
        } else if (side == Side.RIGHT_SIDE) {
            if (corner == Corner.LOWER_RIGHT) {
                moveTo(battleFieldWidth, 0);
                corner = Corner.UPPER_RIGHT;
            } else if (corner == Corner.UPPER_RIGHT) {
                moveTo(battleFieldWidth, battleFieldHeight);
                corner = Corner.LOWER_RIGHT;
            }
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
            if (event.getDistance() > 300) {
                fire(0.5);
            } else if (event.getDistance() > 200) {
                fire(1);
            } else if (event.getDistance() > 150) {
                fire(1.5);
            } else if (event.getDistance() > 100) {
                fire(2);
            } else if (event.getDistance() > 50) {
                fire(3);
            }
        }
     }

    @Override
    public void onMessageReceived(MessageEvent event) {
        super.onMessageReceived(event);
        if (event.getMessage() instanceof SideMessage) {
            if (!event.getSender().equals(this.getName())) {
                Side side = ((SideMessage) event.getMessage()).getSide();
                if (side == Side.LEFT_SIDE && this.side == Side.LEFT_SIDE) {
                    this.side = Side.RIGHT_SIDE;
                    this.corner = Corner.LOWER_RIGHT;
                } else if (side == Side.RIGHT_SIDE && this.side == Side.RIGHT_SIDE) {
                    this.side = Side.LEFT_SIDE;
                    this.corner = Corner.LOWER_LEFT;
                }
            }
        }
    }

}