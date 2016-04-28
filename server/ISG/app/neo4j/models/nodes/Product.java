package neo4j.models.nodes;

import neo4j.models.Entity;
import neo4j.models.edges.ProductAttribute;
import org.neo4j.ogm.annotation.*;

import java.util.Set;

/**
 * Created by Lycantropus on 15-04-2016.
 */


@NodeEntity
public class Product extends Entity
{
    private String name;
    private String EAN;
    private Float price;

    @Relationship(type = "HAS")
    private Set<ProductAttribute> attributes;

    @Relationship(type = "HAS_PRODUCTS", direction = Relationship.INCOMING)
    private Category category;

    public Product() {}

    public Product(String name, String EAN, Float price, Category category)
    {
        this.name = name;
        this.EAN = EAN;
        this.price = price;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }
}