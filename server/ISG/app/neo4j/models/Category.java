package neo4j.models;

import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by Lycantropus on 16-04-2016.
 */

@NodeEntity(label="category")
public class Category extends Entity {

    private String name;

    public Category() {}

    public Category(String text)
    {
        this.name=text;
    }

    @Override
    public String toString() {
        return "Category{" + "id=" + getId() + ", name=" + name + "} \n";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
