package neo4j.models.nodes;

/**
 * Created by Lycantropus on 14-04-2016.
 */

import neo4j.models.edges.QuestionEdge;
import org.neo4j.ogm.annotation.*;

import java.util.Set;

@NodeEntity
public class Question extends Entity
{
    private String text;

    @Relationship(type = "HAS")
    private Set<Answer> answers;

    @Relationship(type = "FOLLOWS", direction = Relationship.OUTGOING)
    private Set<QuestionEdge> nextQuestions;

    @Relationship(type = "FOLLOWS", direction = Relationship.OUTGOING)
    private Set<QuestionEdge> previousQuestions;


    public Question()
    {
        //this.nextQuestions= new HashSet<>();
        //this.previousQuestions=new HashSet<>();

    }

    public Question(String text, String category)
    {
        this();
        this.text=text;
        //this.category=category;
    }

    @Override
    public String toString() {
        return "Question{" + "id=" + getId() + ", text=" + text +
                //", category=" + category +
                //", nextQuestionsSize=" + nextQuestions.size() +
                '}'+ '\n';
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /*public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }*/

    /*public Set<Question> getNextQuestions() {
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
    */



}
