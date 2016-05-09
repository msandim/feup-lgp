package neo4j.models.nodes;

import neo4j.models.Entity;

/**
 * Created by Miguel on 09-05-2016.
 */
public class SequenceNode extends Entity
{
    private SequenceNode nextNode;
    private String text;
    private Question question;
    private Answer answer;

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

    public String getText()
    {
        return text;
    }
}
