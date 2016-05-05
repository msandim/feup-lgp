package neo4j.models.nodes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import neo4j.models.Entity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Miguel on 27-04-2016.
 */
public class Attribute extends Entity
{
    private String name;

    @Relationship(type = "VALUES", direction = Relationship.INCOMING)
    @JsonIgnore
    private Set<Product> products = new HashSet<>();

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
