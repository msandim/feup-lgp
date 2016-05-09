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


        JsonNode jsonRequest = request().body().asJson();

        // Service initialization
        CategoryService categoryService = new CategoryService();
        QuestionService questionService = new QuestionService();
        AttributeService attrService = new AttributeService();

        // Get the category and verify if it exists

        String categoryCode = jsonRequest.findPath("category").asText();

        Category category = categoryService.findByCode(categoryCode);

        if (category == null)
            return badRequest(ControllerUtils.generalError("INVALID_CATEGORY","Category not found!"));

        //parse questions
        JsonNode questionsNode = jsonRequest.findPath("questions");

        Iterator<JsonNode> itQuestion = questionsNode.elements();

        //iterate through questions
        while (itQuestion.hasNext()) {

            JsonNode qtNode = itQuestion.next();

            String questionText = qtNode.findPath("text").asText();

            //create question object
            Question question = new Question(questionText);

            //parse answers
            JsonNode answersNode = qtNode.findPath("answers");

            List<Answer> answers = new ArrayList<>();

            Iterator<JsonNode> itAnswer = answersNode.elements();

            //iterate through answers
            while (itAnswer.hasNext()) {

                JsonNode answerNode = itAnswer.next();

                String answerText = answerNode.findValue("text").asText();

                //create answer object
                Answer answer = new Answer(answerText);

                JsonNode characteristics = answerNode.findPath("characteristics");

                List<AnswerAttribute> answerAttrs = new ArrayList<>();

                Iterator<JsonNode> itCharacteristics = characteristics.elements();

                //iterate through characteristics
                while (itCharacteristics.hasNext()) {

                    JsonNode characteristicsNode = itCharacteristics.next();

                    String characteristicsName = characteristicsNode.findValue("name").asText();

                    //verify if the attribute exists in the database

                    Attribute attr = attrService.findByName(characteristicsName);

                    if (attr == null)
                        return badRequest(ControllerUtils.generalError("INVALID_ATTRIBUTE", "There is no attribute with this name: " + characteristicsName));

                    //validate operator
                    String characteristicsOperator = characteristicsNode.findValue("operator").asText();

                    if (!AnswerAttribute.Operators.isValid(characteristicsOperator))
                        return badRequest(ControllerUtils.generalError("INVALID_OPERATOR", "Operators must be equal to one of these: < <= > >= = !="));

                    //validate value
                    String characteristicsValue = characteristicsNode.findValue("value").asText();

                    //validate attribute operator relation
                    if(!(attr.getType().equals(Attribute.Type.CATEGORICAL) && AnswerAttribute.Operators.isValidForCategorical(characteristicsOperator)))
                        return badRequest(ControllerUtils.generalError("INVALID_ATTRIBUTE_OPERATOR_RELATION", "This operator is not valid"));

                    //validate attribute
                    if(attr.getType().equals(Attribute.Type.NUMERIC)) {
                        try {
                            float f = Float.parseFloat(characteristicsValue);

                        } catch (NumberFormatException nfe) {
                            return badRequest(ControllerUtils.generalError("INVALID_SCORE", "Score must be parsable to float"));
                        }
                    }

                    String chScore = characteristicsNode.findValue("score").asText();

                    //verify if the score is a valid number
                    try {
                        float f = Float.parseFloat(chScore);
                        AnswerAttribute answerAttr = new AnswerAttribute(answer, attr, characteristicsOperator, characteristicsValue, f);
                        answerAttrs.add(answerAttr);

                    } catch (NumberFormatException nfe) {
                        return badRequest(ControllerUtils.generalError("INVALID_SCORE", "Score must be parsable to float"));
                    }
                }

                //setting answer attributes
                answer.setAttributes(answerAttrs);

                //add answer to list of answers
                answers.add(answer);
            }

            //connecting answers to question
            question.setAnswers(answers);

            //connecting same category questions
            question.setNextQuestions(questionService.findByCategoryCode(categoryCode, false));

            //adding answer to DB
            questionService.createOrUpdate(question, 2);
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
