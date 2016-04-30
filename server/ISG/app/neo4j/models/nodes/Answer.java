package neo4j.models.nodes;

import neo4j.models.Entity;
import neo4j.models.edges.AnswerAttribute;
import org.neo4j.ogm.annotation.*;

import java.util.Set;

/**
 * Created by Miguel on 26-04-2016.
 */

@NodeEntity
public class Answer extends Entity
{
    private String name;

    private Float frequency;

    @Relationship(type = "INFLUENCES")
    private Set<AnswerAttribute> attributes;

    public Answer(String name) {
        this.name = name;
    }

    public Set<AnswerAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<AnswerAttribute> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString()
    {
        return "Answer{" + "id=" + getId()
                + ", name=" + name
                + ", frequency=" + frequency
                + "}\n";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFrequency(Float frequency)
    {
        this.frequency = frequency;
    }

    public Float getFrequency()
    {
        return frequency;
    }
}
