package nl.saxion.ehi1vsb1;

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
}
