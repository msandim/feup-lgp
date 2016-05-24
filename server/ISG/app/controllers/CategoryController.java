package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import neo4j.Neo4jSessionFactory;
import neo4j.models.nodes.Category;
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


    public Result deleteCategory(Long id)
    {
        CategoryService service = new CategoryService();

        service.delete(id);


        return ok(Long.toString(id));
    }

    public Result removeCategory(String code){
        CategoryService service = new CategoryService();

        Category tmp = service.findByCode(code);



        if(tmp==null){
            //Logger.info("ta null");
            Map<String, String> errorMsg = new HashMap<>();
            errorMsg.put("error", "INVALID_NAME");
            errorMsg.put("msg", "there is no category with this name!");
            return badRequest(errorMsg.toString());
        }
        //Logger.info(tmp.getName());

        String query = new StringBuilder("MATCH (c:Category{name:\'" + tmp.getName() + "\'}) OPTIONAL MATCH (c)-[]->(q:Question)-[]->(a:Answer) OPTIONAL MATCH (c)-[]->(p:Product)-[]->(at:Attribute) detach delete c, q, a, p").toString();
        //Logger.debug("query:" + query);
        Neo4jSessionFactory.getInstance().getNeo4jSession().query(query, Collections.EMPTY_MAP);

        String purgeAttributesQuery= new StringBuilder("MATCH (at:Attribute) OPTIONAL MATCH (at)--(p:Product) WHERE p IS NULL delete at").toString();
        Neo4jSessionFactory.getInstance().getNeo4jSession().query(purgeAttributesQuery, Collections.EMPTY_MAP);
        return ok(Json.newObject());
    }
}
