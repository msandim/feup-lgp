package neo4j.services;

import neo4j.models.nodes.AlgorithmParameters;
import neo4j.services.utils.GenericService;

import java.util.Collection;

/**
 * Created by Miguel on 06-05-2016.
 */
public class AlgorithmParametersService extends GenericService<AlgorithmParameters>
{
    @Override
    public Class<AlgorithmParameters> getEntityType() {
        return AlgorithmParameters.class;
    }

    public AlgorithmParameters getAlgorithmParameters()
    {
        Collection<AlgorithmParameters> parameters = findAll();

        if (parameters.iterator().hasNext())
            return parameters.iterator().next();
        else
            return null;
    }
}
