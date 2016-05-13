package controllers;

import algorithm.AlgorithmLogic;
import algorithm.SequenceBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import neo4j.models.edges.QuestionEdge;
import neo4j.models.nodes.*;
import neo4j.services.*;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import utils.ControllerUtils;
import utils.MapUtils;
import utils.Statistics;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        //JsonNode blackListQuestions = jsonRequest.withArray("blacklist_questions");

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
                    return badRequest(ControllerUtils.generalError("INVALID_QUESTION_ANSWER",
                            "One of the question ID or answer ID you supplied is not valid!"));

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
        {
            nextQuestion = AlgorithmLogic.getFirstQuestion(category);

            // If we can't even retrieve 1 question, we must give an error:
            if (nextQuestion == null)
                return badRequest(ControllerUtils.generalError("NO_QUESTIONS",
                        "No questions available in this category!"));
        }

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
    public Result sendFeedback()
    {
        // *******************************************************************
        // ********************* Parsing and error handling ******************

        JsonNode jsonRequest = request().body().asJson();

        // ** Error handling **:
        if (jsonRequest.get("category") == null)
            return badRequest(ControllerUtils.missingField("category"));

        if (jsonRequest.get("feedback") == null)
            return badRequest(ControllerUtils.missingField("feedback"));

        if (jsonRequest.get("answers") == null || !jsonRequest.get("answers").isArray())
            return badRequest(ControllerUtils.missingField("answers"));

        // Get the parameters:
        String category = jsonRequest.get("category").asText();
        int feedback = jsonRequest.get("feedback").asInt(-1);
        List<JsonNode> answers = new ArrayList<>();
        jsonRequest.withArray("answers").forEach(answers::add);

        // ** More error handling **:
        if (feedback != 0 && feedback != 1)
            return badRequest(ControllerUtils.generalError("INVALID_FEEDBACK_VALUE",
                    "The value you supplied for the feedback is invalid (it must be either 0 or 1)!"));

        if (answers.isEmpty())
            return badRequest(ControllerUtils.generalError("EMPTY_ANSWER_LIST",
                    "The answer list you supplied does not have any answers!"));

        ProductService productService = new ProductService();
        CategoryService categoryService = new CategoryService();
        QuestionService questionService = new QuestionService();
        QuestionEdgeService questionEdgeService = new QuestionEdgeService();
        AnswerService answerService = new AnswerService();

        if (categoryService.findByCode(category) == null)
            return badRequest(ControllerUtils.generalError("INVALID_CATEGORY","Category not found!"));

        // *******************************************************************
        // ********************* Request Processing **************************

        Map<Question, Answer> sequence = new LinkedHashMap<>();
        Question lastQuestion = null;
        Map<Product, Float> productScores = productService.initializeProductScores(category);

        //for(JsonNode questionAnswer: answers)
        for(JsonNode questionAnswer: answers)
        {
            String questionCode = questionAnswer.get("question").asText();
            String answerCode = questionAnswer.get("answer").asText();

            if (questionCode == null)
                return badRequest(ControllerUtils.missingField("question"));

            if (answerCode == null)
                return badRequest(ControllerUtils.missingField("answer"));

            Float varianceBeforeUpdate = AlgorithmLogic.calculateScoreVariance(productScores);

            // Update the scores:
            if (!productService.updateScores(questionCode, answerCode, productScores))
                return badRequest(ControllerUtils.generalError("INVALID_QUESTION_ANSWER",
                        "One of the question ID or answer ID you supplied is not valid!"));

            Float varianceAfterUpdate = AlgorithmLogic.calculateScoreVariance(productScores);

            // BEGIN TRANSACTION

            Question currentQuestion = questionService.findByCode(questionCode);
            Answer currentAnswer = answerService.findByCode(questionCode, answerCode);

            if (currentQuestion == null || currentAnswer == null)
                return badRequest(ControllerUtils.generalError("INVALID_QUESTION_ANSWER",
                        "One of the question ID or answer ID you supplied is not valid!"));

            currentQuestion.incNumberOfTimesChosen();
            currentAnswer.incNumberOfTimesChosen();

            // Update Question and Answer::
            questionService.createOrUpdate(currentQuestion, 0);
            answerService.createOrUpdate(currentAnswer, 0);

            // If we're not in the first node, let's retrieve the edge between the last question and the current,
            // and update:
            if (lastQuestion != null)
            {
                QuestionEdge questionEdge =
                        questionEdgeService.getQuestionEdge(lastQuestion.getCode(), questionCode);

                if (questionEdge == null)
                    return badRequest(ControllerUtils.generalError("INVALID_SEQUENCE",
                            "The sequence you supplied is not valid!"));

                questionEdge.incNumberOfTimesChosen();
                questionEdge.incMeanVariance(varianceAfterUpdate / varianceBeforeUpdate);

                if (feedback == 1)
                    questionEdge.incNumberOfTimesGoodFeedback();

                // Update Edge:
                questionEdgeService.createOrUpdate(questionEdge, 0);
            }

            sequence.put(currentQuestion, currentAnswer);
            lastQuestion = currentQuestion;

            // COMMIT
        }

        // Build Sequence:
        SequenceBuilder.build(sequence);

        return ok(Json.newObject());
    }


    @BodyParser.Of(BodyParser.Json.class)
    public Result createOrUpdateQuestion() {

        // TODO mudar por causa dos ArrayLists

        /*

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

            //create question object
            Question question = new Question(questionText, category);

            //parse answers
            JsonNode answersNode = qtNode.findPath("answers");

            Set<Answer> answers = new HashSet<>();

            Iterator<JsonNode> itAn = answersNode.elements();

            //iterate through answers
            while (itAn.hasNext()) {

                JsonNode anNode = itAn.next();

                String answerText = anNode.findValue("text").asText();

                //create answer object
                Answer answer = new Answer(answerText);

                JsonNode characteristics = anNode.findPath("characteristics");

                Set<AnswerAttribute> answerAttrs = new HashSet<>();

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

        */

        return ok("Success");
    }

    /*
    public Result retrieveAllQuestions()
    {
        // Retrieve all the questions in the system:
        QuestionService service = new QuestionService();
        List<Question> questions = new ArrayList<>();
        service.findAll().forEach(questions::add);

        return ok(Json.toJson(questions));
    }
    */

    public Result getQuestionsByCategory(String code)
    {
        QuestionService questionService = new QuestionService();
        CategoryService categoryService = new CategoryService();

        if (categoryService.findByCode(code) == null)
            return badRequest(ControllerUtils.generalError("INVALID_CATEGORY", "Category not found!"));

        return ok(Json.toJson(questionService.findByCategoryCode(code, false)));
    }

    // TODO ver o que retorna se n existir a questao com este ID
    /*
    public Result retrieveQuestion(Long id)
    {
        QuestionService service = new QuestionService();
        Question question = service.find(id);

        return ok(Json.toJson(question));
    }
    */

    public Result deleteQuestion(Long id)
    {
        QuestionService service = new QuestionService();
        service.delete(id);
        return ok(Json.toJson(id));
    }

}
