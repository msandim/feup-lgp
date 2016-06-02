package neo4j.models.nodes;

import neo4j.models.Entity;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by Miguel on 06-05-2016.
 */
@NodeEntity
public class AlgorithmParameters extends Entity
{
    private Float alpha;
    private Float beta;
    private Float gamma;
    private Integer numberOfProducts;
    private Integer numberOfQuestions;

    public AlgorithmParameters() { }

    public Float getAlpha()
    {
        return alpha;
    }

    public Float getBeta()
    {
        return beta;
    }

    public Float getGamma()
    {
        return gamma;
    }

    public Integer getNumberOfProducts()
    {
        return numberOfProducts;
    }

    public Integer getNumberOfQuestions()
    {
        return numberOfQuestions;
    }

    public void setAlpha(Float alpha)
    {
        this.alpha = alpha;
    }

    public void setBeta(Float beta)
    {
        this.beta = beta;
    }

    public void setGamma(Float gamma)
    {
        this.gamma = gamma;
    }

    public void setNumberOfProducts(Integer numberOfProducts)
    {
        this.numberOfProducts = numberOfProducts;
    }

    public void setNumberOfQuestions(Integer numberOfQuestions)
    {
        this.numberOfQuestions = numberOfQuestions;
    }
}
