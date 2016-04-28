package neo4j.models.edges;

import neo4j.models.Entity;
import neo4j.models.nodes.*;
import org.neo4j.ogm.annotation.*;

/**
 * Created by Miguel on 27-04-2016.
 */
@RelationshipEntity(type = "INFLUENCES")
public class AnswerAttribute extends Entity
{
    @StartNode
    private Answer answer;

    @EndNode
    private Attribute attribute;

    private String operator;

    private String value;

    public interface Operators
    {
        String LESS = "<";
        String LESS_EQUAL = "<=";
        String ABOVE = ">";
        String ABOVE_EQUAL = ">=";
        String EQUAL = "=";
        String NOT_EQUAL = "!=";
    }
}
