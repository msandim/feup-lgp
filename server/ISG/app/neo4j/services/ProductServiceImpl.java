package neo4j.services;

import neo4j.models.Product;

/**
 * Created by Lycantropus on 17-04-2016.
 */
public class ProductServiceImpl extends GenericService<Product> implements ProductService {

    @Override
    public Class<Product> getEntityType() {
        return Product.class;
    }
}
