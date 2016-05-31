import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import neo4j.Neo4jSessionFactory;

import play.libs.Json;
import play.libs.ws.WSResponse;
import play.mvc.Http;
import play.mvc.Result;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static play.test.Helpers.*;

@SuppressWarnings("unchecked")
public class BackOfficeAPITest extends APITest {

    //TODO maybe change to not use other APIs and insert directly to the database

    //==============================================================//
    //==============================================================//
    //========================Category==============================//
    //==============================================================//
    //==============================================================//

    //==========================Add=================================//

    @Test
    public void testAddCategory() {
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
    public void testAddCategoryBadName() {
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
    public void testAddCategoryMissingField() {
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

    @Test
    public void testRemoveCategory() {
        response = request("api/addCategory", POST, readJsonFromFile("addCategory/parameters.json"), null);

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("addCategory/response.json"), response.asJson());

        response = request("api/removeCategory", DELETE, null, readJsonFromFile("removeCategory/parameters.json"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("removeCategory/response.json"), response.asJson());

        response = request("api/allCategories", GET, null, null);

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(Json.newArray(), response.asJson());
    }

    @Test
    public void testRemoveCategoryError() {
        response = request("api/addCategory", POST, readJsonFromFile("addCategory/parameters.json"), null);

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("addCategory/response.json"), response.asJson());

        response = request("api/removeCategory", DELETE, null, readJsonFromFile("removeCategory/parametersWrongCode.json"));

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("removeCategory/responseWrongCode.json"), response.asJson());

        response = request("api/allCategories", GET, null, null);

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertNotEquals(Json.newArray(), response.asJson());
    }

    //==========================All=================================//

    @Test
    public void testGetAllCategories() {
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
    public void testAddQuestions() {
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
    }

    @Test
    public void testAddQuestionsInvalidCategory() {
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
    public void testAddQuestionsEmptyArrays() {
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
    public void testAddQuestionsInvalidAttributes() {
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
    public void testAddQuestionsMissingFields() {
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

        //Missing field Answers Text
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/MissingFields/bodyMissingAnswersText.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/MissingFields/responseMissingText.json"), response.asJson());

        //Missing field Questions Text
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/MissingFields/bodyMissingQuestionsText.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/MissingFields/responseMissingText.json"), response.asJson());

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
    public void testRemoveQuestions() {
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
    public void testRemoveQuestionsMissingField() {
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
    public void testRemoveQuestionsInvalidQuestions() {
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
    public void testGetQuestionsByCategory() {
        List<String> text;
        Neo4jSessionFactory.getInstance().getNeo4jSession().query("CREATE (at1: Attribute {name: 'width (cm)', type: 'numeric'});", Collections.EMPTY_MAP);
        Neo4jSessionFactory.getInstance().getNeo4jSession().query("CREATE (at2: Attribute {name: 'resolution', type: 'categorical'});", Collections.EMPTY_MAP);

        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);
        request("api/addCategory", POST, Json.newObject().put("name", "Computadores").put("code", "pcs"), null);

        request("api/addQuestions", POST, readJsonFromFile("getQuestionsByCategory/question1.json"), null);
        request("api/addQuestions", POST, readJsonFromFile("getQuestionsByCategory/question2.json"), null);
        request("api/addQuestions", POST, readJsonFromFile("getQuestionsByCategory/question3.json"), null);
        request("api/addQuestions", POST, readJsonFromFile("getQuestionsByCategory/question4.json"), null);

        response = request("api/questionsByCategory", GET, null, Json.newObject().put("code", "tvs"));

        assert response != null;
        assertEquals(OK, response.getStatus());

        text = response.asJson().findValuesAsText("text");

        assertTrue(text.contains("QUESTION_1"));
        assertTrue(text.contains("QUESTION_1_1"));
        assertTrue(text.contains("QUESTION_1_2"));
        assertTrue(text.contains("QUESTION_1_3"));
        assertTrue(text.contains("QUESTION_2"));
        assertTrue(text.contains("QUESTION_2_1"));
        assertTrue(text.contains("QUESTION_2_2"));
        assertTrue(text.contains("QUESTION_2_3"));

        response = request("api/questionsByCategory", GET, null, Json.newObject().put("code", "pcs"));

        assert response != null;
        assertEquals(OK, response.getStatus());

        text = response.asJson().findValuesAsText("text");

        assertTrue(text.contains("QUESTION_3"));
        assertTrue(text.contains("QUESTION_3_1"));
        assertTrue(text.contains("QUESTION_3_2"));
        assertTrue(text.contains("QUESTION_3_3"));
        assertTrue(text.contains("QUESTION_4"));
        assertTrue(text.contains("QUESTION_4_1"));
        assertTrue(text.contains("QUESTION_4_2"));
        assertTrue(text.contains("QUESTION_4_3"));
    }

    @Test
    public void testGetQuestionsByCategoryInvalidCategory() {
        Neo4jSessionFactory.getInstance().getNeo4jSession().query("CREATE (at1: Attribute {name: 'width (cm)', type: 'numeric'});", Collections.EMPTY_MAP);
        Neo4jSessionFactory.getInstance().getNeo4jSession().query("CREATE (at2: Attribute {name: 'resolution', type: 'categorical'});", Collections.EMPTY_MAP);

        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);
        request("api/addCategory", POST, Json.newObject().put("name", "Computadores").put("code", "pcs"), null);

        request("api/addQuestions", POST, readJsonFromFile("getQuestionsByCategory/question1.json"), null);
        request("api/addQuestions", POST, readJsonFromFile("getQuestionsByCategory/question2.json"), null);
        request("api/addQuestions", POST, readJsonFromFile("getQuestionsByCategory/question3.json"), null);
        request("api/addQuestions", POST, readJsonFromFile("getQuestionsByCategory/question4.json"), null);

        response = request("api/questionsByCategory", GET, null, Json.newObject().put("code", "INVALID_CATEGORY"));

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("getQuestionsByCategory/responseInvalidCategory.json"), response.asJson());
    }

    //==============================================================//
    //==============================================================//
    //========================Sequences=============================//
    //==============================================================//
    //==============================================================//

    @Test
    public void testGetSequencesByCategory() {
        populateDatabase();

        request("api/sendFeedback", POST, readJsonFromFile("sendFeedback/body.json"), null);

        response = request("api/sequencesByCategory", GET, null, readJsonFromFile("getSequencesByCategory/parameters.json"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertTrue(response.asJson().isArray());

        JsonNode jsonResponse = response.asJson().get(0);

        assertTrue(jsonResponse.has("feedback"));
        assertTrue(jsonResponse.has("questions"));
        assertTrue(jsonResponse.get("questions").elements().hasNext());

        JsonNode questions = jsonResponse.get("questions").get(0);
        assertTrue(questions.has("selected_answer"));
        assertTrue(questions.get("selected_answer").has("code"));
        assertTrue(questions.get("selected_answer").has("text"));
        assertTrue(questions.has("question"));

        JsonNode question = questions.get("question");
        assertTrue(question.has("code"));
        assertTrue(question.has("text"));
        assertTrue(question.has("answers"));
        assertTrue(question.get("answers").isArray());
        assertTrue(question.get("answers").elements().hasNext());
        assertTrue(question.get("answers").get(0).has("code"));
        assertTrue(question.get("answers").get(0).has("text"));
    }

    @Test
    public void testGetSequencesByCategoryInvalidCategory() {
        populateDatabase();

        request("api/sendFeedback", POST, readJsonFromFile("sendFeedback/body.json"), null);

        response = request("api/sequencesByCategory", GET, null, readJsonFromFile("getSequencesByCategory/parametersInvalidCategory.json"));

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("getSequencesByCategory/responseInvalidCategory.json"), response.asJson());
    }

    //==============================================================//
    //==============================================================//
    //========================Products==============================//
    //==============================================================//
    //==============================================================//

    //==========================Add=================================//

    @Test
    public void testAddProducts() {
        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        //Adding products. Should return empty JSON object
        response = requestAddProducts("api/addProducts", new File("addProducts/files/file.csv"), "tvs");

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("addProducts/response.json"), response.asJson());

        response = request("api/productsByCategory", GET, null, Json.newObject().put("code", "tvs"));

        assert response != null;
        assertEquals(OK, response.getStatus());

        List<String> prices = response.asJson().findValuesAsText("price");

        assertEquals("TV LED Smart TV 55'' PANASONIC TX-55CX680", response.asJson().findValue("name").asText());
        assertNotEquals(prices.indexOf("1999"), -1);
        assertNotEquals(prices.indexOf("1129"), -1);
        assertNotEquals(prices.indexOf("1399"), -1);
    }

    @Test
    public void testAddProductsInvalidCategory() {
        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        response = requestAddProducts("api/addProducts", new File("addProducts/files/file.csv"), "INVALID_CATEGORY");

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addProducts/responseInvalidCategory.json"), response.asJson());

        response = request("api/productsByCategory", GET, null, Json.newObject().put("code", "tvs"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(Json.newArray(), response.asJson());
    }

    @Test
    public void testAddProductsMissingFile() {
        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        response = requestAddProducts("api/addProducts", null, "tvs");

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addProducts/responseMissingFile.json"), response.asJson());

        response = request("api/productsByCategory", GET, null, Json.newObject().put("code", "tvs"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(Json.newArray(), response.asJson());
    }

    @Test
    public void testAddProductsMissingAttributeType() {
        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        response = requestAddProducts("api/addProducts", new File("addProducts/files/fileMissingAttribute.csv"), "tvs");

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addProducts/responseMissingAttribute.json"), response.asJson());

        response = request("api/productsByCategory", GET, null, Json.newObject().put("code", "tvs"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(Json.newArray(), response.asJson());
    }

    @Test
    public void testAddProductsInvalidAttributeType() {
        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        response = requestAddProducts("api/addProducts", new File("addProducts/files/fileInvalidAttribute.csv"), "tvs");

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addProducts/responseInvalidAttribute.json"), response.asJson());

        response = request("api/productsByCategory", GET, null, Json.newObject().put("code", "tvs"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(Json.newArray(), response.asJson());
    }

    //========================Removing==============================//

    //TODO verify if they are still in the database or not

    @Test
    public void testRemoveProducts() {
        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        requestAddProducts("api/addProducts", new File("removeProducts/products.csv"), "tvs");

        response = request("api/removeProducts", DELETE, readJsonFromFile("removeProducts/body.json"), null);

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("removeProducts/response.json"), response.asJson());
    }

    @Test
    public void testRemoveProductsInvalidCategory() {
        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        requestAddProducts("api/addProducts", new File("removeProducts/products.csv"), "tvs");

        response = request("api/removeProducts", DELETE, readJsonFromFile("removeProducts/bodyInvalidCategory.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("removeProducts/responseInvalidCategory.json"), response.asJson());
    }

    @Test
    public void testRemoveProductsMissingFields() {
        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        requestAddProducts("api/addProducts", new File("removeProducts/products.csv"), "tvs");

        response = request("api/removeProducts", DELETE, readJsonFromFile("removeProducts/bodyMissingFieldCategory.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("removeProducts/responseMissingFieldCategory.json"), response.asJson());

        response = request("api/removeProducts", DELETE, readJsonFromFile("removeProducts/bodyMissingFieldProducts.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("removeProducts/responseMissingFieldProducts.json"), response.asJson());
    }

    @Test
    public void testRemoveProductsInvalidProducts() {
        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        requestAddProducts("api/addProducts", new File("removeProducts/products.csv"), "tvs");

        response = request("api/removeProducts", DELETE, readJsonFromFile("removeProducts/bodyInvalidProducts.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("removeProducts/responseInvalidProducts.json"), response.asJson());
    }

    //==========================All=================================//

    @Test
    public void testGetProductsByCategory() {
        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        response = requestAddProducts("api/addProducts", new File("addProducts/files/file.csv"), "tvs");

        assert response != null;
        assertEquals(OK, response.getStatus());

        response = request("api/productsByCategory", GET, null, readJsonFromFile("getProductsByCategory/parameters.json"));

        assert response != null;
        assertEquals(OK, response.getStatus());

        List<String> ean = response.asJson().findValuesAsText("ean");
        List<String> names = response.asJson().findValuesAsText("name");
        List<String> prices = response.asJson().findValuesAsText("price");

        assertNotEquals(names.indexOf("TV LED Smart TV 3D 48'' SAMSUNG UE48JU7500T"), -1);
        assertNotEquals(names.indexOf("TV LED Smart TV 60'' SAMSUNG UE60JU6400KXXC"), -1);
        assertNotEquals(names.indexOf("TV LED Smart TV 55'' PANASONIC TX-55CX680"), -1);
        assertNotEquals(names.indexOf("TV LED PANASONIC Smart TV UHD 3D 48'' TX-48CX400"), -1);

        assertNotEquals(ean.indexOf("1"), -1);
        assertNotEquals(ean.indexOf("3"), -1);
        assertNotEquals(ean.indexOf("4"), -1);
        assertNotEquals(ean.indexOf("2"), -1);

        assertNotEquals(prices.indexOf("649.99"), -1);
        assertNotEquals(prices.indexOf("1129"), -1);
        assertNotEquals(prices.indexOf("1399"), -1);
        assertNotEquals(prices.indexOf("1999"), -1);
    }

    @Test
    public void testGetProductsByCategoryInvalidCategory() {
        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        response = requestAddProducts("api/addProducts", new File("addProducts/files/file.csv"), "tvs");

        assert response != null;
        assertEquals(OK, response.getStatus());

        response = request("api/productsByCategory", GET, null, readJsonFromFile("getProductsByCategory/parametersInvalidCategory.json"));

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("getProductsByCategory/responseInvalidCategory.json"), response.asJson());
    }

    //==============================================================//
    //==============================================================//
    //=========================Config===============================//
    //==============================================================//
    //==============================================================//

    //TODO verify is values were changed by querying the database

    @Test
    public void testConfigAlgorithm() {
        response = request("api/configAlgorithm", POST, readJsonFromFile("configAlgorithm/body.json"), null);

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("configAlgorithm/response.json"), response.asJson());
    }

    @Test
    public void testConfigAlgorithmInvalidFields() {
        response = request("api/configAlgorithm", POST, readJsonFromFile("configAlgorithm/InvalidFields/bodyInvalidFieldAlpha.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("configAlgorithm/InvalidFields/responseInvalidFieldAlpha.json"), response.asJson());

        response = request("api/configAlgorithm", POST, readJsonFromFile("configAlgorithm/InvalidFields/bodyInvalidFieldBeta.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("configAlgorithm/InvalidFields/responseInvalidFieldBeta.json"), response.asJson());

        response = request("api/configAlgorithm", POST, readJsonFromFile("configAlgorithm/InvalidFields/bodyInvalidFieldGamma.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("configAlgorithm/InvalidFields/responseInvalidFieldGamma.json"), response.asJson());

        response = request("api/configAlgorithm", POST, readJsonFromFile("configAlgorithm/InvalidFields/bodyInvalidFieldNumberOfProducts.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("configAlgorithm/InvalidFields/responseInvalidFieldNumberOfProducts.json"), response.asJson());

        response = request("api/configAlgorithm", POST, readJsonFromFile("configAlgorithm/InvalidFields/bodyInvalidFieldNumberOfQuestions.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("configAlgorithm/InvalidFields/responseInvalidFieldNumberOfQuestions.json"), response.asJson());
    }

    @Test
    public void testConfigAlgorithmMissingFields() {
        response = request("api/configAlgorithm", POST, readJsonFromFile("configAlgorithm/MissingFields/bodyMissingFieldAlpha.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("configAlgorithm/MissingFields/responseMissingFieldAlpha.json"), response.asJson());

        response = request("api/configAlgorithm", POST, readJsonFromFile("configAlgorithm/MissingFields/bodyMissingFieldBeta.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("configAlgorithm/MissingFields/responseMissingFieldBeta.json"), response.asJson());

        response = request("api/configAlgorithm", POST, readJsonFromFile("configAlgorithm/MissingFields/bodyMissingFieldGamma.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("configAlgorithm/MissingFields/responseMissingFieldGamma.json"), response.asJson());

        response = request("api/configAlgorithm", POST, readJsonFromFile("configAlgorithm/MissingFields/bodyMissingFieldNumberOfProducts.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("configAlgorithm/MissingFields/responseMissingFieldNumberOfProducts.json"), response.asJson());

        response = request("api/configAlgorithm", POST, readJsonFromFile("configAlgorithm/MissingFields/bodyMissingFieldNumberOfQuestions.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("configAlgorithm/MissingFields/responseMissingFieldNumberOfQuestions.json"), response.asJson());
    }

    //==============================================================//
    //==============================================================//
    //=========================Other================================//
    //==============================================================//
    //==============================================================//

    @Test
    public void testRequest() {

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
