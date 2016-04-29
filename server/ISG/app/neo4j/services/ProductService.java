package neo4j.services;

import neo4j.models.nodes.Product;
import neo4j.services.utils.GenericService;

/**
 * Created by Lycantropus on 17-04-2016.
 */
public class ProductService extends GenericService<Product>
{
    @Override
    public Class<Product> getEntityType() {
        return Product.class;
    }
}
