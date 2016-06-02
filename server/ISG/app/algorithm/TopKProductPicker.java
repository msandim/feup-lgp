package algorithm;

import controllers.AlgorithmConfigController;
import neo4j.models.nodes.Product;
import neo4j.services.AlgorithmParametersService;
import utils.MapUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Miguel on 27-05-2016.
 */
public class TopKProductPicker
{
    public static List<Map.Entry<Product, Float>> getTopProducts(Map<Product, Float> productScores)
    {
        AlgorithmParametersService service = new AlgorithmParametersService();

        // Order in decreasing score order and return the first k:
        return MapUtils.orderByValueDecreasing(productScores).stream()
            .limit(service.getAlgorithmParameters().getNumberOfProducts())
            .collect(Collectors.toList());
    }
}
