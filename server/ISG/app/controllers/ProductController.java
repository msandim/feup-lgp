package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import neo4j.models.nodes.Category;
import neo4j.models.nodes.Product;
import neo4j.services.CategoryService;
import neo4j.services.ProductService;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.io.File;

/**
 * Created by Lycantropus on 17-04-2016.
 */
public class ProductController extends Controller {

    public Result retrieveAllProducts()
    {
        ProductService service = new ProductService();

        Iterable<Product> res = service.findAll();

        return ok(res.toString());
    }

    public Result retrieveProduct(Long id)
    {
        ProductService service= new ProductService();

        Product res = service.find(id);

        return ok(res.toString());
    }

    public Result createOrUpdateProduct(String name, String EAN, Float price, String categoryCode)
    {
        ProductService service = new ProductService();

        // TODO Falta append a categoria

        //Product temp = new Product (name, EAN, price, categoryCode);

        //return ok(service.createOrUpdate(temp).getName());
        return ok();
    }

    public Result deleteProduct(Long id)
    {
        ProductService service = new ProductService();

        service.delete(id);

        return ok(Long.toString(id));
    }


    @BodyParser.Of(BodyParser.MultipartFormData.class)
    public Result importFromCsv()
    {

        //return json message
        Http.MultipartFormData result = request().body().asMultipartFormData();
        Http.MultipartFormData.Part category = result.getFile("csv");


        // Get the category and verify if it exists
        CategoryService categoryService = new CategoryService();

        //JsonNode jsonRequest = request().body().asJson();




        /*System.out.print(category);

        //Category category = categoryService.findByCode(catCode);
        Result resultado = request().body().asJson(;
        if (category == null) {
            result.put("Error", "Invalid Category");
            result.put("Message", "There is no category with this code: " + catCode);
            return ok(result);
        }*/

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart csv = body.getFile("csv");

        
        if (csv != null) {
            String fileName = csv.getFilename();
            String contentType = csv.getContentType();
            File file = (File) csv.getFile();
            return ok("File uploaded");
        } else {
            return ok( "Missing file");
        }
    }
}
