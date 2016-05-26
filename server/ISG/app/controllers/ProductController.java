package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import neo4j.models.edges.ProductAttribute;
import neo4j.models.nodes.Attribute;
import neo4j.models.nodes.Category;
import neo4j.models.nodes.Product;
import neo4j.services.AttributeService;
import neo4j.services.CategoryService;
import neo4j.services.ProductService;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.ControllerUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import com.opencsv.CSVReader;

/**
 * Created by Lycantropus on 17-04-2016.
 */
public class ProductController extends Controller
{
    public Result getProductsByCategory(String code)
    {
        ProductService productService = new ProductService();
        CategoryService categoryService = new CategoryService();

        if (categoryService.findByCode(code) == null)
            return badRequest(ControllerUtils.generalError("INVALID_CATEGORY", "Category not found!"));

        return ok(Json.toJson(productService.findByCategoryCode(code)));
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
        ProductService productService = new ProductService();

        if (result.get("code") == null || result.get("code")[0] == null)
            return badRequest(ControllerUtils.missingField("code"));

        String categoryCode = result.get("code")[0];

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart csv = body.getFile("csv");

        Category targetCategory = categoryService.findByCode(categoryCode);

        if (targetCategory == null)
            return badRequest(ControllerUtils.generalError("INVALID_CATEGORY", "Category not found!"));

        if (csv == null)
            return badRequest(ControllerUtils.generalError("FILE_NOT_FOUND", "No file was found in your request!"));

        //String fileName = csv.getFilename();
        //String contentType = csv.getContentType();
        File file = (File) csv.getFile();

        BufferedReader br = null;
        String[] csvLine;
        char cvsSplitBy = ';';
        String attributeTypeSplitChar = ":";

        AttributeService attributeService = new AttributeService();
        List<Attribute> attributes = new ArrayList<>();

        // Delete all products from this category:
        productService.deleteAllProductsByCategoryCode(categoryCode);

        try
        {
            CSVReader csvReader = new CSVReader(new FileReader(file), cvsSplitBy);
            int lineCounter = 0;
            while ((csvLine = csvReader.readNext()) != null)
            {
                lineCounter++;

                int attributeCounter = 0;

                // If it's the first line, list all the possible attributes:
                if (lineCounter == 1)
                {
                    for (String attributeName : csvLine)
                    {
                        // Attributes from 0 to 2 are product's features (ean, Name, Price):
                        if (attributeCounter != 0 && attributeCounter != 1 && attributeCounter != 2)
                        {
                            String[] attInfo = attributeName.split(attributeTypeSplitChar);
                            String attName = attInfo[0];

                            if (attInfo[1] == null)
                                return badRequest(ControllerUtils.generalError("MISSING_ATTRIBUTE_TYPE", "Attribute '" + attName + "' lacks his type ('categorical' or 'numeric')!"));

                            System.out.println("Atributo: " + attInfo[1]);

                            if (!attInfo[1].equals(Attribute.Type.CATEGORICAL) && !attInfo[1].equals(Attribute.Type.NUMERIC))
                                return badRequest(ControllerUtils.generalError("INVALID_ATTRIBUTE_TYPE", "Attribute '" + attName + "' must have a valid type: ('categorical' or 'numeric')!"));

                            String attType = attInfo[1];
                            Attribute attribute = new Attribute(attName, attType);
                            attributes.add(attributeService.createOrUpdate(attribute));
                        }
                        attributeCounter++;
                    }
                }
                else // if not, match the attributes to the products:
                {
                    List<String> productFeatures = new ArrayList<>();
                    List<String> attributeValues = new ArrayList<>();
                    for (String productAttribute : csvLine)
                    {
                        if (attributeCounter == 0 || attributeCounter == 1 || attributeCounter == 2)
                            productFeatures.add(productAttribute);
                        else
                            attributeValues.add(productAttribute);

                        attributeCounter++;
                    }

                    // Replace price separator from "," to ".":
                    productFeatures.set(2, productFeatures.get(2).replace(',', '.'));

                    List<ProductAttribute> productAttributes = new ArrayList<>();

                    // Create new product:
                    Product product = new Product(productFeatures.get(1), productFeatures.get(0), Float.parseFloat(productFeatures.get(2)), targetCategory);

                    // Link Attributes to Products:
                    for (int i = 0; i < attributes.size(); i++)
                    {
                        Attribute attribute = attributes.get(i);

                        // If attribute value is "NA" or empty space, then it has no value:
                        if (!Objects.equals(attributeValues.get(i), "NA") &&
                                !Objects.equals(attributeValues.get(i), " "))
                        {
                            productAttributes.add(new ProductAttribute(product, attribute, attributeValues.get(i)));
                        }
                    }

                    product.setAttributes(productAttributes);
                    productService.createOrUpdate(product);
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            if (br != null)
            {
                try
                {
                    br.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        return ok(Json.newObject());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result removeProducts()
    {

        JsonNode jsonRequest = request().body().asJson();

        //detect missing or null values
        if (jsonRequest.get("category") == null)
            return badRequest(ControllerUtils.missingField("category"));
        if (jsonRequest.get("products") == null)
            return badRequest(ControllerUtils.missingField("products"));

        // Service initialization
        CategoryService categoryService = new CategoryService();
        ProductService productService = new ProductService();

        // Get the category and verify if it exists
        String categoryCode = jsonRequest.findPath("category").asText();
        Category category = categoryService.findByCode(categoryCode);

        if (category == null)
            return badRequest(ControllerUtils.generalError("INVALID_CATEGORY", "Category not found!"));

        //parse questions
        JsonNode productsNode = jsonRequest.findPath("products");
        Iterator<JsonNode> itProducts = productsNode.elements();

        //check for product that doesnt exist in the DB
        while (itProducts.hasNext())
        {
            JsonNode node = itProducts.next();
            String productEan = node.asText();
            Product product = productService.findByEan(productEan);

            if (product == null)
                return badRequest(ControllerUtils.generalError("INVALID_PRODUCTS", "One or more products specified for elimination do not exist!"));

            productService.delete(product.getId());
        }

        return ok(Json.newObject());
    }
}
