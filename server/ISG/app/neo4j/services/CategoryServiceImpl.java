package neo4j.services;

import neo4j.models.nodes.Category;

/**
 * Created by Lycantropus on 16-04-2016.
 */
public class CategoryServiceImpl extends GenericService<Category> implements CategoryService
{

    @Override
    public Class<Category> getEntityType() {
        return Category.class;
    }
}
