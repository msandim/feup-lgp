package neo4j.models.nodes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import neo4j.models.Entity;
import org.neo4j.ogm.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Lycantropus on 16-04-2016.
 */

@NodeEntity
public class Category extends Entity
{
    private String name;
    private String code;

    //@Relationship(type = "HAS_PRODUCTS")
    //@JsonIgnore
    //private List<Product> products = new ArrayList<>();

    //@Relationship(type = "HAS_QUESTIONS")
    //@JsonIgnore
    //private List<Question> questions = new ArrayList<>();

    public Category() {}

    public Category(String name, String code)
    {
        this.name = name;
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public String getCode()
    {
        return code;
    }
}
