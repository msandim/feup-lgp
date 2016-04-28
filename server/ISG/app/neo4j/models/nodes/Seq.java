package neo4j.models.nodes;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.StartNode;

/**
 * Created by Lycantropus on 14-04-2016.
 */
public class Seq extends Entity
{
    @StartNode
    private Question origin;

    @EndNode
    private Question destiny;

    private Float varianceGain;

    private Float SequenceEffectiveness;

    public Seq() {}

    public Seq(Question origin, Question destiny)
    {
        this.origin=origin;
        this.destiny=destiny;
    }

    public Question getOrigin() {
        return origin;
    }

    public void setOrigin(Question origin) {
        this.origin = origin;
    }

    public Question getDestiny() {
        return destiny;
    }

    public void setDestiny(Question destiny) {
        this.destiny = destiny;
    }
}
