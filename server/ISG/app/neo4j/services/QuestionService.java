package neo4j.services;

import neo4j.models.Question;
import java.util.Map;

/**
 * Created by Lycantropus on 14-04-2016.
 */
public interface QuestionService extends Service<Question> {

    Iterable<Question> getQuestionsFromCategory();
}
