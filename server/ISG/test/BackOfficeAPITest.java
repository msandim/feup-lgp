import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import play.libs.Json;
import play.libs.ws.WSResponse;
import play.mvc.Http;
import play.mvc.Result;

import java.io.File;
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
    public void testAddCategoryInvalidName() {
        //Adding a category. Should return empty JSON object
        response = request("api/addCategory", POST, readJsonFromFile("addCategory/parameters.json"), null);

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("addCategory/response.json"), response.asJson());

        //Trying to add another category with the same name. It should return an error.
        response = request("api/addCategory", POST, readJsonFromFile("addCategory/parameters.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addCategory/responseInvalidName.json"), response.asJson());

        //Checking if the category was not added. The other category with the same code should be there
        response = request("api/allCategories", GET, null, null);

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(1, response.asJson().findValues("code").size());
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

        //Checking if the category was not added
        response = request("api/allCategories", GET, null, null);

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(Json.newArray(), response.asJson());
    }

    //========================Removing==============================//

    @Test
    public void testRemoveCategory() {
        //Adding a category
        response = request("api/addCategory", POST, readJsonFromFile("addCategory/parameters.json"), null);

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("addCategory/response.json"), response.asJson());

        //Removing a category
        response = request("api/removeCategory", DELETE, null, readJsonFromFile("removeCategory/parameters.json"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("removeCategory/response.json"), response.asJson());

        //Checking if the category was removed
        response = request("api/allCategories", GET, null, null);

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(Json.newArray(), response.asJson());
    }

    @Test
    public void testRemoveCategoryInvalidCategory() {
        //Adding a category
        response = request("api/addCategory", POST, readJsonFromFile("addCategory/parameters.json"), null);

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("addCategory/response.json"), response.asJson());

        //Removing a category with the wrong code
        response = request("api/removeCategory", DELETE, null, readJsonFromFile("removeCategory/parametersWrongCode.json"));

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("removeCategory/responseWrongCode.json"), response.asJson());

        //Checking if the category was not removed
        response = request("api/allCategories", GET, null, null);

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertNotEquals(Json.newArray(), response.asJson());
    }

    //========================GetAll================================//

    @Test
    public void testGetAllCategories() {
        //Adding the first Category
        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tv"), null);

        //Adding the second Category
        request("api/addCategory", POST, Json.newObject().put("name", "Maquinas de Lavar").put("code", "maqla"), null);

        //Getting all the categories and checking response
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
        settingUpAttributes();

        //adding a category
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

        List<String> answersFirstQuestion = questions.findValuesAsText("text");

        assertEquals(OK, response.getStatus());
        assertEquals("Qual o tamanho da sua sala?", questions.get("text").asText());
        assertNotEquals(answersFirstQuestion.indexOf("Pequena"), -1);
        assertNotEquals(answersFirstQuestion.indexOf("Media"), -1);
        assertNotEquals(answersFirstQuestion.indexOf("Grande"), -1);

        //Fixme add better verifications
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
        assertEquals(Json.newArray(), response.asJson());
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
        assertEquals(Json.newArray(), response.asJson());
    }

    @Test
    public void testAddQuestionsInvalidAttributes() {
        settingUpAttributes();

        //Adding the category
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
        assertEquals(Json.newArray(), response.asJson());
    }

    @Test
    public void testAddQuestionsMissingFields() {
        settingUpAttributes();

        //Adding the category
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
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/MissingFields/bodyMissingAnswerText.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/MissingFields/responseMissingAnswerText.json"), response.asJson());

        //Missing field Questions Text
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/MissingFields/bodyMissingQuestionText.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/MissingFields/responseMissingQuestionText.json"), response.asJson());

        //Missing field Answers
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/MissingFields/bodyMissingAnswers.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addQuestions/MissingFields/responseMissingAnswers.json"), response.asJson());

        //Missing field Attributes
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
        assertEquals(Json.newArray(), response.asJson());
    }

    //========================Removing==============================//

    @Test
    public void testRemoveQuestions() {
        settingUpAttributes();

        //Adding the category
        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        //Adding a question. Should return empty JSON object
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/body.json"), null);

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("removeQuestions/response.json"), response.asJson());

        //Getting the code for the question added
        response = request("api/questionsByCategory", GET, null, Json.newObject().put("code", "tvs"));

        //Preparing the body with the question code
        JsonNode body = Json.newObject().set("questions", Json.newArray().add(response.asJson().findValue("code")));

        //Removing questions. Should return empty JSON object
        response = request("api/removeQuestions", DELETE, body, null);

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("removeQuestions/response.json"), response.asJson());

        //verifying if the questions were removed
        response = request("api/questionsByCategory", GET, null, Json.newObject().put("code", "tvs"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(Json.newArray(), response.asJson());
    }

    @Test
    public void testRemoveQuestionsMissingField() {
        settingUpAttributes();

        //Adding the category
        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        //Adding a question. Should return empty JSON object
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/body.json"), null);

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("removeQuestions/response.json"), response.asJson());

        //Removing questions with a missing field should return an error
        response = request("api/removeQuestions", DELETE, readJsonFromFile("removeQuestions/bodyMissingField.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("removeQuestions/responseMissingField.json"), response.asJson());

        //verifying if the questions were removed
        response = request("api/questionsByCategory", GET, null, Json.newObject().put("code", "tvs"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertNotEquals(Json.newArray(), response.asJson());
    }

    @Test
    public void testRemoveQuestionsInvalidQuestions() {
        settingUpAttributes();

        //Adding the category
        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        //Adding a question. Should return empty JSON object
        response = request("api/addQuestions", POST, readJsonFromFile("addQuestions/body.json"), null);

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("removeQuestions/response.json"), response.asJson());

        //Removing questions with a invalid field should return an error
        response = request("api/removeQuestions", DELETE, readJsonFromFile("removeQuestions/bodyInvalidQuestion.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("removeQuestions/responseInvalidQuestion.json"), response.asJson());

        //verifying if the questions were removed
        response = request("api/questionsByCategory", GET, null, Json.newObject().put("code", "tvs"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertNotEquals(Json.newArray(), response.asJson());
    }

    //========================GetAll================================//

    @Test
    public void testGetQuestionsByCategory() {
        List<String> text;

        settingUpAttributes();

        //Adding the categories
        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);
        request("api/addCategory", POST, Json.newObject().put("name", "Computadores").put("code", "pcs"), null);

        //Adding questions
        request("api/addQuestions", POST, readJsonFromFile("getQuestionsByCategory/question1.json"), null);
        request("api/addQuestions", POST, readJsonFromFile("getQuestionsByCategory/question2.json"), null);
        request("api/addQuestions", POST, readJsonFromFile("getQuestionsByCategory/question3.json"), null);
        request("api/addQuestions", POST, readJsonFromFile("getQuestionsByCategory/question4.json"), null);

        //Getting questions for the first category
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

        //Getting questions for the second category
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

        //FIXME better verification
    }

    @Test
    public void testGetQuestionsByCategoryInvalidCategory() {
        settingUpAttributes();

        //Adding the categories
        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);
        request("api/addCategory", POST, Json.newObject().put("name", "Computadores").put("code", "pcs"), null);

        //Adding questions
        request("api/addQuestions", POST, readJsonFromFile("getQuestionsByCategory/question1.json"), null);
        request("api/addQuestions", POST, readJsonFromFile("getQuestionsByCategory/question2.json"), null);
        request("api/addQuestions", POST, readJsonFromFile("getQuestionsByCategory/question3.json"), null);
        request("api/addQuestions", POST, readJsonFromFile("getQuestionsByCategory/question4.json"), null);

        //Getting all questions dor an invalid Category
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

        //Sending feedback to be able to get a sequence
        request("api/sendFeedback", POST, readJsonFromFile("sendFeedback/body.json"), null);

        //Getting a sequence
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

        //Sending feedback to be able to get a sequence
        request("api/sendFeedback", POST, readJsonFromFile("sendFeedback/body.json"), null);

        //Getting sequences for an invalid category
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
        //Adding a category
        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        //Adding products. Should return empty JSON object
        response = requestAddProducts("api/addProducts", new File("addProducts/files/file.csv"), "tvs");

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("addProducts/response.json"), response.asJson());

        //Verifying if the products were added
        response = request("api/productsByCategory", GET, null, Json.newObject().put("code", "tvs"));

        assert response != null;
        assertEquals(OK, response.getStatus());

        List<String> prices = response.asJson().findValuesAsText("price");

        assertEquals("TV LED Smart TV 55'' PANASONIC TX-55CX680", response.asJson().findValue("name").asText());
        assertNotEquals(prices.indexOf("1999"), -1);
        assertNotEquals(prices.indexOf("1129"), -1);
        assertNotEquals(prices.indexOf("1399"), -1);

        //TODO IMPROVE
    }

    @Test
    public void testAddProductsInvalidCategory() {
        //Adding a category
        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        //Adding products for an invalid category
        response = requestAddProducts("api/addProducts", new File("addProducts/files/file.csv"), "INVALID_CATEGORY");

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addProducts/responseInvalidCategory.json"), response.asJson());

        //verifying if products were not added
        response = request("api/productsByCategory", GET, null, Json.newObject().put("code", "tvs"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(Json.newArray(), response.asJson());
    }

    @Test
    public void testAddProductsMissingFile() {
        //Adding a category
        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        //Adding products with no file
        response = requestAddProducts("api/addProducts", null, "tvs");

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addProducts/responseMissingFile.json"), response.asJson());

        //verifying if products were not added
        response = request("api/productsByCategory", GET, null, Json.newObject().put("code", "tvs"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(Json.newArray(), response.asJson());
    }

    @Test
    public void testAddProductsMissingAttributeType() {
        //Adding a category
        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        //Adding products with no attribute types
        response = requestAddProducts("api/addProducts", new File("addProducts/files/fileMissingAttribute.csv"), "tvs");

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addProducts/responseMissingAttribute.json"), response.asJson());

        //verifying if products were not added
        response = request("api/productsByCategory", GET, null, Json.newObject().put("code", "tvs"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(Json.newArray(), response.asJson());
    }

    @Test
    public void testAddProductsInvalidAttributeType() {
        //Adding a category
        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        //Adding products with an invalid attribute type
        response = requestAddProducts("api/addProducts", new File("addProducts/files/fileInvalidAttribute.csv"), "tvs");

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addProducts/responseInvalidAttribute.json"), response.asJson());

        //verifying if products were not added
        response = request("api/productsByCategory", GET, null, Json.newObject().put("code", "tvs"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(Json.newArray(), response.asJson());
    }

    @Test
    public void testAddProductsExistingAttributeName() {
        //Adding categories
        request("api/addCategory", POST, Json.newObject().put("name", "First category").put("code", "cat1"), null);
        request("api/addCategory", POST, Json.newObject().put("name", "Second category").put("code", "cat2"), null);

        //Adding products to the first category
        response = requestAddProducts("api/addProducts", new File("addProducts/files/file.csv"), "cat1");

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("addProducts/response.json"), response.asJson());

        //Checking if the first category products were added
        response = request("api/productsByCategory", GET, null, Json.newObject().put("code", "cat1"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertNotEquals(Json.newArray(), response.asJson());

        //Adding products to the second category
        response = requestAddProducts("api/addProducts", new File("addProducts/files/file.csv"), "cat2");

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("addProducts/responseExistingAttributeName.json"), response.asJson());

        //Checking if the second category products were not added
        response = request("api/productsByCategory", GET, null, Json.newObject().put("code", "cat2"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(Json.newArray(), response.asJson());
    }

    //========================Removing==============================//

    @Test
    public void testRemoveProducts() {
        //Adding the category
        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        //Adding products
        requestAddProducts("api/addProducts", new File("removeProducts/products.csv"), "tvs");

        //Removing the products
        response = request("api/removeProducts", DELETE, readJsonFromFile("removeProducts/body.json"), null);

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("removeProducts/response.json"), response.asJson());

        //verifying if values were deleted
        response = request("api/productsByCategory", GET, null, readJsonFromFile("getProductsByCategory/parameters.json"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(Json.newArray(), response.asJson());
    }

    @Test
    public void testRemoveProductsInvalidCategory() {
        //Adding the category
        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        //Adding products
        requestAddProducts("api/addProducts", new File("removeProducts/products.csv"), "tvs");

        //Removing products with an invalid category
        response = request("api/removeProducts", DELETE, readJsonFromFile("removeProducts/bodyInvalidCategory.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("removeProducts/responseInvalidCategory.json"), response.asJson());

        //verifying if values were not deleted
        response = request("api/productsByCategory", GET, null, readJsonFromFile("getProductsByCategory/parameters.json"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertNotEquals(Json.newArray(), response.asJson());
    }

    @Test
    public void testRemoveProductsMissingFields() {
        //Adding the category
        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        //Adding products
        requestAddProducts("api/addProducts", new File("removeProducts/products.csv"), "tvs");

        //Removing products with no category
        response = request("api/removeProducts", DELETE, readJsonFromFile("removeProducts/bodyMissingFieldCategory.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("removeProducts/responseMissingFieldCategory.json"), response.asJson());

        //Removing products with no products
        response = request("api/removeProducts", DELETE, readJsonFromFile("removeProducts/bodyMissingFieldProducts.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("removeProducts/responseMissingFieldProducts.json"), response.asJson());

        //verifying if values were not deleted
        response = request("api/productsByCategory", GET, null, readJsonFromFile("getProductsByCategory/parameters.json"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertNotEquals(Json.newArray(), response.asJson());
    }

    @Test
    public void testRemoveProductsInvalidProducts() {
        //Adding the category
        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        //Adding products
        requestAddProducts("api/addProducts", new File("removeProducts/products.csv"), "tvs");

        //Adding products with invalid products
        response = request("api/removeProducts", DELETE, readJsonFromFile("removeProducts/bodyInvalidProducts.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("removeProducts/responseInvalidProducts.json"), response.asJson());

        //verifying if values were not deleted
        response = request("api/productsByCategory", GET, null, readJsonFromFile("getProductsByCategory/parameters.json"));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertNotEquals(Json.newArray(), response.asJson());
    }

    //========================GetAll================================//

    @Test
    public void testGetProductsByCategory() {
        //Adding the category
        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        //Adding products
        response = requestAddProducts("api/addProducts", new File("addProducts/files/file.csv"), "tvs");

        assert response != null;
        assertEquals(OK, response.getStatus());

        //Getting all products for the category
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
        //Adding the category
        request("api/addCategory", POST, Json.newObject().put("name", "Televisoes").put("code", "tvs"), null);

        //Adding products
        response = requestAddProducts("api/addProducts", new File("addProducts/files/file.csv"), "tvs");

        assert response != null;
        assertEquals(OK, response.getStatus());

        //Getting all products of an invalid category
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

    @Test
    public void testConfigAlgorithm() {
        populateDatabase();

        //Changing values of the algorithm. 'numberOfProducts' and 'numberOfQuestions' before this were 10 and 3, respectively
        response = request("api/configAlgorithm", POST, readJsonFromFile("configAlgorithm/body.json"), null);

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(readJsonFromFile("configAlgorithm/response.json"), response.asJson());

        //Verifying if the change in numberOfProducts and numberOfQuestions affect the response of getNextQuestion
        settingUpAttributes();

        request("api/addQuestions", POST, readJsonFromFile("addQuestions/body.json"), null);
        response = request("api/getNextQuestion", POST, readJsonFromFile("configAlgorithm/bodyFollowingQuestion.json"), null);

        assert response != null;
        assertEquals(OK, response.getStatus());

        JsonNode jsonResponse = response.asJson();

        assertEquals(2, jsonResponse.get("answers").findValuesAsText("code").size());
        assertEquals(2, jsonResponse.get("products").findValuesAsText("ean").size());
    }

    @Test
    public void testConfigAlgorithmInvalidFields() {
        //Configuring the algorithm with invalid alpha
        response = request("api/configAlgorithm", POST, readJsonFromFile("configAlgorithm/InvalidFields/bodyInvalidFieldAlpha.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("configAlgorithm/InvalidFields/responseInvalidFieldAlpha.json"), response.asJson());

        //Configuring the algorithm with invalid beta
        response = request("api/configAlgorithm", POST, readJsonFromFile("configAlgorithm/InvalidFields/bodyInvalidFieldBeta.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("configAlgorithm/InvalidFields/responseInvalidFieldBeta.json"), response.asJson());

        //Configuring the algorithm with invalid gamma
        response = request("api/configAlgorithm", POST, readJsonFromFile("configAlgorithm/InvalidFields/bodyInvalidFieldGamma.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("configAlgorithm/InvalidFields/responseInvalidFieldGamma.json"), response.asJson());

        //Configuring the algorithm with invalid numberOfProducts
        response = request("api/configAlgorithm", POST, readJsonFromFile("configAlgorithm/InvalidFields/bodyInvalidFieldNumberOfProducts.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("configAlgorithm/InvalidFields/responseInvalidFieldNumberOfProducts.json"), response.asJson());

        //Configuring the algorithm with invalid numberOfQuestions
        response = request("api/configAlgorithm", POST, readJsonFromFile("configAlgorithm/InvalidFields/bodyInvalidFieldNumberOfQuestions.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("configAlgorithm/InvalidFields/responseInvalidFieldNumberOfQuestions.json"), response.asJson());
    }

    @Test
    public void testConfigAlgorithmMissingFields() {
        //Configuring the algorithm with no alpha
        response = request("api/configAlgorithm", POST, readJsonFromFile("configAlgorithm/MissingFields/bodyMissingFieldAlpha.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("configAlgorithm/MissingFields/responseMissingFieldAlpha.json"), response.asJson());

        //Configuring the algorithm with no beta
        response = request("api/configAlgorithm", POST, readJsonFromFile("configAlgorithm/MissingFields/bodyMissingFieldBeta.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("configAlgorithm/MissingFields/responseMissingFieldBeta.json"), response.asJson());

        //Configuring the algorithm with no gamma
        response = request("api/configAlgorithm", POST, readJsonFromFile("configAlgorithm/MissingFields/bodyMissingFieldGamma.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("configAlgorithm/MissingFields/responseMissingFieldGamma.json"), response.asJson());

        //Configuring the algorithm with no numberOfProducts
        response = request("api/configAlgorithm", POST, readJsonFromFile("configAlgorithm/MissingFields/bodyMissingFieldNumberOfProducts.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("configAlgorithm/MissingFields/responseMissingFieldNumberOfProducts.json"), response.asJson());

        //Configuring the algorithm with no numberOfQuestions
        response = request("api/configAlgorithm", POST, readJsonFromFile("configAlgorithm/MissingFields/bodyMissingFieldNumberOfQuestions.json"), null);

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(readJsonFromFile("configAlgorithm/MissingFields/responseMissingFieldNumberOfQuestions.json"), response.asJson());
    }
}
