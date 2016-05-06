package neo4j.models.nodes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import neo4j.models.Entity;
import neo4j.models.edges.AnswerAttribute;
import neo4j.models.edges.ProductAttribute;
import org.neo4j.ogm.annotation.Relationship;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 * Created by Miguel on 27-04-2016.
 */
public class Attribute extends Entity
{
    private String name;
    @Relationship(type = "VALUES", direction = Relationship.INCOMING)
    @JsonIgnore
    private List<ProductAttribute> products = new ArrayList<>();

    @Relationship(type = "INFLUENCES", direction = Relationship.INCOMING)
    @JsonIgnore
    private List<AnswerAttribute> answers = new ArrayList<>();

    private String type;

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
    public List<ProductAttribute> getProducts() {
        return products;
    }
    public void setProducts(List<ProductAttribute> products) {
        this.products = products;
    }
    public List<AnswerAttribute> getAnswerAttributes()
    {
        return this.answers;
    }
    public String getType()
    {
        return this.type;
    }
    public void setType(String type)
    {
        this.type = type;
    }
    @Override
    public String toString() {
        return "Category{" + "id=" + getId() + ", name=" + name + "} \n";
    }
}