package neo4j.services;

import neo4j.Neo4jSessionFactory;

import neo4j.models.edges.AnswerAttribute;
import neo4j.models.nodes.Answer;
import neo4j.models.nodes.Question;
import neo4j.services.utils.GenericService;
import org.neo4j.ogm.model.Result;

import java.lang.StringBuilder;
import java.util.Collections;
import java.util.Map;

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

    public Result findByCategoryCode(String code)
    {
        String query = new StringBuilder("MATCH (pr)-[x:INFLUENCES]->(a) return pr,x").toString();
        return Neo4jSessionFactory.getInstance().getNeo4jSession().query(query, Collections.EMPTY_MAP);
        //return Neo4jSessionFactory.getInstance().getNeo4jSession().loadAll(getEntityType(), 2);
    }
}
