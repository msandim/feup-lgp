package neo4j.models.nodes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import neo4j.models.Entity;
import neo4j.models.edges.AnswerAttribute;
import org.neo4j.ogm.annotation.*;
import utils.IdGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Miguel on 26-04-2016.
 */

@NodeEntity
public class Answer extends Entity
{
    private String code;
    private String text;

    @JsonIgnore
    private Long numberOfTimesChosen = (long) 0.0;

    @Relationship(type = "INFLUENCES")
    private List<AnswerAttribute> attributes = new ArrayList<>();

    public Answer() {this.code = IdGenerator.generate();}

    public Answer(String name) {
        this.text = name;
        this.code = IdGenerator.generate();
    }

    public List<AnswerAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AnswerAttribute> attributes) {
        this.attributes = attributes;
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

    public void incNumberOfTimesChosen()
    {
        numberOfTimesChosen++;
    }

    public Long getNumberOfTimesChosen()
    {
        return numberOfTimesChosen;
    }
}
