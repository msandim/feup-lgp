package neo4j.models.edges;

import neo4j.models.Entity;
import neo4j.models.nodes.Question;
import org.neo4j.ogm.annotation.*;
import utils.Statistics;

import java.util.ArrayList;

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

    public Question getNextQuestion()
    {
        return nextQuestion;
    }

    public Float getVarianceGainMean()
    {
        return varianceGainMean;
    }

    // Source by: http://math.stackexchange.com/questions/106700/incremental-averageing
    public void incMeanVariance(Float newVarianceValue)
    {
        varianceGainMean = varianceGainMean + ((newVarianceValue - varianceGainMean) / numberOfTimesChosen);
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
