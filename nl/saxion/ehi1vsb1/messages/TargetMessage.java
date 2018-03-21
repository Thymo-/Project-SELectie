package nl.saxion.ehi1vsb1.messages;

import nl.saxion.ehi1vsb1.data.Target;

import java.io.Serializable;

/**
 * Message containing target data
 *
 * @author Thymo van Beers
 */
public class TargetMessage implements Serializable {
    private Target target;

    public TargetMessage(Target target) {
        this.target = target;
    }

    public Target getTarget() {
        return target;
    }
}
