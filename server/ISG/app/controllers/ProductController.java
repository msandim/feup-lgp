package controllers;

import neo4j.models.nodes.Product;
import neo4j.services.CategoryService;
import neo4j.services.ProductService;
import neo4j.services.QuestionService;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.ControllerUtils;

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

    public Result getProductsByCategory(String code)
    {
        ProductService productService = new ProductService();
        CategoryService categoryService = new CategoryService();

        if (categoryService.findByCode(code) == null)
            return badRequest(ControllerUtils.generalError("INVALID_CATEGORY", "Category not found!"));

        return ok(Json.toJson(productService.findByCategoryCode(code)));
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
}
