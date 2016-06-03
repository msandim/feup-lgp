import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import neo4j.Neo4jSessionFactory;
import org.junit.Test;
import play.libs.Json;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static play.test.Helpers.*;

public class MobileAPITest extends APITest {

    //==============================================================//
    //==============================================================//
    //======================Next Question===========================//
    //==============================================================//
    //==============================================================//

    //=========================Normal===============================//

    @Test
    public void testGetNextQuestionFirstQuestion() {
        populateDatabase();

        response = request("api/getNextQuestion", POST, readJsonFromFile("getNextQuestion/bodyFirstQuestion.json"), null);

        assert response != null;
        assertEquals(OK, response.getStatus());

        JsonNode jsonResponse = response.asJson();

        assertTrue(jsonResponse.has("question"));
        assertTrue(jsonResponse.get("question").elements().hasNext());
        assertTrue(jsonResponse.get("question").has("code"));
        assertTrue(jsonResponse.get("question").has("text"));

        assertTrue(jsonResponse.has("answers"));
        assertTrue(jsonResponse.get("answers").isArray());
        assertTrue(jsonResponse.get("answers").elements().hasNext());
        assertTrue(jsonResponse.get("answers").get(0).has("code"));
        assertTrue(jsonResponse.get("answers").get(0).has("text"));

        assertTrue(jsonResponse.has("products"));
        assertTrue(jsonResponse.get("products").isArray());
        assertFalse(jsonResponse.get("products").elements().hasNext());
    }

    @Test
    public void testGetNextQuestionFollowingQuestion() {
        populateDatabase();

        response = request("api/getNextQuestion", POST, readJsonFromFile("getNextQuestion/bodyFollowingQuestion.json"), null);

        assert response != null;
        assertEquals(OK, response.getStatus());

        JsonNode jsonResponse = response.asJson();

        assertTrue(jsonResponse.has("question"));
        assertTrue(jsonResponse.get("question").elements().hasNext());
        assertTrue(jsonResponse.get("question").has("code"));
        assertTrue(jsonResponse.get("question").has("text"));

        List<String> codes = jsonResponse.get("question").findValuesAsText("code");

        assertEquals(codes.indexOf("q1"), -1);
        assertEquals(codes.indexOf("q2"), -1);

        assertTrue(jsonResponse.has("answers"));
        assertTrue(jsonResponse.get("answers").isArray());
        assertTrue(jsonResponse.get("answers").get(0).has("code"));
        assertTrue(jsonResponse.get("answers").get(0).has("text"));

        assertTrue(jsonResponse.has("products"));
        assertTrue(jsonResponse.get("products").isArray());
        assertTrue(jsonResponse.get("products").elements().hasNext());

        JsonNode product = jsonResponse.get("products").get(0);

        assertTrue(product.has("ean"));
        assertTrue(product.has("name"));
        assertTrue(product.has("price"));
        assertTrue(product.has("score"));
    }

    @Test
    public void testGetNextQuestionFinalProducts() {
        populateDatabase();

        response = request("api/getNextQuestion", POST, readJsonFromFile("getNextQuestion/bodyFinalProducts.json"), null);

        assert response != null;
        assertEquals(OK, response.getStatus());

        JsonNode jsonResponse = response.asJson();

        assertTrue(jsonResponse.has("question"));
        assertFalse(jsonResponse.get("question").elements().hasNext());

        assertTrue(jsonResponse.has("answers"));
        assertTrue(jsonResponse.get("answers").isArray());
        assertFalse(jsonResponse.get("answers").elements().hasNext());

        assertTrue(jsonResponse.has("products"));
        assertTrue(jsonResponse.get("products").isArray());
        assertTrue(jsonResponse.get("products").elements().hasNext());

        JsonNode product = jsonResponse.get("products").get(0);

        assertTrue(product.has("ean"));
        assertTrue(product.has("name"));
        assertTrue(product.has("price"));
        assertTrue(product.has("score"));
    }

    //=========================Errors================================//

    @Test
    public void testGetNextQuestionNoQuestions() {
        populateDatabase();

        //Adding a category with nothing associated
        request("api/addCategory", POST, Json.newObject().put("code", "empty").put("name", "Empty Category"), null);

        response = request("api/getNextQuestion", POST, readJsonFromFile("getNextQuestion/bodyNoQuestions.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("getNextQuestion/responseNoQuestions.json"), response.asJson());
    }

    @Test
    public void testGetNextQuestionInvalidFields() {
        populateDatabase();

        //Getting the next question with invalid answers
        response = request("api/getNextQuestion", POST, readJsonFromFile("getNextQuestion/InvalidFields/bodyInvalidAnswers.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("getNextQuestion/InvalidFields/responseInvalidQuestionsAnswers.json"), response.asJson());

        //Getting the next question with invalid questions
        response = request("api/getNextQuestion", POST, readJsonFromFile("getNextQuestion/InvalidFields/bodyInvalidQuestions.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("getNextQuestion/InvalidFields/responseInvalidQuestionsAnswers.json"), response.asJson());

        //Getting the next question with invalid category
        response = request("api/getNextQuestion", POST, readJsonFromFile("getNextQuestion/InvalidFields/bodyInvalidCategory.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("getNextQuestion/InvalidFields/responseInvalidCategory.json"), response.asJson());
    }

    @Test
    public void testGetNextQuestionMissingFields() {
        populateDatabase();

        //Getting the next question with no category
        response = request("api/getNextQuestion", POST, readJsonFromFile("getNextQuestion/MissingFields/bodyMissingFieldCategory.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("getNextQuestion/MissingFields/responseMissingFieldCategory.json"), response.asJson());

        //Getting the next question with no answers
        response = request("api/getNextQuestion", POST, readJsonFromFile("getNextQuestion/MissingFields/bodyMissingFieldAnswers.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("getNextQuestion/MissingFields/responseMissingFieldAnswers.json"), response.asJson());

        //Getting the next question with no question
        response = request("api/getNextQuestion", POST, readJsonFromFile("getNextQuestion/MissingFields/bodyMissingFieldQuestion.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("getNextQuestion/MissingFields/responseMissingFieldQuestion.json"), response.asJson());

        //Getting the next question with no answer
        response = request("api/getNextQuestion", POST, readJsonFromFile("getNextQuestion/MissingFields/bodyMissingFieldAnswer.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("getNextQuestion/MissingFields/responseMissingFieldAnswer.json"), response.asJson());
    }

    //==============================================================//
    //==============================================================//
    //========================FeedBack==============================//
    //==============================================================//
    //==============================================================//

    //=========================Normal===============================//

    @Test
    public void testSendFeedback() {
        populateDatabase();

        response = request("api/sendFeedback", POST, readJsonFromFile("sendFeedback/body.json"), null);

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("sendFeedback/response.json"), response.asJson());
    }

    //=========================Errors===============================//

    @Test
    public void testSendFeedbackEmptyAnswerList() {
        populateDatabase();

        response = request("api/sendFeedback", POST, readJsonFromFile("sendFeedback/bodyEmptyAnswerList.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("sendFeedback/responseEmptyAnswerList.json"), response.asJson());
    }

    @Test
    public void testSendFeedbackInvalidFields() {
        populateDatabase();

        //Sending feedback with invalid category
        response = request("api/sendFeedback", POST, readJsonFromFile("sendFeedback/InvalidFields/bodyInvalidCategory.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("sendFeedback/InvalidFields/responseInvalidCategory.json"), response.asJson());

        //Sending feedback with invalid feedback
        response = request("api/sendFeedback", POST, readJsonFromFile("sendFeedback/InvalidFields/bodyInvalidFeedback.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("sendFeedback/InvalidFields/responseInvalidFeedback.json"), response.asJson());

        //Sending feedback with invalid question
        response = request("api/sendFeedback", POST, readJsonFromFile("sendFeedback/InvalidFields/bodyInvalidQuestion.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("sendFeedback/InvalidFields/responseInvalidQuestionAnswer.json"), response.asJson());

        //Sending feedback with invalid answer
        response = request("api/sendFeedback", POST, readJsonFromFile("sendFeedback/InvalidFields/bodyInvalidAnswer.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("sendFeedback/InvalidFields/responseInvalidQuestionAnswer.json"), response.asJson());

        //Sending feedback with an invalid sequence
        settingUpAttributes();

        //Adding a new question for the sequence
        response = request("api/addQuestions", POST, readJsonFromFile("sendFeedback/InvalidFields/bodyAddingQuestionsForSequence.json"), null);
        assert response != null;
        assertEquals(OK, response.getStatus());

        //Adding a new question for the new category
        response = request("api/questionsByCategory", GET, null, Json.newObject().put("code", "dws"));
        assert response != null;
        assertEquals(OK, response.getStatus());

        //Getting the code of the questions
        List<String> codes = response.asJson().findValuesAsText("code");

        JsonNode body = readJsonFromFile("sendFeedback/InvalidFields/bodyInvalidSequence.json");

        ((ArrayNode) body.get("answers"))
                .add(Json.newObject()
                        .put("question", codes.get(0))
                        .put("answer", codes.get(1))
                );

        //Sending feedback with questions belonging to another category
        response = request("api/sendFeedback", POST, body, null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("sendFeedback/InvalidFields/responseInvalidSequence.json"), response.asJson());
    }

    @Test
    public void testSendFeedbackMissingFields() {
        populateDatabase();

        //Sending feedback with no category
        response = request("api/sendFeedback", POST, readJsonFromFile("sendFeedback/MissingFields/bodyMissingCategory.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("sendFeedback/MissingFields/responseMissingCategory.json"), response.asJson());

        //Sending feedback with no feedback
        response = request("api/sendFeedback", POST, readJsonFromFile("sendFeedback/MissingFields/bodyMissingFeedback.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("sendFeedback/MissingFields/responseMissingFeedback.json"), response.asJson());

        //Sending feedback with no answers
        response = request("api/sendFeedback", POST, readJsonFromFile("sendFeedback/MissingFields/bodyMissingAnswers.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("sendFeedback/MissingFields/responseMissingAnswers.json"), response.asJson());

        //Sending feedback with no answer
        response = request("api/sendFeedback", POST, readJsonFromFile("sendFeedback/MissingFields/bodyMissingAnswer.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("sendFeedback/MissingFields/responseMissingAnswer.json"), response.asJson());

        //Sending feedback with no question
        response = request("api/sendFeedback", POST, readJsonFromFile("sendFeedback/MissingFields/bodyMissingQuestion.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("sendFeedback/MissingFields/responseMissingQuestion.json"), response.asJson());
    }
}
