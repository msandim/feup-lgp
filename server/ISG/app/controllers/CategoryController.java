package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import neo4j.Neo4jSessionFactory;
import neo4j.models.nodes.Category;
import neo4j.services.AttributeService;
import neo4j.services.CategoryService;
//import org.neo4j.ogm.json.JSONObject;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import utils.ControllerUtils;


import play.Logger;

import java.util.*;

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

        // Return JSON with name and code:
        //ArrayNode jsonArray = Json.newArray();
        //categories.forEach(x -> jsonArray.add(Json.newObject().put("name", x.getName()).put("code", x.getCode())));

        return ok(Json.toJson(categories));
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
}
