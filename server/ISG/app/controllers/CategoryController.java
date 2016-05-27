package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import neo4j.models.nodes.Category;
import neo4j.services.CategoryService;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.ControllerUtils;

import java.util.ArrayList;
import java.util.List;

//import org.neo4j.ogm.json.JSONObject;

/**
 * Created by Lycantropus on 16-04-2016.
 */
public class CategoryController extends Controller {

    public Result retrieveAllCategories()
    {
        // Gather all the categories:
        CategoryService service = new CategoryService();
        List<Category> categories = new ArrayList<>();
        service.findAll().forEach(categories::add);

        return ok(Json.toJson(categories));
    }

    public Result createCategory(String name, String code)
    {
        CategoryService categoryService = new CategoryService();

        if (categoryService.findByCode(code) != null)
            return badRequest(ControllerUtils.generalError("INVALID_CODE", "This category code already exists!"));

        // Save the category:
        Category temp = new Category(name, code);
        categoryService.createOrUpdate(temp);

        return ok(Json.newObject());
    }

    public Result removeCategory(String code) {
        CategoryService categoryService = new CategoryService();

        Category category = categoryService.findByCode(code);

        if (category == null)
            return badRequest(ControllerUtils.generalError("INVALID_CATEGORY","Category not found!"));

        categoryService.deleteByCode(category.getCode());

        return ok(Json.newObject());
    }

    public Result createOrUpdateCategory()
    {
        /*CategoryService service = new CategoryService();
        Category temp = new Category(name, code);
        service.createOrUpdate(temp);
        //return ok("Ok");*/


        JsonNode json = request().body().asJson();



        if(json == null) {
            JsonNode obj =  Json.parse("{\"error\":\"INVALID NAME\", \"msg\":\"This category name already exists!\"}");
            return badRequest(obj);
        } else {
            String categoryName = json.findPath("name").asText();
            Logger.info("e null?" + categoryName);
            String categoryCode = json.findPath("code").asText();
            Logger.info(categoryName);
            if(categoryName == null) {
                return badRequest("Missing parameter [name]");

            }
            else if(categoryCode == null){
                return badRequest("Missing parameter [code]");
            }
            else{

                CategoryService service = new CategoryService();
                Category temp = new Category(categoryName, categoryCode);

                if (service.findByCode(temp.getCode()) != null)
                    return badRequest(ControllerUtils.generalError("INVALID_CODE", "This category code already exists!"));

                service.createOrUpdate(temp);
                return ok(Json.newObject());

            }
        }


    }
}
