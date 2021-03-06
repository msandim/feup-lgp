package neo4j.models.nodes;

/**
 * Created by Lycantropus on 14-04-2016.
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.node.ObjectNode;
import neo4j.models.Entity;
import neo4j.models.edges.QuestionEdge;
import org.neo4j.ogm.annotation.*;
import play.libs.Json;
import utils.IdGenerator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NodeEntity
public class Question extends Entity
{
    private String code;
    private String text;

    @JsonIgnore
    private Long numberOfTimesChosen = (long) 0;

    @Relationship(type = "HAS")
    private List<Answer> answers = new ArrayList<>();

    @Relationship(type = "CONNECTS", direction = Relationship.OUTGOING)
    @JsonIgnore
    private List<QuestionEdge> nextQuestions = new ArrayList<>();

    @Relationship(type = "HAS_QUESTIONS", direction = Relationship.INCOMING)
    @JsonIgnore
    private Category category;

    //@Relationship(type = "CONNECTS", direction = Relationship.INCOMING)
    //private Set<QuestionEdge> previousQuestions;

    public Question() {}

    public Question(String text) {
        this.text = text;
        this.code = IdGenerator.generate();
    }

    @Override
    public String toString() {
        return code;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public List<QuestionEdge> getNextQuestions() {
        return nextQuestions;
    }

    public void setNextQuestions(List<QuestionEdge> nextQuestions) {
        this.nextQuestions = nextQuestions;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public void incNumberOfTimesChosen()
    {
        numberOfTimesChosen++;
    }

    public Long getNumberOfTimesChosen()
    {
        return numberOfTimesChosen;
    }

    public ObjectNode toJson() {
        ObjectNode node = Json.newObject();
        node.put("code", code);
        node.put("text", text);

        List<ObjectNode> answerNodes = new ArrayList<>();
        answers.forEach(x -> answerNodes.add(x.toJson()));
        node.set("answers", Json.toJson(answerNodes));

        return node;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    // Hashcode of each Question is the code's hashcode:
    @Override
    public int hashCode()
    {
        return this.code.hashCode();
    }

    // Two Questions are the same if they have the same code:
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Question)
        {
            Question q = (Question) obj;
            return (q.code.equals(this.code));
        } else
        {
            return false;
        }
    }
}
