import com.fasterxml.jackson.databind.JsonNode;
import neo4j.Neo4jSessionFactory;
import org.junit.Ignore;
import org.junit.Test;
import play.libs.Json;
import play.libs.ws.WSResponse;
import play.mvc.Http;
import play.mvc.Result;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static play.test.Helpers.*;

@SuppressWarnings("Duplicates")
public class BackOfficeAPITest extends APITest {

    private WSResponse response;

    //TODO add tests for missing arguments, wrong argument names, wrong number of parameters, etc
    //TODO maybe change to not use other APIs and insert directly to the database


    //==============================================================//
    //==============================================================//
    //========================Category==============================//
    //==============================================================//
    //==============================================================//

    //==========================Add=================================//

    @Test
    public void testAddCategory() throws Exception {
        //Adding a category. Should return empty JSON object
        response = request("api/addCategory", POST, readJsonFromFile("addCategory/parameters.json"), null);

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("addCategory/response.json"), response.asJson());

        //Checking if the category was really added
        response = request("api/allCategories", GET, null, null);

        assert response != null;

        JsonNode categories = response.asJson().elements().next();

        assertEquals(OK, response.getStatus());
        assertEquals("Televisoes", categories.get("name").asText());
        assertEquals("TVs", categories.get("code").asText());
    }

    @Test
    public void testAddCategoryBadName() throws Exception {
        //Adding a category. Should return empty JSON object
        response = request("api/addCategory", POST, readJsonFromFile("addCategory/parameters.json"), null);

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("addCategory/response.json"), response.asJson());

        //Trying to add another category with the same name. It should return an error.
        response = request("api/addCategory", POST, readJsonFromFile("addCategory/parameters.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addCategory/responseBadName.json"), response.asJson());
    }

    @Test
    public void testAddCategoryMissingField() throws Exception {
        response = request("api/addCategory", POST, readJsonFromFile("addCategory/parametersMissingFieldCode.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addCategory/responseMissingFieldCode.json"), response.asJson());

        response = request("api/addCategory", POST, readJsonFromFile("addCategory/parametersMissingFieldName.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addCategory/responseMissingFieldName.json"), response.asJson());
    }

    //==========================All=================================//

    @Test
    public void testGetAllCategories() throws Exception {
        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tv"), null);

        request("api/addCategory", POST, Json.newObject().put("name", "Maquinas de Lavar").put("code", "maqla"), null);

        response = request("api/allCategories", GET, null, null);

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("getAllCategories/response.json"), response.asJson());
    }

    //========================Removing==============================//

    //TODO check if category was added and then isn't after removing it

    @Test
    public void testRemoveCategory() throws Exception {
        response = request("api/addCategory", POST, readJsonFromFile("addCategory/parameters.json"), null);

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("addCategory/response.json"), response.asJson());

        response = request("api/removeCategory", DELETE, null, readJsonFromFile("removeCategory/parameters.json"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("removeCategory/response.json"), response.asJson());
    }

    @Test
    public void testRemoveCategoryError() throws Exception {
        response = request("api/addCategory", POST, readJsonFromFile("addCategory/parameters.json"), null);

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("addCategory/response.json"), response.asJson());

        response = request("api/removeCategory", DELETE, null, readJsonFromFile("removeCategory/parametersWrongCode.json"));

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("removeCategory/responseWrongCode.json"), response.asJson());
    }

    //==============================================================//
    //==============================================================//
    //========================Questions=============================//
    //==============================================================//
    //==============================================================//

    //==========================Add=================================//

    @Test
    public void testAddQuestions() throws Exception {
        Neo4jSessionFactory.getInstance().getNeo4jSession().query("CREATE (at1: Attribute {name: 'width (cm)', type: 'numeric'});", Collections.EMPTY_MAP);
        Neo4jSessionFactory.getInstance().getNeo4jSession().query("CREATE (at2: Attribute {name: 'resolution', type: 'categorical'});", Collections.EMPTY_MAP);

        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        //Adding a question. Should return empty JSON object
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/body.json"), null);

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/response.json"), response.asJson());

        //Checking if Questions were added
        response = request("api/questionsByCategory", GET, null, Json.newObject().put("code", "tvs"));

        assert response != null;

        JsonNode questions = response.asJson().elements().next();
        List<String> text =  questions.findValuesAsText("text");

        assertEquals(OK, response.getStatus());
        assertEquals("Qual o tamanho da sua sala?", questions.get("text").asText());
        assertNotEquals(text.indexOf("Pequena"), -1);
        assertNotEquals(text.indexOf("Media"), -1);
        assertNotEquals(text.indexOf("Grande"), -1);

        //TODO Can be verified with more values
    }

    @Test
    public void testAddQuestionsInvalidCategory() throws Exception {
        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        //Adding a question. Should return the wrong category error
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/bodyInvalidCategory.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/responseInvalidCategory.json"), response.asJson());

        //Checking if Questions were not added
        response = request("api/questionsByCategory", GET, null, Json.newObject().put("code", "tvs"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals("[]", response.getBody());
    }

    @Test
    @Ignore
    public void testAddQuestionsNoQuestions() throws Exception {
        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        //NO_QUESTIONS
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/bodyInvalidCategory.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/responseInvalidCategory.json"), response.asJson());

        //Checking if Questions were not added
        response = request("api/questionsByCategory", GET, null, Json.newObject().put("code", "tvs"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals("[]", response.getBody());
    }

    @Test
    @Ignore
    public void testAddQuestionsNoAnswers() throws Exception {
        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        //NO_ANSWERS
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/bodyInvalidCategory.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/responseInvalidCategory.json"), response.asJson());

        //Checking if Questions were not added
        response = request("api/questionsByCategory", GET, null, Json.newObject().put("code", "tvs"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals("[]", response.getBody());
    }

    @Test
    @Ignore
    public void testAddQuestionsNoAttributes() throws Exception {
        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        //NO_ATTRIBUTES
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/bodyInvalidCategory.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/responseInvalidCategory.json"), response.asJson());

        //Checking if Questions were not added
        response = request("api/questionsByCategory", GET, null, Json.newObject().put("code", "tvs"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals("[]", response.getBody());
    }

    @Test
    @Ignore
    public void testAddQuestionsInvalidAttributes() throws Exception {
        //INVALID_ATTRIBUTE
        //INVALID_OPERATOR
        //INVALID_ATTRIBUTE_OPERATOR_RELATION
        //INVALID_VALUE
        //INVALID_SCORE

        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        //INVALID_ATTRIBUTE
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/bodyInvalidCategory.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/responseInvalidCategory.json"), response.asJson());

        //INVALID_OPERATOR
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/bodyInvalidCategory.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/responseInvalidCategory.json"), response.asJson());

        //INVALID_ATTRIBUTE_OPERATOR_RELATION
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/bodyInvalidCategory.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/responseInvalidCategory.json"), response.asJson());

        //INVALID_VALUE
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/bodyInvalidCategory.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/responseInvalidCategory.json"), response.asJson());

        //INVALID_SCORE
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/bodyInvalidCategory.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/responseInvalidCategory.json"), response.asJson());

        //Checking if Questions were not added
        response = request("api/questionsByCategory", GET, null, Json.newObject().put("code", "tvs"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals("[]", response.getBody());
    }

    @Test
    @Ignore
    public void testAddQuestionsMissingFields() throws Exception {
        //category
        //questions
        //answers
        //attributes
        //attribute name
        //attribute operator
        //attribute value
        //attribute score
        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        //category
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/bodyInvalidCategory.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/responseInvalidCategory.json"), response.asJson());

        //questions
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/bodyInvalidCategory.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/responseInvalidCategory.json"), response.asJson());

        //answers
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/bodyInvalidCategory.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/responseInvalidCategory.json"), response.asJson());

        //attributes
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/bodyInvalidCategory.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/responseInvalidCategory.json"), response.asJson());

        //attribute name
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/bodyInvalidCategory.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/responseInvalidCategory.json"), response.asJson());

        //attribute operator
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/bodyInvalidCategory.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/responseInvalidCategory.json"), response.asJson());

        //attribute value
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/bodyInvalidCategory.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/responseInvalidCategory.json"), response.asJson());

        //attribute score
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/bodyInvalidCategory.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/responseInvalidCategory.json"), response.asJson());

        //Checking if Questions were not added
        response = request("api/questionsByCategory", GET, null, Json.newObject().put("code", "tvs"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals("[]", response.getBody());
    }

    //==========================All=================================//

    @Test
    @Ignore
    public void testGetQuestionsByCategory() throws Exception {

    }

    @Test
    @Ignore
    public void testGetQuestionsByCategoryBadCategory() throws Exception {

    }

    //========================Removing==============================//

    @Test
    @Ignore
    public void testRemoveQuestions() throws Exception {

    }

    //==============================================================//
    //==============================================================//
    //========================Products==============================//
    //==============================================================//
    //==============================================================//

    //==========================Add=================================//

    @Test
    @Ignore
    public void testAddProducts() throws Exception {
        request("api/addCategory", POST, null, readJsonFromFile("addCategory/parameters.json"));

        //Adding products. Should return empty JSON object
        //response = requestFile("api/addProducts", POST, new File("addProducts/tv.csv"), readJsonFromFile("addProducts/parameters.json"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/response.json"), response.asJson());
    }

    @Test
    @Ignore
    public void testAddProductsBadCategory() throws Exception {
        response = request("api/addProducts", POST, null /* TODO use file */, readJsonFromFile("addProducts/parameters.json"));

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/responseBadCategory.json"), response.asJson());
    }

    @Test
    @Ignore
    public void testAddProductsBadFile() throws Exception {
        request("api/addCategory", POST, null, readJsonFromFile("addCategory/parameters.json"));

        response = request("api/addProducts", POST, null /* TODO use file */, readJsonFromFile("addProducts/parameters.json"));

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/responseBadFile.json"), response.asJson());
    }

    //==========================All=================================//

    @Test
    @Ignore
    public void testGetProductsByCategoryBadCategory() throws Exception {

    }

    //========================Removing==============================//

    @Test
    @Ignore
    public void testRemoveProducts() throws Exception {

    }

    @Test
    @Ignore
    public void testGetProductsByCategory() throws Exception {

    }

    //==============================================================//
    //==============================================================//
    //=========================Config===============================//
    //==============================================================//
    //==============================================================//

    @Test
    @Ignore
    public void testConfigAlgorithm() throws Exception {

    }

    @Test
    @Ignore
    public void testConfigAlgorithmBadNumber() throws Exception {

    }

    //==============================================================//
    //==============================================================//
    //=========================Other================================//
    //==============================================================//
    //==============================================================//

    @Test
    public void testRequest() throws Exception {

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/api/allCategories");

        Result result = route(request);
        assertEquals(OK, result.status());

        WSResponse response = request("api/allCategories", GET, null, null);

        assert response != null;
        assertEquals(OK, response.getStatus());
    }
}
