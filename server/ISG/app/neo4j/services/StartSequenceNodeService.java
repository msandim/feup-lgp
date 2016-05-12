package neo4j.services;

import neo4j.models.nodes.StartSequenceNode;
import neo4j.services.utils.GenericService;

/**
 * Created by Miguel on 12-05-2016.
 */
public class StartSequenceNodeService extends GenericService<StartSequenceNode>
{
    @Override
    public Class<StartSequenceNode> getEntityType()
    {
        return StartSequenceNode.class;
    }
}
