package neo4j.services;

import neo4j.Neo4jSessionFactory;
import neo4j.models.nodes.Category;
import neo4j.models.nodes.Question;
import neo4j.services.utils.GenericService;

import java.util.Collections;
import java.util.Iterator;

/**
 * Created by Lycantropus on 16-04-2016.
 */
public class CategoryService extends GenericService<Category>
{
    @Override
    public Class<Category> getEntityType() {
        return Category.class;
    }

    public Category findByCodename(String codename)
    {
        String query = new StringBuilder("MATCH (c: Category) where c.codename = \'").append(codename).append("\' return c").toString();

        Iterator<Category> iterator =
                Neo4jSessionFactory.getInstance().getNeo4jSession().query(getEntityType(), query, Collections.EMPTY_MAP).iterator();

        if (iterator.hasNext())
            return iterator.next();
        else
            return null;
    }
}
