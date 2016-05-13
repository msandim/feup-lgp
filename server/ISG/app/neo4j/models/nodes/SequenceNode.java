package neo4j.models.nodes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import neo4j.models.Entity;
import org.neo4j.ogm.annotation.Relationship;

/**
 * Created by Miguel on 09-05-2016.
 */
public class SequenceNode extends Entity
{
    @Relationship(type="NEXT_NODE", direction = Relationship.OUTGOING)
    @JsonIgnore
    private SequenceNode nextNode;

    @Relationship(type="NEXT_NODE", direction = Relationship.INCOMING)
    @JsonIgnore
    private SequenceNode previousNode;

    private Question question;
    private Answer answer;

    public SequenceNode() { }

    public SequenceNode(Question question, Answer answer)
    {
        this.question = question;
        this.answer = answer;
    }

    public void setNextNode(SequenceNode nextNode)
    {
        this.nextNode = nextNode;
    }

    public SequenceNode getNextNode()
    {
        return nextNode;
    }

    public Question getQuestion()
    {
        return question;
    }

    public Answer getAnswer()
    {
        return answer;
    }
}
