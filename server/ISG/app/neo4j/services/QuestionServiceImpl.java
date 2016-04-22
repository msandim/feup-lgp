package neo4j.services;

import neo4j.models.Question;
import neo4j.Neo4jSessionFactory;
import java.util.Map;
import java.util.Collections;
import java.lang.StringBuilder;

/**
 * Created by Lycantropus on 14-04-2016.
 */
//Service("QuestionService")
public class QuestionServiceImpl extends GenericService<Question> implements QuestionService{

    @Override
    public Class<Question> getEntityType() {
        return Question.class;
    }

    @Override
    public Iterable<Question> getQuestionsFromCategory(String category) {
        String query = new StringBuilder("MATCH (q: question) where q.category = \'").append(category).append("\' return q").toString();

        return Neo4jSessionFactory.getInstance().getNeo4jSession()
                .query(Question.class, query, Collections.EMPTY_MAP);
    }
}
