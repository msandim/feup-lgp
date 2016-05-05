package neo4j.models.edges;

import neo4j.models.Entity;
import neo4j.models.nodes.*;
import org.neo4j.ogm.annotation.*;

/**
 * Created by Miguel on 27-04-2016.
 */
@RelationshipEntity(type = "VALUES")
public class ProductAttribute extends Entity
{
    @StartNode
    private Product product;

    @EndNode
    private Attribute attribute;

    private String value;

    public ProductAttribute(Product product, Attribute attribute,String value)
    {
        this.product=product;
        this.attribute=attribute;
        this.value=value;
    }
}
