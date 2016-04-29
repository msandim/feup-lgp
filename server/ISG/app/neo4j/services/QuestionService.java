package neo4j.services;

import neo4j.Neo4jSessionFactory;

import neo4j.models.nodes.Question;
import neo4j.services.utils.GenericService;

import java.lang.StringBuilder;
import java.util.Collections;

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

    public Iterable<Question> getQuestionsFromCategory(String category) {
        String query = new StringBuilder("MATCH (q: Question) where q.category = \'").append(category).append("\' return q").toString();

        return Neo4jSessionFactory.getInstance().getNeo4jSession().query(Question.class, query, Collections.EMPTY_MAP);
    }
}
