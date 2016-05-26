package neo4j.services;

import neo4j.Neo4jSessionFactory;
import neo4j.models.nodes.Attribute;
import neo4j.services.utils.GenericService;

import java.util.Collections;
import java.util.Iterator;

/**
 * Created by Vitor on 30/04/2016.
 */
public class AttributeService extends GenericService<Attribute>
{
    @Override
    public Class<Attribute> getEntityType() {
        return Attribute.class;
    }

    public Attribute findByName(String name)
    {
        String query = new StringBuilder("MATCH (c: Attribute) where c.name = \'").append(name).append("\' return c").toString();

        Iterator<Attribute> iterator =
                Neo4jSessionFactory.getInstance().getNeo4jSession().query(getEntityType(), query, Collections.EMPTY_MAP).iterator();

        if (iterator.hasNext())
            return iterator.next();
        else
            return null;
    }

    public void deleteNotConnectedAttributes()
    {
        // Delete attributes that are not connected now:
        String purgeAttributesQuery= new StringBuilder("MATCH (at:Attribute) OPTIONAL MATCH (at)--(p:Product) WHERE p IS NULL delete at").toString();
        Neo4jSessionFactory.getInstance().getNeo4jSession().query(purgeAttributesQuery, Collections.EMPTY_MAP);
    }
}
