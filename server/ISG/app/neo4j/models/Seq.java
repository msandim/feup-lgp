package neo4j.models;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.StartNode;

/**
 * Created by Lycantropus on 14-04-2016.
 */
public class Seq {

    @GraphId
    private Long id;

    @StartNode
    private Question origin;

    @EndNode
    private Question destiny;

    public Seq(){}

    public Seq(Question origin, Question destiny)
    {
        this.origin=origin;
        this.destiny=destiny;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
