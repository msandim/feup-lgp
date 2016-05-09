package neo4j.models.nodes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import neo4j.models.Entity;
import neo4j.models.edges.AnswerAttribute;
import org.neo4j.ogm.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Miguel on 26-04-2016.
 */

@NodeEntity
public class Answer extends Entity
{
    private String code;
    private String text;

    @JsonIgnore
    private Float frequency;

    @Relationship(type = "INFLUENCES")
    private List<AnswerAttribute> attributes = new ArrayList<>();

    public Answer() { }

    public Answer(String name) {
        this.text = name;
    }

    public List<AnswerAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AnswerAttribute> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString()
    {
        return "Answer{" + "id=" + getId()
                + ", name=" + text
                + ", frequency=" + frequency
                + "}\n";
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    @Override
    public int hashCode()
    {
        return this.code.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Answer)
        {
            Answer a = (Answer) obj;
            return (a.code.equals(this.code));
        } else
        {
            return false;
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
