package nl.saxion.ehi1vsb1.data;

/**
 * Target data
 *
 * @author Tim Hofman
 */
public class Target implements Comparable<Target> {

    private double xPos;
    private double yPos;
    private double bearing;

    private double energy;

    private double distance;
    private double heading;
    private double velocity;

    private int turn;

    private String name;

    public Target(double xPos, double yPos, double bearing, double energy, double distance, double heading, double velocity, int turn, String name) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.bearing = bearing;
        this.energy = energy;
        this.distance = distance;
        this.heading = heading;
        this.velocity = velocity;
        this.turn = turn;
        this.name = name;
    }

    /**
     * Given a target compare it to the current target
     *
     * @param o The target to be compared
     * @return int: The value 0 if o == Target; a value less
     * than 0 if o < Target; and a value greater than 0 if o > Target
     *
     * @author Tim Hofman
     */
    @Override
    public int compareTo(Target o) {
        return Integer.compare(this.getTurn(), o.getTurn());
    }

    public double getxPos() {
        return xPos;
    }

    public void setxPos(double xPos) {
        this.xPos = xPos;
    }

    public double getyPos() {
        return yPos;
    }

    public void setyPos(double yPos) {
        this.yPos = yPos;
    }

    public double getBearing() {
        return bearing;
    }

    public void setBearing(double bearing) {
        this.bearing = bearing;
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
