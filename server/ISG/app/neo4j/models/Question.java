package neo4j.models;

/**
 * Created by Lycantropus on 14-04-2016.
 */

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import java.util.HashSet;
import java.util.Set;

@NodeEntity(label="question")
public class Question extends Entity{


    private String text;

    private String category;

    @Relationship(type = "seq", direction = "OUTGOING")
    public Set<Question> nextQuestions;
    @Relationship(type = "seq", direction = "INCOMING")
    public Set<Question> previousQuestions;


    public Question()
    {
        this.nextQuestions= new HashSet<>();
        this.previousQuestions=new HashSet<>();

    }

    public Question(String text, String category)
    {
        this();
        this.text=text;
        this.category=category;
    }

    @Override
    public String toString() {
        return "Question{" + "id=" + getId() + ", text=" + text +
                ", category=" + category +
                ", nextQuestionsSize=" + nextQuestions.size() +
                '}'+ '\n';
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Set<Question> getNextQuestions() {
        return nextQuestions;
    }

    public void setNextQuestions(Set<Question> nextQuestions) {
        this.nextQuestions = nextQuestions;
    }

    public Set<Question> getPreviousQuestions() {
        return previousQuestions;
    }

    public void setPreviousQuestions(Set<Question> previousQuestions) {
        this.previousQuestions = previousQuestions;
    }



}
