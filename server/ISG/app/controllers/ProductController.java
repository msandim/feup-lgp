package controllers;

import neo4j.models.nodes.Product;
import neo4j.services.ProductService;
import neo4j.services.ProductServiceImpl;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by Lycantropus on 17-04-2016.
 */
public class ProductController extends Controller {

    public Result retrieveAllProducts()
    {
        ProductService service = new ProductServiceImpl();


        Iterable<Product> res = service.findAll();


        return ok(res.toString());
    }

    public Result retrieveProduct(Long id)
    {
        ProductService service= new ProductServiceImpl();


        Product res = service.find(id);


        return ok(res.toString());
    }

    public Result createOrUpdateProduct(String productName, Float productPrice)
    {
        ProductService service = new ProductServiceImpl();


        Product temp = new Product (productName, productPrice);
        service.createOrUpdate(temp);


        return ok(service.createOrUpdate(temp).getName());
    }

    public Result deleteProduct(Long id)
    {
        ProductService service = new ProductServiceImpl();

        service.delete(id);


        return ok(Long.toString(id));
    }
}
