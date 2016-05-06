package neo4j.services;

import neo4j.Neo4jSessionFactory;
import neo4j.models.edges.QuestionEdge;
import neo4j.models.nodes.Question;
import neo4j.services.utils.GenericService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Lycantropus on 14-04-2016.
 */
//Service("QuestionService")
public class QuestionService extends GenericService<Question>
{
    @Override
    public Class<Question> getEntityType() {
        return Question.class;
    }

    public List<Question> findByCategoryCode(String code, boolean includeProducts)
    {
        String query = null;

        if (includeProducts)
        {
            query = new StringBuilder(
                    "MATCH (c:Category)-[:HAS_QUESTIONS]->(q:Question)-[h:HAS]->(a:Answer)-[i:INFLUENCES]->(at:Attribute)")
                    .append("<-[v:VALUES]-(p:Product) WHERE c.code = '")
                    .append(code)
                    .append("' RETURN q,h,a,i,at,v,p")
                    .toString();
        }
        else
        {
            query = new StringBuilder(
                    "MATCH (c:Category)-[:HAS_QUESTIONS]->(q:Question)-[h:HAS]->(a:Answer)-[i:INFLUENCES]->(at:Attribute)")
                    .append("WHERE c.code = '")
                    .append(code)
                    .append("' RETURN q,h,a,i,at")
                    .toString();
        }

        List<Question> questionList = new ArrayList<>();
        Iterable<Question> iterator = Neo4jSessionFactory.getInstance().getNeo4jSession().query(getEntityType(), query, Collections.EMPTY_MAP);
        iterator.forEach(questionList::add);

        return questionList;
    }

    public List<QuestionEdge> getNextQuestions(String questionCode)
    {
        // MATCH (q1:Question)-[c:CONNECTS]->(q2:Question)-[h:HAS]->(a:Answer)-[i:INFLUENCES]->(at:Attribute)<-[v:VALUES]-(p:Product) WHERE q1.code = 'q1' RETURN q1,c,q2,h,a,i,at,v,p
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
}
