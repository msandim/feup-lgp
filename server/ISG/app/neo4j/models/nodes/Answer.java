package neo4j.models.nodes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.node.ObjectNode;
import neo4j.models.Entity;
import neo4j.models.edges.AnswerAttribute;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import play.libs.Json;

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

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
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

    public ObjectNode toJson()
    {
        return Json.newObject().put("code", code).put("text", text);
    }
}
