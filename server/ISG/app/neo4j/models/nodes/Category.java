package neo4j.models.nodes;

import neo4j.models.Entity;
import org.neo4j.ogm.annotation.*;

import java.util.Set;

/**
 * Created by Lycantropus on 16-04-2016.
 */

@NodeEntity
public class Category extends Entity
{

    private String name;
    private String code;

    @Relationship(type = "HAS_PRODUCTS")
    private Set<Product> products;
    @Relationship(type = "HAS_QUESTIONS")
    private Set<Question> questions;

    public Category() {}

    public Category(String name, String code)
    {
        this.name = name;
        this.code = code;
    }

    @Override
    public String toString() {
        return "Category{" + "id=" + getId() + ", name=" + name + "} \n";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
