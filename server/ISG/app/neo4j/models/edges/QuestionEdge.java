package neo4j.models.edges;

import neo4j.models.nodes.Entity;
import neo4j.models.nodes.Question;
import org.neo4j.ogm.annotation.*;

/**
 * Created by Miguel on 27-04-2016.
 */
@RelationshipEntity(type = "FOLLOWS")
public class QuestionEdge extends Entity
{
    @StartNode
    private Question previousQuestion;

    @EndNode
    private Question nextQuestion;

    private Float varianceGain;
    private Float goodSequenceRatio;
}
