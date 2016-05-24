package neo4j.services;

import neo4j.Neo4jSessionFactory;
import neo4j.models.edges.QuestionEdge;
import neo4j.models.nodes.Category;
import neo4j.models.nodes.Question;
import neo4j.services.utils.GenericService;
import scala.Console;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
        String query;

        if (includeProducts)
        {
            query = new StringBuilder(
                    "MATCH (c:Category)-[:HAS_QUESTIONS]->(q:Question)-[h:HAS]->(a:Answer) WHERE c.code = '")
                    .append(code)
                    .append("' OPTIONAL MATCH (a)-[i:INFLUENCES*0..]->(at:Attribute)")
                    .append(" OPTIONAL MATCH (at)<-[v:VALUES]-(p:Product) RETURN q,h,a,i,at,v,p")
                    .toString();
        }
        else
        {
            query = new StringBuilder(
                    "MATCH (c:Category)-[:HAS_QUESTIONS]->(q:Question)-[h:HAS]->(a:Answer) WHERE c.code = '")
                    .append(code)
                    .append("' OPTIONAL MATCH (a)-[i:INFLUENCES*0..]->(at:Attribute)")
                    .append(" RETURN q,h,a,i,at")
                    .toString();
        }

        List<Question> questionList = new ArrayList<>();
        Iterable<Question> iterator = Neo4jSessionFactory.getInstance().getNeo4jSession().query(getEntityType(), query, Collections.EMPTY_MAP);
        iterator.forEach(questionList::add);

        return questionList;
    }

    public Question findByCode(String code)
    {
        String query = new StringBuilder("MATCH (q: Question) where q.code = '").append(code).append("' return q").toString();

        Iterator<Question> iterator =
                Neo4jSessionFactory.getInstance().getNeo4jSession().query(getEntityType(), query, Collections.EMPTY_MAP).iterator();

        if (iterator.hasNext())
            return iterator.next();
        else
            return null;
    }
}
