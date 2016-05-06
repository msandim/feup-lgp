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
}
