package neo4j.models.edges;

import neo4j.models.Entity;
import neo4j.models.nodes.Question;
import org.neo4j.ogm.annotation.*;

/**
 * Created by Miguel on 27-04-2016.
 */
@RelationshipEntity(type = "CONNECTS")
public class QuestionEdge extends Entity
{
    @StartNode
    private Question previousQuestion;

    @EndNode
    private Question nextQuestion;

    private Float varianceGainMean = (float) 0.0;
    private Long numberOfTimesChosen = (long) 0;
    private Long numberOfTimesGoodFeedback = (long) 0;

    public QuestionEdge() {

    }

    public QuestionEdge(Question in, Question out) {
        this.previousQuestion = in;
        this.nextQuestion = out;
    }

    public Question getNextQuestion()
    {
        return nextQuestion;
    }

    public void setNextQuestion(Question nextQuestion) {
        this.nextQuestion = nextQuestion;
    }

    public Question getPreviousQuestion() {
        return previousQuestion;
    }

    public void setPreviousQuestion(Question previousQuestion) {
        this.previousQuestion = previousQuestion;
    }

    public Float getVarianceGainMean()

    {
        return varianceGainMean;
    }

    public void incNumberOfTimesChosen()
    {
        numberOfTimesChosen++;
    }

    public Long getNumberOfTimesChosen()
    {
        return numberOfTimesChosen;
    }

    public void incNumberOfTimesGoodFeedback()
    {
        numberOfTimesGoodFeedback++;
    }

    public Long getNumberOfTimesGoodFeedback()
    {
        return numberOfTimesGoodFeedback;
    }

    private String getCode()
    {
        return previousQuestion.getCode() + "-" + nextQuestion.getCode();
    }

    @Override
    public int hashCode()
    {
        return getCode().hashCode();
    }

    // Two Questions are the same if they have the same code:
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof QuestionEdge)
        {
            QuestionEdge qe = (QuestionEdge) obj;
            return (qe.getCode().equals(this.getCode()));
        } else
        {
            return false;
        }
    }
}
