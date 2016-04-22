package neo4j.models;

import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by Lycantropus on 15-04-2016.
 */


@NodeEntity(label="product")
public class Product extends Entity{

    String name;

    Float price;

    public Product() {}

    public Product(String name, Float price)
    {
        this.name=name;
        this.price=price;
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
