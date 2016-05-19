package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import neo4j.Neo4jSessionFactory;
import neo4j.models.edges.ProductAttribute;
import neo4j.models.nodes.Attribute;
import neo4j.models.nodes.Category;
import neo4j.models.nodes.Product;

import neo4j.services.*;
import play.libs.Json;

import neo4j.services.CategoryService;
import neo4j.services.ProductService;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;

import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.ControllerUtils;

import java.io.*;
import java.util.*;

/**
 * Created by Lycantropus on 17-04-2016.
 */
public class ProductController extends Controller {

    public Result retrieveAllProducts() {
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


    public Result retrieveProduct(Long id) {
        ProductService service = new ProductService();


        Product res = service.find(id);

        return ok(res.toString());
    }

    public Result createOrUpdateProduct(String name, String EAN, Float price, String categoryCode) {
        ProductService service = new ProductService();

        // TODO Falta append a categoria

        //Product temp = new Product (name, EAN, price, categoryCode);

        //return ok(service.createOrUpdate(temp).getName());
        return ok(Json.newObject());
    }

    public Result deleteProduct(Long id) {
        ProductService service = new ProductService();

        service.delete(id);

        return ok(Long.toString(id));
    }


    //@BodyParser.Of(BodyParser.FormUrlEncoded.class)
    public Result importFromCsv() {

        //getting the body and try to parse not files
        Map<String, String[]> result = request().body().asMultipartFormData().asFormUrlEncoded();


        // Get the category and verify if it exists
        CategoryService categoryService = new CategoryService();


        Map<String, String> errorMsg = new HashMap<>();

        if (result.get("code") == null) {
            errorMsg.put("error", "missing attribute keyword");
            errorMsg.put("msg", "There is no code: ");
            return badRequest(errorMsg.toString());
        }

        if (result.get("code")[0] == null) {
            errorMsg.put("error", "NO_CODE");
            errorMsg.put("msg", "There is no code in the request");
            return badRequest(errorMsg.toString());
        }
        String categoryCode = result.get("code")[0];
        //Logger.debug("primeiro: " + result.get("code")[0]);

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart csv = body.getFile("csv");

        //Category targetCategory =
        if (categoryService.findByCode(categoryCode) == null) {
            errorMsg.put("error", "INVALID_CODE");
            errorMsg.put("msg", "There is no category with this code: ");
            JsonNode node = ControllerUtils.missingField("code");
            return badRequest(node);
        }

        Category targetCategory = categoryService.findByCode(categoryCode);


        if (csv != null) {
            String fileName = csv.getFilename();
            String contentType = csv.getContentType();
            File file = (File) csv.getFile();


            BufferedReader br = null;
            String line = "";
            String cvsSplitBy = ";";
            String attributeTypeSplitChar = ":";
            AttributeService attributeService = new AttributeService();
            ProductService productService = new ProductService();
            Vector<String> attributeNames = new Vector<>();

            String query = new StringBuilder("MATCH (c:Category{name:\'" + targetCategory.getName() + "\'})-[]->(p:Product)-[]->(a:Attribute) detach delete p, a").toString();
            //Logger.debug("query:" + query);
            Neo4jSessionFactory.getInstance().getNeo4jSession().query(query, Collections.EMPTY_MAP);

            try {

                br = new BufferedReader(new FileReader(file));
                int linecounter = 0;
                while ((line = br.readLine()) != null) {
                    linecounter++;
                    // use comma as separator
                    String[] product = line.split(cvsSplitBy);
                    int tokencounter = 0;
                    if (linecounter == 1) {
                        for (String header : product) {
                            if (tokencounter != 0 && tokencounter != 3 && tokencounter != 4) {
                                String[] attInfo = header.split(attributeTypeSplitChar);
                                String attName = attInfo[0];

                                if (attInfo[1] == null) {
                                    return ok("missing attribute type: use numerical or categorical");
                                }
                                String attType = attInfo[1];
                                Attribute tmpAtt = new Attribute(attName);
                                tmpAtt.setType(attType);
                                attributeService.createOrUpdate(tmpAtt);
                                attributeNames.add(attName);
                                //Logger.debug("linha "+ linecounter+" |token= " + tokencounter+ " {cena] " + produto + "]");
                            }

                            tokencounter++;
                        }
                    } else {
                        Vector<String> values = new Vector<>();
                        Vector<String> attributeValues = new Vector<>();
                        for (String feature : product) {
                            if (tokencounter == 0 || tokencounter == 3 || tokencounter == 4) {
                                values.add(feature);
                                //Logger.debug("linha " + linecounter + " |token= " + tokencounter + " {cena] " + feature + "]");
                            } else {
                                attributeValues.add(feature);
                            }

                            tokencounter++;
                        }
                        String tmpPrice = values.get(2).replace(',', '.');
                        //Logger.debug("attnames: " + attributeNames.toString());
                        values.set(2, tmpPrice);

                        List<ProductAttribute> tempList = new ArrayList<>();

                        Product nodeProduct = new Product(values.get(1), values.get(0), Float.parseFloat(values.get(2)), targetCategory);
                        //Logger.debug("attribute names: " + attributeNames.size());
                        //Logger.debug("attribute values: " + attributeValues.size());
                        for (int i = 0; i < attributeNames.size(); i++) {
                            Attribute tempAttribute = attributeService.findByName(attributeNames.get(i));
                            if (attributeValues.get(i) != "NA" &&
                                    attributeValues.get(i) != " ") {
                                tempList.add(new ProductAttribute(nodeProduct, tempAttribute, attributeValues.get(i)));
                            }
                        }

                        nodeProduct.setAttributes(tempList);

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
            //Logger.debug("Done");
        } else {
            errorMsg.put("error", "FILE_NOT_FOUND");
            errorMsg.put("msg", "No file was found in your request!");
            return badRequest(errorMsg.toString());
        }
        //Logger.debug("antes");
        //Logger.debug("segundo: " + targetCategory.getName());
        return ok(Json.newObject());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result removeProducts(){


        JsonNode jsonRequest = request().body().asJson();

        //detect missing or null values
        if(jsonRequest.get("category") == null)
            return badRequest(ControllerUtils.missingField("category"));
        if(jsonRequest.get("products") == null)
            return badRequest(ControllerUtils.missingField("products"));

        // Service initialization
        CategoryService categoryService = new CategoryService();
        ProductService productService= new ProductService();
        AttributeService attrService = new AttributeService();

        // Get the category and verify if it exists
        String categoryCode = jsonRequest.findPath("category").asText();
        Category category = categoryService.findByCode(categoryCode);

        if (category == null){
            return badRequest(ControllerUtils.generalError("INVALID_CATEGORY","Category not found!"));
        }


        //parse questions
        JsonNode productsNode = jsonRequest.findPath("products");
        Iterator<JsonNode> itProducts = productsNode.elements();

        //if field is present but has no content
        if(!itProducts.hasNext()) {
            return badRequest(ControllerUtils.generalError("NO_PRODUCTS", "Products not found in the request!"));
        }

        //check for product that doesnt exist in the DB
        while (itProducts.hasNext()) {
            JsonNode node = itProducts.next();
            String productEAN = node.asText();
            Logger.info(productEAN);
            Product product = productService.findByEAN(productEAN);

            if (product == null) {
                return badRequest(ControllerUtils.generalError("INVALID_PRODUCTS", "One or more products specified for elimination do not exist!"));
            }

            Logger.info(product.getName());
        }


        //remove products
        itProducts=productsNode.elements();

        while (itProducts.hasNext()) {
            JsonNode node = itProducts.next();
            String productEAN = node.asText();
            Logger.info("to delete: " + productEAN);
            Product product = productService.findByEAN(productEAN);
            productService.delete(product.getId());
            Logger.info("deleted " + productEAN);
        }

        //TODO como �? � para apagar os attributos que ficam ligados a respostas? ou checko so se ficam tristes sos e abandonados?

        return ok(Json.newObject());
    }
}
