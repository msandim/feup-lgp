package controllers;

import neo4j.models.nodes.Product;
import neo4j.services.ProductService;
import play.mvc.Controller;
import play.mvc.Result;

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
