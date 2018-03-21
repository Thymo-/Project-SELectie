package nl.saxion.ehi1vsb1;

import nl.saxion.ehi1vsb1.data.Target;
import nl.saxion.ehi1vsb1.data.TargetMap;
import robocode.MoveCompleteCondition;
import robocode.TurnCompleteCondition;

abstract public class TeamRobot extends robocode.TeamRobot {
    TargetMap targets;

    /**
     * Given a new position calculate the angle to it based
     * on the current position.
     *
     * @param xcmd Desired X position
     * @param ycmd Desired Y position
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
     * @return double: Distance to point
     *
     * @author Thymo van Beers
     */
    double calcDistance(double xcmd, double ycmd) {
        double dX = xcmd - getX();
        double dY = ycmd - getY();

        return (Math.sqrt((dX*dX+dY*dY)));
    }

    /**
     * Given a heading calculate the steering angle
     * and steer to it
     * This method blocks
     *
     * @param heading Desired heading (north referenced)
     * @return The calculated steering angl
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
        waitFor(new TurnCompleteCondition(this));
        return steerAngle;
    }

    /**
     * Move to a specified coordinate
     * This method blocks
     *
     * @param xcmd X position
     * @param ycmd Y position
     *
     * @author Thymo van Beers
     */
    void moveTo(double xcmd, double ycmd) {
        double fieldHeigt = getBattleFieldHeight();
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
        } else if (ycmd > fieldHeigt - botHeight) {
            ycmd = fieldHeigt - botHeight;
        }

        steerTo(calcHeading(xcmd, ycmd));
        setAhead(calcDistance(xcmd, ycmd));
        waitFor(new MoveCompleteCondition(this));
    }
}
