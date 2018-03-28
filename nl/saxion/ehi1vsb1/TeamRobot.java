package nl.saxion.ehi1vsb1;

import nl.saxion.ehi1vsb1.data.Target;
import nl.saxion.ehi1vsb1.data.TargetMap;
import robocode.*;

abstract public class TeamRobot extends robocode.TeamRobot {
    protected RobotStatus status;
    protected TargetMap targets;

    //TODO: Hook up to TargetMap
    Target currentTarget = null;

    public TeamRobot() {
        status = null;
        targets = new TargetMap();
    }

    /**
     * Given a new position calculate the angle to it based
     * on the current position.
     *
     * @param xcmd Desired X position
     * @param ycmd Desired Y position
     * @return double: Required steering angle
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
     * @return double: Distance to point
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
     * @return The calculated steering angle
     * @author Thymo van Beers
     */
    double steerTo(double heading) {
        double steerAngle = heading - getHeading();

        if (steerAngle < -180) {
            steerAngle = 360 - -steerAngle;
        }

        out.println("Steer command: " + steerAngle);

        setTurnRight(steerAngle);
        waitFor(new TurnCompleteCondition(this));
        return steerAngle;
    }

    /**
     * Move to a specified coordinate
     * This method blocks
     *
     * @param xcmd X position
     * @param ycmd Y position
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
    protected void evade(Target target) {
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
        double ownX = getX();
        double ownY = getY();

        double scanX = event.getDistance()*Math.cos(event.getBearing())+ownX;
        double scanY = event.getDistance()*Math.sin(event.getBearing())+ownY;

        Target scannedTarget = new Target(scanX, scanY, event.getBearing(), event.getEnergy(),
                event.getDistance(), event.getHeading(), event.getVelocity(), status.getRoundNum(), event.getName());

        if (!targets.exists(scannedTarget)) {
            targets.addTarget(scannedTarget);
        }
    }

    @Override
    public void onStatus(StatusEvent e) {
        status = e.getStatus();
    }

    public void run() {
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        scanAll();
    }

    private void scanAll() {
        setTurnRadarRight(Double.POSITIVE_INFINITY); // Spin forever
        setTurnGunRight(360);   // Turn gun one rotation for faster scanning
        execute();
        waitFor(new GunTurnCompleteCondition(this));
    }
}
