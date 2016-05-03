package neo4j.models.nodes;

import neo4j.models.Entity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

/**
 * Created by Miguel on 27-04-2016.
 */
public class Attribute extends Entity
{
    private String name;

    @Relationship(type = "HAS", direction = Relationship.INCOMING)
    private Set<Product> products;

    public Attribute() {}

    public Attribute(String name)
    {
        this.name=name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    @Override

    public String toString() {
        return "Category{" + "id=" + getId() + ", name=" + name + "} \n";
    }
}
