package neo4j.services;

import neo4j.models.nodes.SequenceNode;
import neo4j.models.nodes.StartSequenceNode;
import neo4j.services.utils.GenericService;

/**
 * Created by Miguel on 12-05-2016.
 */
public class SequenceNodeService extends GenericService<SequenceNode>
{
    @Override
    public Class<SequenceNode> getEntityType()
        {
            return SequenceNode.class;
        }
}
