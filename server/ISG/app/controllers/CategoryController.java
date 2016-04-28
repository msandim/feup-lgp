package controllers;

import neo4j.models.nodes.Category;
import neo4j.services.CategoryService;
//import org.neo4j.ogm.json.JSONObject;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by Lycantropus on 16-04-2016.
 */
public class CategoryController extends Controller {

    public Result retrieveAllCategories()
    {
        CategoryService service = new CategoryService();

        Iterable<Category> res = service.findAll();

        return ok(res.toString());
    }

    public Result retrieveCategory(Long id)
    {
        CategoryService service = new CategoryService();

        Category res = service.find(id);

        return ok(res.toString());
    }

    /*
    public Result retrieveCategoryByCodename(String codename)
    {
        CategoryService service = new CategoryService();

        Category res = service.findByCodename(codename);

        return ok(res.toString());
    }*/

    public Result createOrUpdateCategory(String name, String codename)
    {
        CategoryService service = new CategoryService();
        Category temp = new Category(name, codename);
        service.createOrUpdate(temp);
        return ok("Ok");

        // TODO Falta se already exists

        /*
        JsonNode json = request().body().asJson();
        if(json == null) {
            JsonNode obj =  Json.parse("{\"error\":\"INVALID NAME\", \"msg\":\"This category name already exists!\"}");
            return badRequest(obj);
        } else {
            String name = json.findPath("name").textValue();
            if(name == null) {
                return badRequest("Missing parameter [name]");
            } else {

                CategoryService service = new CategoryService();
                Category temp = new Category(name);
                service.createOrUpdate(temp);
                return ok();

            }
        }
        */
    }


    public Result deleteCategory(Long id)
    {
        CategoryService service = new CategoryService();

        service.delete(id);


        return ok(Long.toString(id));
    }
}
