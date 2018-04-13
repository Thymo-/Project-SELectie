package nl.saxion.ehi1vsb1;

import nl.saxion.ehi1vsb1.messages.SideMessage;
import robocode.*;
import robocode.util.Utils;

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

    private boolean moveRobot = true;
    private int lastTurn = 0;

    /**
     * Main run method
     *
     * @author Tim Hofman
     */
    @Override
    public void run() {
        double x = getX();
        double y = getY();
        double battleFieldWidth = getBattleFieldWidth();
        double battleFieldHeight = getBattleFieldHeight();

        //Side and corner where the robot is located
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

        //Sends a message to the second CamperBot in the team
        //See the onMessageReceived method
        try {
            sendMessage("nl.saxion.ehi1vsb1.CamperBot* (2)", new SideMessage(this.side));
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.run();

        if (targets.getClosest(x, y) != null) {
            setCurrentTarget(targets.getClosest(x, y).getName());
        }

        while (true) {
            if (moveRobot) {
                setScanMode(SCAN_SEARCH);
                radarStep();
                //Makes the robot move alongside the wall
                driveAlongsideWall();
                lastTurn = (int)getTime();
            } else {
                if (targets.getClosest(x, y) != null) {
                    setCurrentTarget(targets.getClosest(x, y).getName());
                    setScanMode(SCAN_LOCK);
                }
            }
            //If moveRobot is true, then the robot is driving alongside the wall
            //If moveRobot is false, then the robot is in a corner scanning for targets
            //CamperBot will move when the current round number minus the round number
            // when it was last driving alongside the wall is greater than 100
            moveRobot = ((int)getTime() - lastTurn >= 100);
            radarStep();
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

    /**
     * Customized onScannedRobot event that uses the super.onScannedRobot
     *
     * @param event - The scanned robot
     * @author Tim Hofman
     */
    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);

        double followGun = getHeading() + event.getBearing() - getGunHeading();
        setTurnGunRight(Utils.normalRelativeAngleDegrees(followGun));

        if (!moveRobot) {
            //Executed when the robot is not moving
            if (!targets.getTarget(event.getName()).isFriendly()) {
                if (event.getDistance() > 300) {
                    fire(1);
                } else if (event.getDistance() > 100) {
                    fire(2);
                } else if (event.getDistance() >= 0) {
                    fire(3);
                }
            }
        } else {
            //Executed when the robot is moving
            if (!targets.getTarget(event.getName()).isFriendly()) {
                if (event.getDistance() < 200) {
                    fire(2);
                }
            }
        }
     }

    /**
     * Customized onMessageReceived event which adjusts the
     * side of the second Robot if necessary and uses
     * super.OnMessageReceived
     *
     * @param event - The message
     * @author Tim Hofman
     */
    @Override
    public void onMessageReceived(MessageEvent event) {
        super.onMessageReceived(event);
        if (event.getMessage() instanceof SideMessage) {
            if (!event.getSender().equals(this.getName())) {
                Side side = ((SideMessage) event.getMessage()).getSide();
                //If both CamperBots are on the same side, the second CamperBot will change sides
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

    /**
     * Customized method for CamperBot
     * This method blocks
     *
     * @param heading Desired heading (north referenced)
     *
     * @return The calculated steering angle
     *
     * @author Thymo van Beers
     * @author Tim Hofman
     */
    @Override
    double steerTo(double heading) {
        double steerAngle = heading - getHeading();

        if (steerAngle < -180) {
            steerAngle = 360 - -steerAngle;
        }

        out.println("Steer command: " + steerAngle);

        setTurnRight(steerAngle);

        //If waitFor method is not called, the robot will not drive correctly
        waitFor(new TurnCompleteCondition(this));
        return steerAngle;
    }

}