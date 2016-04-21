package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.util.JSONPObject;
import neo4j.models.Category;
import neo4j.services.CategoryService;
import neo4j.services.CategoryServiceImpl;
//import org.neo4j.ogm.json.JSONObject;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
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

    public Result createOrUpdateCategory()
    {

        JsonNode json = request().body().asJson();
        if(json == null) {
            JsonNode obj =  Json.parse("{\"error\":\"INVALID NAME\", \"msg\":\"This category name already exists!\"}");
            return badRequest(obj);
        } else {
            String name = json.findPath("name").textValue();
            if(name == null) {
                return badRequest("Missing parameter [name]");
            } else {

                CategoryService service = new CategoryServiceImpl();
                Category temp = new Category (name);
                service.createOrUpdate(temp);
                return ok();

            }
        }
    }


    public Result deleteCategory(Long id)
    {
        CategoryService service = new CategoryServiceImpl();

        service.delete(id);


        return ok(Long.toString(id));
    }
}
