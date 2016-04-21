package neo4j.services;

import neo4j.models.Question;
import neo4j.Neo4jSessionFactory;
import java.util.Map;
import java.util.Collections;

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
    public Iterable<Question> getQuestionsFromCategory() {
        String query;
        query = "MATCH (q: question) where q.category = \"TV\" return q"; //'TV' to be changed after QuestionController builds questions with other than TV category

        return Neo4jSessionFactory.getInstance().getNeo4jSession()
                .query(Question.class, query, Collections.EMPTY_MAP);
    }
}
