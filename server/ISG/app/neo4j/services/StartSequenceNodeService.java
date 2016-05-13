package neo4j.services;

import neo4j.Neo4jSessionFactory;
import neo4j.models.nodes.Question;
import neo4j.models.nodes.StartSequenceNode;
import neo4j.services.utils.GenericService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public List<StartSequenceNode> findByCategoryCode(String code)
    {
        String query = new StringBuilder(
                    "MATCH (a1:StartSequenceNode)-[s1:NEXT_NODE*0..]->(a2:SequenceNode) ")
                    .append("MATCH (a3:Answer)<-[s2:ANSWER]-(se)-[s3:QUESTION]->(a4:Question)-[s4:HAS]->(a5:Answer) ")
                    .append("MATCH (a4)<--(c:Category) ")
                    .append("WHERE c.code = '")
                    .append(code)
                    .append("' RETURN a1, a2, a3, a4, a5, s1, s2, s3, s4")
                    .toString();

        List<StartSequenceNode> sequenceList = new ArrayList<>();
        Iterable<StartSequenceNode> iterator = Neo4jSessionFactory.getInstance().getNeo4jSession().query(getEntityType(), query, Collections.EMPTY_MAP);
        iterator.forEach(sequenceList::add);

        return sequenceList;
    }
}
