package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import neo4j.Neo4jSessionFactory;
import neo4j.models.edges.ProductAttribute;
import neo4j.models.nodes.Attribute;
import neo4j.models.nodes.Category;
import neo4j.models.nodes.Product;
import neo4j.services.AttributeService;
import neo4j.services.CategoryService;
import neo4j.services.ProductService;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.io.*;
import java.util.*;

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


    //@BodyParser.Of(BodyParser.FormUrlEncoded.class)
    public Result importFromCsv()
    {

        //getting the body and try to parse not files
        Map<String, String[]> result = request().body().asMultipartFormData().asFormUrlEncoded();


        // Get the category and verify if it exists
        CategoryService categoryService = new CategoryService();



        //Category category = categoryService.findByCode(catCode);return ok("deu");
        Map<String, String> errorMsg = new HashMap<>();

        if (result.get("code") == null) {
            errorMsg.put("Error", "missing attribute keyword");
            errorMsg.put("Message", "There is no code: ");
            return ok(errorMsg.toString());
        }

        if (result.get("code")[0] == null)
        {
            errorMsg.put("Error", "Invalid Category");
            errorMsg.put("Message", "There is no category with this code: ");
            return ok(errorMsg.toString());
        }
        String categoryCode = result.get("code")[0];
        Logger.debug("primeiro: "+ result.get("code")[0]);

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart csv = body.getFile("csv");

        //Category targetCategory =
        if(categoryService.findByCode(categoryCode)==null){
            errorMsg.put("Error", "Invalid Category");
            errorMsg.put("Message", "There is no category with this code: ");
            return ok(errorMsg.toString());
        }

        Category targetCategory =categoryService.findByCode(categoryCode);


        if (csv != null) {
            String fileName = csv.getFilename();
            String contentType = csv.getContentType();
            File file = (File) csv.getFile();


            BufferedReader br = null;
            String line = "";
            String cvsSplitBy = ";";
            AttributeService attributeService = new AttributeService();
            ProductService productService = new ProductService();
            Vector<String> attributeNames= new Vector<>();

            String query = new StringBuilder("MATCH (c:Category{name:\'" + targetCategory.getName()+ "\'})-[]->(p:Product)-[]->(a:Attribute) detach delete p, a").toString();
            Logger.debug("query:" + query);
            Neo4jSessionFactory.getInstance().getNeo4jSession().query(query, Collections.EMPTY_MAP);


            try {

                br = new BufferedReader(new FileReader(file));
                int linecounter=0;
                while ((line = br.readLine()) != null) {
                    linecounter++;
                    // use comma as separator
                    String[] product = line.split(cvsSplitBy);
                    int tokencounter =0;
                    if(linecounter==1)
                    {
                        for(String produto : product){
                            if(tokencounter!=0 && tokencounter!=3 && tokencounter!=4 ){
                                attributeService.createOrUpdate(new Attribute(produto));
                                attributeNames.add(produto);
                                //Logger.debug("linha "+ linecounter+" |token= " + tokencounter+ " {cena] " + produto + "]");
                            }

                            tokencounter++;
                        }
                    }
                    else
                    {
                        Vector<String> values= new Vector<>();
                        Vector<String> attributeValues = new Vector<>();
                        for(String feature : product){
                            if(tokencounter==0 || tokencounter==3 || tokencounter==4){
                                values.add(feature);
                                //Logger.debug("linha "+ linecounter+" |token= " + tokencounter+ " {cena] " + feature + "]");
                            }
                            else
                            {
                                attributeValues.add(feature);
                            }

                            tokencounter++;
                        }
                        String tmpPrice = values.get(2).replace(',', '.');

                        values.set(2, tmpPrice);

                        List<ProductAttribute> tempSet= new ArrayList<>();

                        Product nodeProduct = new Product(values.get(1), values.get(0), Float.parseFloat(values.get(2)), targetCategory);
                        //Logger.debug("attribute names: " + attributeNames.size());
                        //Logger.debug("attribute values: " + attributeValues.size());
                        for(int i = 0; i< attributeNames.size(); i++)
                        {
                            Attribute tempAttribute = attributeService.findByName(attributeNames.get(i));
                            if(attributeValues.get(i)!= "NA" &&
                                    attributeValues.get(i)!= " "){
                                tempSet.add(new ProductAttribute(nodeProduct, tempAttribute, attributeValues.get(i)));
                            }


                        }

                        nodeProduct.setAttributes(tempSet);

                        productService.createOrUpdate(nodeProduct);
                    }

                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            Logger.debug("Done");

        } else {
            return ok( "Missing file");
        }


        Logger.debug("antes");
        Logger.debug("segundo: " + targetCategory.getName());



        return ok();
    }
}
