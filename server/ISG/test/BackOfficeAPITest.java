import com.fasterxml.jackson.databind.JsonNode;
import neo4j.Neo4jSessionFactory;
import org.junit.*;

import play.libs.Json;
import play.libs.ws.WSResponse;
import play.mvc.Http;
import play.mvc.Result;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;

import static play.test.Helpers.*;

import static org.junit.Assert.assertEquals;

public class BackOfficeAPITest extends APITest {

    //TODO add tests for missing arguments, wrong argument names, wrong number of parameters, etc
    //TODO maybe change to not use other APIs and insert directly to the database

    @Test
    public void testAddCategory() throws Exception {
        //Adding a category. Should return empty JSON object
        response = request("api/addCategory", "POST", null, readJsonFromFile("addCategory/parameters.json"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("addCategory/response.json"), response.asJson());

        //Checking if the category was really added
        response = request("api/allCategories", "GET", null, null);

        assert response != null;

        JsonNode categories = response.asJson().elements().next();

        assertEquals(OK, response.getStatus());
        assertEquals("Televisoes", categories.get("name").asText());
        assertEquals("TVs", categories.get("code").asText());
    }

    @Test
    public void testAddCategoryBadName() throws Exception {
        //Adding a category. Should return empty JSON object
        response = request("api/addCategory", "POST", null,readJsonFromFile("addCategory/parameters.json"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("addCategory/response.json"), response.asJson());

        //Trying to add another category with the same name. It should return an error.
        response = request("api/addCategory", "POST", null, readJsonFromFile("addCategory/parameters.json"));

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addCategory/responseBadName.json"), response.asJson());
    }

    @Test
    public void testAddProducts() throws Exception {
        request("api/addCategory", "POST", null, readJsonFromFile("addCategory/parameters.json"));

        //Adding products. Should return empty JSON object
        response = requestFile("api/addProducts", "POST", new File("addProducts/tv.csv"), readJsonFromFile("addProducts/parameters.json"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/response.json"), response.asJson());
    }

    @Test @Ignore
    public void testAddProductsBadCategory() throws Exception {
        response = request("api/addProducts", "POST", null /* TODO use file */, readJsonFromFile("addProducts/parameters.json"));

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/responseBadCategory.json"), response.asJson());
    }

    @Test @Ignore
    public void testAddProductsBadFile() throws Exception {
        request("api/addCategory", "POST", null, readJsonFromFile("addCategory/parameters.json"));

        response = request("api/addProducts", "POST", null /* TODO use file */, readJsonFromFile("addProducts/parameters.json"));

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/responseBadFile.json"), response.asJson());
    }

    @Test
    public void testAddQuestions() throws Exception {
        request("api/addCategory", "POST", null, Json.newObject()
                .put("name", "Televisoes")
                .put("code", "tvs")
        );

        Neo4jSessionFactory.getInstance().getNeo4jSession().query("CREATE (at1: Attribute {name: 'width (cm)', type: 'numeric'});", Collections.EMPTY_MAP);
        Neo4jSessionFactory.getInstance().getNeo4jSession().query("CREATE (at2: Attribute {name: 'resolution', type: 'categorical'});", Collections.EMPTY_MAP);

        //Adding a question. Should return empty JSON object
        response = request("api/addQuestions", "POST", readJsonFromFile("addQuestions/body.json"), null);

        System.out.println(response.getBody());

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/response.json"), response.asJson());

        //TODO check if Questions were added
    }

    @Test
    public void testAddQuestionsBadCategory() throws Exception {
        request("api/addCategory", "POST", null, Json.newObject()
                .put("name", "Televisoes")
                .put("code", "tvs")
        );

        //Adding a question. Should return empty JSON object
        response = request("api/addQuestions", "POST", readJsonFromFile("addQuestions/bodyBadCategory.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/responseBadCategory.json"), response.asJson());

        //TODO check if Questions were not added
    }

    @Test @Ignore
    public void testAddQuestionsBadQuestions() throws Exception {

    }

    @Test @Ignore
    public void testConfigAlgorithm() throws Exception {

    }

    @Test @Ignore
    public void testConfigAlgorithmBadNumber() throws Exception {

    }

    @Test @Ignore
    public void testRemoveCategory() throws Exception {

    }

    @Test @Ignore
    public void testRemoveCategoryError() throws Exception {

    }

    @Test @Ignore
    public void testRemoveProducts() throws Exception {

    }

    @Test @Ignore
    public void testRemoveQuestions() throws Exception {

    }

    @Test
    public void testGetAllCategories() throws Exception {
        request("api/addCategory", "POST", null, Json.newObject()
                .put("name", "Televisoes")
                .put("code", "tv")
        );

        request("api/addCategory", "POST", null, Json.newObject()
                .put("name", "Máquinas de Lavar")
                .put("code", "maqla")
        );

        response = request("api/allCategories", "GET", null, null);

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("getAllCategories/response.json"), response.asJson());
    }

    @Test @Ignore
    public void testGetProductsByCategory() throws Exception {

    }

    @Test @Ignore
    public void testGetProductsByCategoryBadCategory() throws Exception {

    }

    @Test @Ignore
    public void testGetQuestionsByCategory() throws Exception {

    }

    @Test @Ignore
    public void testGetQuestionsByCategoryBadCategory() throws Exception {

    }

    @Test
    public void testRequest() throws Exception {

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/api/allCategories");

        Result result = route(request);
        assertEquals(OK, result.status());

        WSResponse response = request("api/allCategories", "GET", null, null);

        assert response != null;
        assertEquals(OK, response.getStatus());
    }
}
