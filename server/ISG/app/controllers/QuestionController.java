package controllers;

import algorithm.AlgorithmLogic;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import neo4j.models.edges.AnswerAttribute;
import neo4j.models.nodes.*;
import neo4j.services.AttributeService;
import neo4j.services.CategoryService;
import neo4j.services.ProductService;
import neo4j.services.QuestionService;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import utils.ControllerUtils;
import utils.MapUtils;

import javax.inject.Singleton;
import java.util.*;

@Singleton
public class QuestionController extends Controller {

    @BodyParser.Of(BodyParser.Json.class)
    public Result getNextQuestion()
    {
        // *******************************************************************
        // ********************* Parsing and error handling ******************

        JsonNode jsonRequest = request().body().asJson();

        // ** Sintatic Error handling **:
        if (jsonRequest.get("category") == null)
            return badRequest(ControllerUtils.missingField("category"));

        if (jsonRequest.get("answers") == null || !jsonRequest.get("answers").isArray())
            return badRequest(ControllerUtils.missingField("answers"));

        //if (jsonRequest.get("blacklist_questions") == null || jsonRequest.get("blacklist_questions").isArray())
        //    return badRequest(ControllerUtils.missingField("blacklist_questions"));

        // Get the parameters:
        String category = jsonRequest.get("category").asText();

        JsonNode answers = jsonRequest.withArray("answers");
        JsonNode blackListQuestions = jsonRequest.withArray("blacklist_questions");

        ProductService productService = new ProductService();
        CategoryService categoryService = new CategoryService();

        // **Semantic Error handling **:
        if (categoryService.findByCode(category) == null)
            return badRequest(ControllerUtils.generalError("INVALID_CATEGORY","Category not found!"));


        // *******************************************************************
        // ********************* Request Processing **************************

        // Initialize any necessary return values:
        Question nextQuestion = null;
        List<Map.Entry<Product, Float>> orderedProductScores = new ArrayList<>();

        // Initialize local variables:
        List<String> answeredQuestionCodes = new ArrayList<>();

        // If we're not seeking the first question
        if (answers.elements().hasNext())
        {
            Map<Product, Float> productScores = productService.initializeProductScores(category);

            for(JsonNode questionAnswer: answers)
            {
                String questionCode = questionAnswer.get("question").asText();
                String answerCode = questionAnswer.get("answer").asText();

                if (questionCode == null)
                    return badRequest(ControllerUtils.missingField("question"));

                if (answerCode == null)
                    return badRequest(ControllerUtils.missingField("answer"));

                // Update the scores:
                if (!productService.updateScores(questionCode, answerCode, productScores))
                    return badRequest(ControllerUtils.generalError("INVALID_QUESTION_ANSWER", "One of the question ID or answer ID you supplied is not valid!"));

                // Add the question code the answered question list:
                answeredQuestionCodes.add(questionCode);
            }

            // Retrieve the top X products with higher score:
            orderedProductScores = MapUtils.orderByValueDecreasing(productScores);

            // Return the next question:
            nextQuestion = AlgorithmLogic.getNextQuestion(category, answeredQuestionCodes);
        }
        // If we're seeking the first question:
        else
            nextQuestion = AlgorithmLogic.getFirstQuestion(category);

        // *******************************************************************
        // *********************** Request Return ****************************

        ObjectNode result = Json.newObject();
        ObjectNode questionNode = result.putObject("question");
        ArrayNode answersNode = result.putArray("answers");

        // If the algorithm didn't end, let's present the next question:
        if (nextQuestion != null)
        {
            questionNode.put("code", nextQuestion.getCode()).put("text", nextQuestion.getText());
            nextQuestion.getAnswers().forEach(
                    answer -> answersNode.addObject()
                            .put("code", answer.getCode())
                            .put("text", answer.getText()));
        }

        ArrayNode products = result.putArray("products");
        orderedProductScores.forEach(x -> products.addObject()
                .put("EAN", x.getKey().getEAN())
                .put("name", x.getKey().getName())
                .put("score", x.getValue()));

        return ok(result);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result createOrUpdateQuestion() {

        //return json message
        ObjectNode result = Json.newObject();

        // Get the category and verify if it exists
        CategoryService categoryService = new CategoryService();

        JsonNode jsonRequest = request().body().asJson();

        String catCode = jsonRequest.findPath("category").asText();

        Category category = categoryService.findByCode(catCode);

        if (category == null) {
            result.put("Error", "Invalid Category");
            result.put("Message", "There is no category with this code: " + catCode);
            return ok(result);
        }

        //parse questions
        JsonNode questionsNode = jsonRequest.findPath("questions");

        Iterator<JsonNode> itQt = questionsNode.elements();

        //iterate through questions
        while (itQt.hasNext()) {

            JsonNode qtNode = itQt.next();

            String questionText = qtNode.findPath("text").asText();
            //TODO ligar à categoria e às perguntas da categoria
            //create question object
            Question question = new Question(questionText);

            //parse answers
            JsonNode answersNode = qtNode.findPath("answers");

            List<Answer> answers = new ArrayList<>();

            Iterator<JsonNode> itAn = answersNode.elements();

            //iterate through answers
            while (itAn.hasNext()) {

                JsonNode anNode = itAn.next();

                String answerText = anNode.findValue("text").asText();

                //create answer object
                Answer answer = new Answer(answerText);

                JsonNode characteristics = anNode.findPath("characteristics");

                List<AnswerAttribute> answerAttrs = new ArrayList<>();

                Iterator<JsonNode> itCh = characteristics.elements();

                //iterate through characteristics
                while (itCh.hasNext()) {

                    JsonNode chNode = itCh.next();

                    String chName = chNode.findValue("name").asText();

                    //verify if the attribute exists in the database

                    AttributeService attrService = new AttributeService();

                    Attribute attr = attrService.findByName(chName);

                    if (attr == null) {
                        result.put("Error", "Invalid Attribute");
                        result.put("Message", "There is no attribute with this name: " + chName);
                        return ok(result);
                    }

                    String chOperator = chNode.findValue("operator").asText();

                    if (!AnswerAttribute.Operators.isValid(chOperator)) {
                        result.put("Error", "Invalid operator");
                        result.put("Message", "It must be equal one of these: < <= > >= = !=");
                        return ok(result);
                    }

                    String chValue = chNode.findValue("value").asText();

                    // TODO Verificar relação entre os operadores e valores

                    String chScore = chNode.findValue("score").asText();

                    //verify if the score is a number
                    try {
                        float f = Float.parseFloat(chScore);
                        AnswerAttribute answerAttr = new AnswerAttribute(answer, attr, chOperator, chValue, f);
                        answerAttrs.add(answerAttr);

                    } catch (NumberFormatException nfe) {
                        result.put("Error", "Invalid answer score");
                        result.put("Message", "It must be a number");
                        return ok(result);
                    }
                }

                //setting answer attributes
                answer.setAttributes(answerAttrs);

                //add answer to list of answers
                answers.add(answer);
            }

            //connecting answers to question
            question.setAnswers(answers);

            //adding answer to DB
            QuestionService service = new QuestionService();
            service.createOrUpdate(question, 2);
        }

        return ok("Success");
    }

    public Result retrieveAllQuestions()
    {
        // Retrieve all the questions in the system:
        QuestionService service = new QuestionService();
        List<Question> questions = new ArrayList<>();
        service.findAll().forEach(questions::add);

        return ok(Json.toJson(questions));
    }

    public Result getQuestionByCategory(String code)
    {
        QuestionService service = new QuestionService();
        return ok(Json.toJson(service.findByCategoryCode(code, false)));
    }

    // TODO ver o que retorna se n existir a questao com este ID
    public Result retrieveQuestion(Long id)
    {
        QuestionService service = new QuestionService();
        Question question = service.find(id);

        return ok(Json.toJson(question));
    }

    public Result deleteQuestion(Long id)
    {
        QuestionService service = new QuestionService();
        service.delete(id);
        return ok(Json.toJson(id));
    }

}
