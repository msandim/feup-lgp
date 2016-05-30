import com.fasterxml.jackson.databind.JsonNode;
import org.junit.*;
import play.libs.Json;

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

        request("api/addCategory", POST, Json.newObject().put("code", "empty").put("name", "Empty Category"), null);

        response = request("api/getNextQuestion", POST, readJsonFromFile("getNextQuestion/bodyNoQuestions.json"), null);

        assert response != null;

        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("getNextQuestion/responseNoQuestions.json"), response.asJson());
    }

    @Test
    @Ignore
    public void testGetNextQuestionInvalidFields() {

    }

    @Test
    @Ignore
    public void testGetNextQuestionMissingFields() {

    }

    //==============================================================//
    //==============================================================//
    //========================FeedBack==============================//
    //==============================================================//
    //==============================================================//

    //=========================Normal===============================//

    @Test
    @Ignore
    public void testSendFeedback() {

    }

    //=========================Errors===============================//

    @Test
    @Ignore
    public void testSendFeedbackInvalidCategory() {

    }

    @Test
    @Ignore
    public void testSendFeedbackBadAnswers() {

    }

    @Test
    @Ignore
    public void testSendFeedbackBadFeedback() {

    }
}
