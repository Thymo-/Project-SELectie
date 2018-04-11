package nl.saxion.ehi1vsb1.messages;

import nl.saxion.ehi1vsb1.CamperBot;

import java.io.Serializable;

/**
 * Message containing the side where the robot is located
 *
 * @author Tim Hofman
 */
public class SideMessage implements Serializable {

    private CamperBot.Side side;

    public SideMessage(CamperBot.Side side) {
        this.side = side;
    }

    public CamperBot.Side getSide() {
        return side;
    }

}
