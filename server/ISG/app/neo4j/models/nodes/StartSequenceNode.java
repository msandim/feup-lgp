package neo4j.models.nodes;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import neo4j.models.Entity;
import play.libs.Json;

/**
 * Created by Miguel on 12-05-2016.
 */
public class StartSequenceNode extends Entity
{
    private SequenceNode nextNode;
    private Integer feedback;

    public StartSequenceNode()
    { }

    public StartSequenceNode(SequenceNode nextNode, Integer feedback)
    {
        this.nextNode = nextNode;
        this.feedback = feedback;
    }

    public SequenceNode getNextNode()
    {
        return nextNode;
    }

    public ObjectNode toJson()
    {
        ObjectNode sequence = Json.newObject();
        sequence.put("feedback", feedback);

        ArrayNode array = sequence.putArray("questions");
        SequenceNode sequenceNode = nextNode;

        while(sequenceNode != null)
        {
            ObjectNode node = array.addObject();
            node.set("question", sequenceNode.getQuestion().toJson());
            node.set("selected_answer", sequenceNode.getAnswer().toJson());
            sequenceNode = sequenceNode.getNextNode();
        }

        return sequence;
    }
}
