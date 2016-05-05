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

    public Iterable<Question> findByCategoryCode(String code)
    {
        return Neo4jSessionFactory.getInstance().getNeo4jSession().query(getEntityType(), "MATCH (q:Question)-[x:HAS]->(a:Answer)-[i:INFLUENCES]->(t:Attribute) RETURN q,x,a,i,t", Collections.EMPTY_MAP);
    }
}
