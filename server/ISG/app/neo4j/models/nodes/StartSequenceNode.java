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

    public StartSequenceNode()
    { }

    public StartSequenceNode(SequenceNode nextNode)
    {
        this.nextNode = nextNode;
    }

    public SequenceNode getNextNode()
    {
        return nextNode;
    }

    public ArrayNode toJson()
    {
        ArrayNode array = Json.newArray();
        SequenceNode sequenceNode = nextNode;

        while(sequenceNode != null)
        {
            ObjectNode node = array.addObject();
            node.set("question", sequenceNode.getQuestion().toJson());
            node.set("selected_answer", sequenceNode.getAnswer().toJson());
            sequenceNode = sequenceNode.getNextNode();
        }

        return array;
    }
}
