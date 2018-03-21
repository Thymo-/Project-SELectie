package nl.saxion.ehi1vsb1;

import robocode.TurnCompleteCondition;

abstract public class TeamRobot extends robocode.TeamRobot {

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
    protected double calcHeading(double xcmd, double ycmd) {
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
    protected double calcDistance(double xcmd, double ycmd) {
        double dX = xcmd - getX();
        double dY = ycmd - getY();

        return (Math.sqrt((dX*dX+dY*dY)));
    }

    /**
     * Given a heading calculate the steering angle
     * and steer to it
     *
     * @param heading Desired heading (north referenced)
     * @return The calculated steering angl
     *
     * @author Thymo van Beers
     */
    protected double steerTo(double heading) {
        double steerAngle = heading - getHeading();

        if (steerAngle < -180) {
            steerAngle = 360 - -steerAngle;
        }

        out.println("Steer command: " + steerAngle);

        setTurnRight(steerAngle);
        waitFor(new TurnCompleteCondition(this));
        return steerAngle;
    }
}
