package neo4j.models.nodes;

import neo4j.models.Entity;

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
}
