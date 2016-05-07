package neo4j.services;

import neo4j.Neo4jSessionFactory;
import neo4j.models.nodes.Answer;
import neo4j.models.nodes.Question;
import neo4j.services.utils.GenericService;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.cypher.Filters;

import java.util.Collections;

/**
 * Created by Miguel on 03-05-2016.
 */
public class AnswerService extends GenericService<Answer>
{
    @Override
    public Class<Answer> getEntityType() {
        return Answer.class;
    }

    /*
    public Iterable<Answer> findByQuestionId(Long id)
    {
        return findAllDetailedWithFilters(new Filters().add(new Filter().))
    }*/
}
