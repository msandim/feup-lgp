package neo4j.services;

import neo4j.Neo4jSessionFactory;
import neo4j.models.edges.QuestionEdge;
import neo4j.models.nodes.Question;
import neo4j.services.utils.GenericService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Miguel on 09-05-2016.
 */
public class QuestionEdgeService extends GenericService<QuestionEdge>
{
    @Override
    public Class<QuestionEdge> getEntityType() {
        return QuestionEdge.class;
    }

    public List<QuestionEdge> getNextQuestions(String questionCode)
    {
        String query = new StringBuilder(
                "MATCH (q1:Question)-[c:CONNECTS]->(q2:Question)-[h:HAS]->(a:Answer)-[i:INFLUENCES]->(at:Attribute)")
                .append("<-[v:VALUES]-(p:Product) WHERE q1.code = '")
                .append(questionCode)
                .append("' RETURN q1,c,q2,h,a,i,at,v,p")
                .toString();

        List<QuestionEdge> questionEdgeList = new ArrayList<>();
        Iterable<QuestionEdge> iterator = Neo4jSessionFactory.getInstance().getNeo4jSession().query(QuestionEdge.class, query, Collections.EMPTY_MAP);
        iterator.forEach(questionEdgeList::add);

        return questionEdgeList;
    }

    public QuestionEdge getQuestionEdge(String questionCode1, String questionCode2)
    {
        String query = new StringBuilder(
                "MATCH (q1:Question)-[c:CONNECTS]->(q2:Question) WHERE q1.code = '")
                .append(questionCode1)
                .append("' AND q2.code = '")
                .append(questionCode2)
                .append("' RETURN q1,c,q2")
                .toString();

        Iterator<QuestionEdge> iterator = Neo4jSessionFactory.getInstance().getNeo4jSession().query(QuestionEdge.class, query, Collections.EMPTY_MAP).iterator();

        if (iterator.hasNext())
            return iterator.next();
        else
            return null;
    }
}
