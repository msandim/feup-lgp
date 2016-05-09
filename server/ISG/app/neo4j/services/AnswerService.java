package neo4j.services;

import neo4j.Neo4jSessionFactory;
import neo4j.models.nodes.Answer;
import neo4j.models.nodes.Question;
import neo4j.services.utils.GenericService;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.cypher.Filters;

import java.util.Collections;
import java.util.Iterator;

/**
 * Created by Miguel on 03-05-2016.
 */
public class AnswerService extends GenericService<Answer>
{
    @Override
    public Class<Answer> getEntityType() {
        return Answer.class;
    }

    public Answer findByCode(String questionCode, String answerCode)
    {
        String query = new StringBuilder("MATCH (q: Question)-[:HAS]->(a: Answer) where q.code = '")
                .append(questionCode)
                .append("' and a.code = '")
                .append(answerCode)
                .append("' return a").toString();

        Iterator<Answer> iterator =
                Neo4jSessionFactory.getInstance().getNeo4jSession().query(getEntityType(), query, Collections.EMPTY_MAP).iterator();

        if (iterator.hasNext())
            return iterator.next();
        else
            return null;
    }
}
