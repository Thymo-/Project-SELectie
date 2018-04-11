package nl.saxion.ehi1vsb1;

import nl.saxion.ehi1vsb1.data.*;
import nl.saxion.ehi1vsb1.messages.*;
import robocode.*;
import robocode.util.Utils;

import java.io.IOException;

/**
 * Abstract class that implements generic stuff for all bots
 *
 * @author Thymo van Beers
 */
abstract public class TeamRobot extends robocode.TeamRobot {
    TargetMap targets;
    String currentTarget;

    private int scanMode;
    private boolean active;

    // Radar modes
    static final int SCAN_SEARCH = 0;
    static final int SCAN_LOCK = 1;
    static final int SCAN_NOOP = 2;


    public TeamRobot() {
        targets = new TargetMap();
        scanMode = SCAN_SEARCH;
        active = true;
    }

    int getScanMode() {
        return scanMode;
    }

    /**
     * Set the new mode the radar needs to operate in.
     * Calls radarStep to make mode switching happen ASAP.
     *
     * @param scanMode SCAN_SEARCH or SCAN_LOCK
     *
     * @author Thymo van Beers
     */
    void setScanMode(int scanMode) {
        switch (scanMode) {
            case SCAN_SEARCH:
                out.println("Radar: Now in SEARCH mode.");
                break;
            case SCAN_LOCK:
                out.println("Radar: Now in LOCK mode. Target: " + currentTarget);
                break;
            case SCAN_NOOP:
                out.println("Radar: Now in NOOP mode.");
                break;
        }

        active = true;
        this.scanMode = scanMode;
    }

    void setCurrentTarget(String targetName) {
        this.currentTarget = targetName;
    }

    /**
     * Given a new position calculate the angle to it based
     * on the current position.
     *
     * @param xcmd Desired X position
     * @param ycmd Desired Y position
     *
     * @return double: Required steering angle
     *
     * @author Thymo van Beers
     */
    double calcHeading(double xcmd, double ycmd) {
        double dX = xcmd - getX();
        double dY = ycmd - getY();

        return Math.toDegrees(Math.atan2(dX, dY));
    }

    /**
     * Calculate the distance to a point in the arena
     *
     * @param xcmd Desired X position
     * @param ycmd Desired Y position
     *
     * @return double: Distance to point
     *
     * @author Thymo van Beers
     */
    double calcDistance(double xcmd, double ycmd) {
        double dX = xcmd - getX();
        double dY = ycmd - getY();

        return (Math.sqrt((dX * dX + dY * dY)));
    }

    /**
     * Given a heading calculate the steering angle
     * and steer to it
     * This method blocks
     *
     * @param heading Desired heading (north referenced)
     *
     * @return The calculated steering angle
     *
     * @author Thymo van Beers
     */
    double steerTo(double heading) {
        double steerAngle = heading - getHeading();

        if (steerAngle < -180) {
            steerAngle = 360 - -steerAngle;
        }

        out.println("Steer command: " + steerAngle);

        setTurnRight(steerAngle);
        return steerAngle;
    }

    /**
     * Move to a specified coordinate
     *
     * @param xcmd X position
     * @param ycmd Y position
     *
     * @author Thymo van Beers
     */
    void moveTo(double xcmd, double ycmd) {
        double fieldHeight = getBattleFieldHeight();
        double fieldWidth = getBattleFieldWidth();
        double botWidth = getWidth();
        double botHeight = getHeight();

        //Avoid running into field border
        if (xcmd < botWidth) {
            xcmd = botWidth;
        } else if (xcmd > fieldWidth - botWidth) {
            xcmd = fieldWidth - botWidth;
        }

        if (ycmd < botHeight) {
            ycmd = botHeight;
        } else if (ycmd > fieldHeight - botHeight) {
            ycmd = fieldHeight - botHeight;
        }

        steerTo(calcHeading(xcmd, ycmd));
        setAhead(calcDistance(xcmd, ycmd));
        waitFor(new MoveCompleteCondition(this));
    }

    /**
     * Evade and turn to target
     *
     * @param target Target to evade
     *
     * @author Sieger van Breugel
     */
    void evade(Target target) {
        double xPos = this.getX();
        double yPos = this.getY();

        //If robot is on left side
        if (xPos < getBattleFieldWidth() / 2) {
            if (this.getHeading() < 90 || this.getHeading() > 270) {
                setTurnRight(90);
            } else {
                setTurnLeft(90);
            }
        }
        //If robot is on right side
        else if (xPos > getBattleFieldWidth() / 2) {
            if (this.getHeading() < 90 || this.getHeading() > 270) {
                setTurnLeft(90);
            } else {
                setTurnRight(90);
            }
        }
        setAhead(36);
        waitFor(new TurnCompleteCondition(this));
        steerTo(calcHeading(target.getxPos(), target.getyPos()));
    }

    /**
     * Simple scan handler that scans a robot and compares it to the current target.
     *
     * @param event - The scanned robot
     *
     * @author Thymo van Beers
     */
    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        double angleToEnemy = event.getBearing();

        double angle = Math.toRadians((getHeading() + angleToEnemy % 360));

        double enemyX = (getX() + Math.sin(angle) * event.getDistance());
        double enemyY = (getY() + Math.cos(angle) * event.getDistance());

        boolean friendly = false;

        if (event.getName().contains("ehi1vsb1")) {
            friendly = true;
        }
        Target scannedTarget = new Target(enemyX, enemyY, event.getBearing(), event.getEnergy(),
                event.getDistance(), event.getHeading(), event.getVelocity(), friendly, (int) getTime(), event.getName());

        try {
            sendMessage("target_found", new TargetMessage(scannedTarget));
        } catch (IOException e) {
            e.printStackTrace();
        }

        targets.addTarget(scannedTarget);
    }

    @Override
    public void onMessageReceived(MessageEvent event) {
        if (event.getMessage() instanceof TargetMessage) {
            Target messageTarget = ((TargetMessage) event.getMessage()).getTarget();
            targets.addTarget(messageTarget);
        }
    }

    /**
     * Run initialization for all robots
     * Initializes the radar and does initial sweep
     *
     * @author Thymo van Beers
     */
    public void run() {
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        scanAll();
    }

    /**
     * Radar controller.
     * This method should be called each round. Especially when in lock mode
     * in order not to lose the lock.
     *
     * In scan mode:
     *      - Check if the radar is about to stop turning (it shouldn't, but who knows)
     *      - Set the radar to turn infinitely
     * In lock mode:
     *      - Arc around the target and narrow until target locked
     * In no-op mode:
     *      - Stops the radar if it's active
     *      - Relinquishes radar control to child
     *
     * @author Thymo van Beers
     */
    void radarStep() {
        if (scanMode == SCAN_SEARCH) {
                setTurnRadarRight(Double.POSITIVE_INFINITY); // Spin forever
                execute();
        } else if (scanMode == SCAN_LOCK) {
            Target radarTarget = targets.getTarget(currentTarget);

            if (radarTarget == null) { // No target, fall back to search mode.
                out.println("Radar: Could not find target to lock!");
                out.println("       Falling back to search mode.");
                setScanMode(SCAN_SEARCH);
                return;
            }

            double radarTurn =
                    // Absolute bearing to target
                    getHeading() + radarTarget.getBearing()
                    // Subtract current heading to get turn cmd
                    - getRadarHeading();

            double turnCmd = 2.0 * Utils.normalRelativeAngleDegrees(radarTurn);
            setTurnRadarRight(turnCmd);
            execute();
        } else if (scanMode == SCAN_NOOP) {
            if (active) {
                active = false;
                setTurnRadarRight(0);
                execute();
            }
        }
    }

    /**
     * Switch radar to scan mode and do a single accelerated scan.
     * This method blocks until the scan pass is complete.
     *
     * @author Thymo van Beers
     */
    void scanAll() {
        setScanMode(SCAN_SEARCH); // Reset radar to scan mode to be sure
        setTurnRadarRight(Double.POSITIVE_INFINITY); // Spin forever
        setTurnGunRight(360);   // Turn gun one rotation for faster scanning
        execute();
        waitFor(new GunTurnCompleteCondition(this));
    }
}
