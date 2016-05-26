package neo4j.models.nodes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import neo4j.models.Entity;
import neo4j.models.edges.ProductAttribute;
import org.neo4j.ogm.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lycantropus on 15-04-2016.
 */


@NodeEntity
public class Product extends Entity
{
    private String name;
    private String ean;
    private Float price;


    @Relationship(type = "VALUES")
    @JsonIgnore
    private List<ProductAttribute> attributes = new ArrayList<>();

    @Relationship(type = "HAS_PRODUCTS", direction = Relationship.INCOMING)
    private Category category;



    //@Transient
    //private Float currentScore = (float) 0.0;

    public Product() {}

    public Product(String name, String ean, Float price)
    {
        this.name = name;
        this.ean = ean;
        this.price = price;
    }

    public Product(String name, String ean, Float price, Category category)
    {
        this.name = name;
        this.ean = ean;
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



    public String getEan()
    {
        return this.ean;
    }

    // Hashcode of each product is the ean's hashcode:
    @Override
    public int hashCode()
    {
        return this.ean.hashCode();
    }

    // Two Products are the same if they have the same ean:
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Product)
        {
            Product p = (Product) obj;
            return (p.ean.equals(this.ean));
        } else
        {
            return false;
        }
    }

    @Override
    public String toString() {
        return this.ean;
    }

    public List<ProductAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<ProductAttribute> attributes) {
        this.attributes = attributes;

    }


}
