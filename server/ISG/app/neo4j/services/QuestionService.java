package neo4j.services;

import neo4j.models.nodes.Question;

/**
 * Created by Lycantropus on 14-04-2016.
 */
public interface QuestionService extends Service<Question> {

    Iterable<Question> getQuestionsFromCategory(String category);
}
