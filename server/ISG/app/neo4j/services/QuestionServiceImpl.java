package neo4j.services;

import neo4j.models.Question;

/**
 * Created by Lycantropus on 14-04-2016.
 */
public class QuestionServiceImpl extends GenericService<Question> implements QuestionService{

    @Override
    public Class<Question> getEntityType() {
        return Question.class;
    }
}
