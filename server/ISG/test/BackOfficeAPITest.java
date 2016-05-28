import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import neo4j.Neo4jSessionFactory;
import org.junit.Ignore;
import org.junit.Test;
import play.libs.Json;
import play.libs.ws.WSResponse;
import play.mvc.Http;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static play.test.Helpers.*;

@SuppressWarnings("Duplicates")
public class BackOfficeAPITest extends APITest {

    private WSResponse response;

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
        List<String> text = questions.findValuesAsText("text");

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
    public void testAddQuestionsEmptyArrays() throws Exception {
        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        //Empty array Questions
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/EmptyArrays/bodyNoQuestions.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/EmptyArrays/responseNoQuestions.json"), response.asJson());

        //Empty array Answers
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/EmptyArrays/bodyNoAnswers.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/EmptyArrays/responseNoAnswers.json"), response.asJson());

        //Empty array Attributes
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/EmptyArrays/bodyNoAttributes.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/EmptyArrays/responseNoAttributes.json"), response.asJson());

        //Checking if Questions were not added
        response = request("api/questionsByCategory", GET, null, Json.newObject().put("code", "tvs"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals("[]", response.getBody());
    }

    @Test
    public void testAddQuestionsInvalidAttributes() throws Exception {
        Neo4jSessionFactory.getInstance().getNeo4jSession().query("CREATE (at1: Attribute {name: 'width (cm)', type: 'numeric'});", Collections.EMPTY_MAP);
        Neo4jSessionFactory.getInstance().getNeo4jSession().query("CREATE (at2: Attribute {name: 'resolution', type: 'categorical'});", Collections.EMPTY_MAP);

        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        //Invalid Attribute Name
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/InvalidAttributes/bodyInvalidAttribute.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/InvalidAttributes/responseInvalidAttribute.json"), response.asJson());

        //Invalid Operator
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/InvalidAttributes/bodyInvalidOperator.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/InvalidAttributes/responseInvalidOperator.json"), response.asJson());

        //Invalid Attribute operator relation
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/InvalidAttributes/bodyInvalidRelation.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/InvalidAttributes/responseInvalidRelation.json"), response.asJson());

        //Invalid Value
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/InvalidAttributes/bodyInvalidValue.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/InvalidAttributes/responseInvalidValue.json"), response.asJson());

        //Invalid Score
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/InvalidAttributes/bodyInvalidScore.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/InvalidAttributes/responseInvalidScore.json"), response.asJson());

        //Checking if Questions were not added
        response = request("api/questionsByCategory", GET, null, Json.newObject().put("code", "tvs"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals("[]", response.getBody());
    }

    @Test
    public void testAddQuestionsMissingFields() throws Exception {
        Neo4jSessionFactory.getInstance().getNeo4jSession().query("CREATE (at1: Attribute {name: 'width (cm)', type: 'numeric'});", Collections.EMPTY_MAP);
        Neo4jSessionFactory.getInstance().getNeo4jSession().query("CREATE (at2: Attribute {name: 'resolution', type: 'categorical'});", Collections.EMPTY_MAP);

        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        //Missing field Category
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/MissingFields/bodyMissingCategory.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/MissingFields/responseMissingCategory.json"), response.asJson());

        //Missing field Questions
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/MissingFields/bodyMissingQuestions.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/MissingFields/responseMissingQuestions.json"), response.asJson());

        //Missing field Answers //FIXME response is different than expected. Its detecting empty array of the missing field and not that its a missing field
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/MissingFields/bodyMissingAnswers.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/MissingFields/responseMissingAnswers.json"), response.asJson());

        //Missing field Attributes //FIXME response is different than expected. Its detecting empty array of the missing field and not that its a missing field
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/MissingFields/bodyMissingAttributes.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/MissingFields/responseMissingAttributes.json"), response.asJson());

        //Missing field Attribute Name
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/MissingFields/bodyMissingAttributeName.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/MissingFields/responseMissingAttributeName.json"), response.asJson());

        //Missing field Attribute Operator
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/MissingFields/bodyMissingAttributeOperator.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/MissingFields/responseMissingAttributeOperator.json"), response.asJson());

        //Missing field Attribute Value
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/MissingFields/bodyMissingAttributeValue.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/MissingFields/responseMissingAttributeValue.json"), response.asJson());

        //Missing field Attribute Score
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/MissingFields/bodyMissingAttributeScore.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/MissingFields/responseMissingAttributeScore.json"), response.asJson());

        //Checking if Questions were not added
        response = request("api/questionsByCategory", GET, null, Json.newObject().put("code", "tvs"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals("[]", response.getBody());
    }

    //========================Removing==============================//

    @Test
    public void testRemoveQuestions() throws Exception {
        Neo4jSessionFactory.getInstance().getNeo4jSession().query("CREATE (at1: Attribute {name: 'width (cm)', type: 'numeric'});", Collections.EMPTY_MAP);
        Neo4jSessionFactory.getInstance().getNeo4jSession().query("CREATE (at2: Attribute {name: 'resolution', type: 'categorical'});", Collections.EMPTY_MAP);

        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        //Adding a question. Should return empty JSON object
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/body.json"), null);

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("removeQuestions/response.json"), response.asJson());

        response = request("api/questionsByCategory", GET, null, Json.newObject().put("code", "tvs"));

        JsonNode body = Json.newObject().set("questions", Json.newArray().add(response.asJson().findValue("code")));

        //Removing questions. Should return empty JSON object
        response = request("api/removeQuestions", DELETE, body, null);

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("removeQuestions/response.json"), response.asJson());

        response = request("api/questionsByCategory", GET, null, Json.newObject().put("code", "tvs"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(Json.newArray(), response.asJson());
    }

    @Test
    public void testRemoveQuestionsMissingField() throws Exception {
        Neo4jSessionFactory.getInstance().getNeo4jSession().query("CREATE (at1: Attribute {name: 'width (cm)', type: 'numeric'});", Collections.EMPTY_MAP);
        Neo4jSessionFactory.getInstance().getNeo4jSession().query("CREATE (at2: Attribute {name: 'resolution', type: 'categorical'});", Collections.EMPTY_MAP);

        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        //Adding a question. Should return empty JSON object
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/body.json"), null);

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("removeQuestions/response.json"), response.asJson());

        response = request("api/removeQuestions", DELETE, readJsonFromFile("removeQuestions/bodyMissingField.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("removeQuestions/responseMissingField.json"), response.asJson());

        response = request("api/questionsByCategory", GET, null, Json.newObject().put("code", "tvs"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertNotEquals(Json.newArray(), response.asJson());
    }

    @Test
    public void testRemoveQuestionsInvalidQuestions() throws Exception {
        Neo4jSessionFactory.getInstance().getNeo4jSession().query("CREATE (at1: Attribute {name: 'width (cm)', type: 'numeric'});", Collections.EMPTY_MAP);
        Neo4jSessionFactory.getInstance().getNeo4jSession().query("CREATE (at2: Attribute {name: 'resolution', type: 'categorical'});", Collections.EMPTY_MAP);

        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        //Adding a question. Should return empty JSON object
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/body.json"), null);

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("removeQuestions/response.json"), response.asJson());

        response = request("api/removeQuestions", DELETE, readJsonFromFile("removeQuestions/bodyInvalidQuestion.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("removeQuestions/responseInvalidQuestion.json"), response.asJson());

        response = request("api/questionsByCategory", GET, null, Json.newObject().put("code", "tvs"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertNotEquals(Json.newArray(), response.asJson());
    }

    //==========================All=================================//

    @Test
    @Ignore
    public void testGetQuestionsByCategory() throws Exception {

    }

    @Test
    @Ignore
    public void testGetQuestionsByCategoryInvalidCategory() throws Exception {

    }

    @Test
    @Ignore
    public void testGetSequencesByCategory() throws Exception {

    }

    @Test
    @Ignore
    public void testGetSequencesByCategoryInvalidCategory() throws Exception {

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

    //========================Removing==============================//

    @Test
    @Ignore
    public void testRemoveProducts() throws Exception {

    }

    @Test
    @Ignore
    public void testGetProductsByCategory() throws Exception {

    }

    //==========================All=================================//

    @Test
    @Ignore
    public void testGetProductsByCategoryBadCategory() throws Exception {

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
