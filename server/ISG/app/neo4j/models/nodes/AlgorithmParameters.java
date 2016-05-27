package neo4j.models.nodes;

import neo4j.models.Entity;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by Miguel on 06-05-2016.
 */
@NodeEntity
public class AlgorithmParameters extends Entity
{
    private Float alfa;
    private Float beta;
    private Float gamma;
    private Integer numberOfProducts;

    public AlgorithmParameters() { }

    public Float getAlfa()
    {
        return alfa;
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

    public void setAlfa(Float alfa)
    {
        this.alfa = alfa;
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
}
