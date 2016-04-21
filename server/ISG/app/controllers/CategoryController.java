package controllers;

import neo4j.models.Category;
import neo4j.services.CategoryService;
import neo4j.services.CategoryServiceImpl;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by Lycantropus on 16-04-2016.
 */
public class CategoryController extends Controller {

    public Result retrieveAllCategories()
    {
        CategoryService service = new CategoryServiceImpl();


        Iterable<Category> res = service.findAll();

        return ok(res.toString());
    }

    public Result retrieveCategory(Long id)
    {
        CategoryService service = new CategoryServiceImpl();


        Category res = service.find(id);


        return ok(res.toString());
    }

    public Result createOrUpdateCategory(String categoryName)
    {
        CategoryService service = new CategoryServiceImpl();

        Category temp = new Category (categoryName);
        service.createOrUpdate(temp);


        return ok(service.createOrUpdate(temp).getName());
    }

    public Result deleteCategory(Long id)
    {
        CategoryService service = new CategoryServiceImpl();

        service.delete(id);


        return ok(Long.toString(id));
    }
}
