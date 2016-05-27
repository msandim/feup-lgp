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
public class CategoryService extends GenericService<Category> {
    @Override
    public Class<Category> getEntityType() {
        return Category.class;
    }

    public Category findByCode(String code) {
        String query = new StringBuilder("MATCH (c: Category) where c.code = \'").append(code).append("\' return c").toString();

        Iterator<Category> iterator =
                Neo4jSessionFactory.getInstance().getNeo4jSession().query(getEntityType(), query, Collections.EMPTY_MAP).iterator();

        if (iterator.hasNext())
            return iterator.next();
        else
            return null;
    }

    public void deleteByCode(String code) {
        // Delete products:
        String query = new StringBuilder("MATCH (c:Category{code:\'" + code + "\'}) OPTIONAL MATCH (c)-[]->(q:Question)-[]->(a:Answer) OPTIONAL MATCH (c)-[]->(p:Product)-[]->(at:Attribute) detach delete c, q, a, p, at").toString();
        Neo4jSessionFactory.getInstance().getNeo4jSession().query(query, Collections.EMPTY_MAP);

        // Delete attributes that are not connected now:
        String purgeAttributesQuery = new StringBuilder("MATCH (at:Attribute) OPTIONAL MATCH (at)--(p:Product) WHERE p IS NULL delete at").toString();
        Neo4jSessionFactory.getInstance().getNeo4jSession().query(purgeAttributesQuery, Collections.EMPTY_MAP);
    }


}
