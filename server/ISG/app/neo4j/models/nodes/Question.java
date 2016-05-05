package neo4j.models.nodes;

/**
 * Created by Lycantropus on 14-04-2016.
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import neo4j.models.Entity;
import neo4j.models.edges.QuestionEdge;
import org.neo4j.ogm.annotation.*;

import java.util.HashSet;
import java.util.Set;

@NodeEntity
public class Question extends Entity {
    private String text;

    @Relationship(type = "HAS")
    private Set<Answer> answers = new HashSet<>();

    @Relationship(type = "CONNECTS", direction = Relationship.OUTGOING)
    @JsonIgnore
    private Set<QuestionEdge> nextQuestions = new HashSet<>();

    //@Relationship(type = "CONNECTS", direction = Relationship.INCOMING)
    //private Set<QuestionEdge> previousQuestions;

    //@Relationship(type = "HAS_QUESTIONS", direction = Relationship.INCOMING)
    @JsonIgnore
    private Category category;

    public Question() {
    }

    public Question(String text, Category category) {
        this.text = text;
        this.category = category;
    }

    @Override
    public String toString() {
        return "Question{" + "id=" + getId() + ", text=" + text +
                ", category=" + category +
                ", answers_Size=" + answers.size() +
                '}' + '\n';
    }

    public Set<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(Set<Answer> answers) {
        this.answers = answers;
    }

    public Set<QuestionEdge> getNextQuestions() {
        return nextQuestions;
    }

    public void setNextQuestions(Set<QuestionEdge> nextQuestions) {
        this.nextQuestions = nextQuestions;
    }

    /*
    public Set<QuestionEdge> getPreviousQuestions() {
        return previousQuestions;
    }

    public void setPreviousQuestions(Set<QuestionEdge> previousQuestions) {
        this.previousQuestions = previousQuestions;
    }
    */

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
