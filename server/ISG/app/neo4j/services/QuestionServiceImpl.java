package neo4j.services;

import neo4j.Neo4jSessionFactory;

import neo4j.models.Question;

import java.util.Collections;

/**
 * Created by Lycantropus on 14-04-2016.
 */
public class QuestionServiceImpl extends GenericService<Question> implements QuestionService{

    @Override
    public Class<Question> getEntityType() {
        return Question.class;
    }


}
