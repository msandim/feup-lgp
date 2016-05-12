package controllers;

import algorithm.AlgorithmLogic;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import neo4j.models.edges.AnswerAttribute;
import neo4j.models.edges.QuestionEdge;
import neo4j.models.nodes.*;
import neo4j.services.*;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import utils.ControllerUtils;
import utils.MapUtils;

import javax.inject.Singleton;
import java.util.*;
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

            // Update the scores:
            if (!productService.updateScores(questionCode, answerCode, productScores))
                return badRequest(ControllerUtils.generalError("INVALID_QUESTION_ANSWER",
                        "One of the question ID or answer ID you supplied is not valid!"));

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
                //questionEdge. // TODO the variance this is missing!

                if (feedback == 1)
                    questionEdge.incNumberOfTimesGoodFeedback();

                // Update Edge:
                questionEdgeService.createOrUpdate(questionEdge, 0);
            }

            sequence.put(currentQuestion, currentAnswer);
            lastQuestion = currentQuestion;

            // COMMIT
        }

        return ok(Json.newObject());
    }


    @BodyParser.Of(BodyParser.Json.class)
    public Result createOrUpdateQuestion() {

        JsonNode jsonRequest = request().body().asJson();

        //detect missing or null values
        if(jsonRequest.get("category") == null)
            return badRequest(ControllerUtils.missingField("category"));
        if(jsonRequest.get("questions") == null)
            return badRequest(ControllerUtils.missingField("questions"));

        // Service initialization
        CategoryService categoryService = new CategoryService();
        QuestionService questionService = new QuestionService();
        QuestionEdgeService questionEdgeService = new QuestionEdgeService();
        AttributeService attrService = new AttributeService();

        // Get the category and verify if it exists
        String categoryCode = jsonRequest.findPath("category").asText();
        Category category = categoryService.findByCode(categoryCode);

        if (category == null)
            return badRequest(ControllerUtils.generalError("INVALID_CATEGORY","Category not found!"));

        //parse questions
        JsonNode questionsNode = jsonRequest.findPath("questions");
        Iterator<JsonNode> itQuestion = questionsNode.elements();

        //if field is present but has no content
        if(!itQuestion.hasNext())
            return badRequest(ControllerUtils.generalError("NO_QUESTIONS","Questions not found!"));

        //iterate through questions
        while (itQuestion.hasNext()) {

            JsonNode questionNode = itQuestion.next();

            String questionText = questionNode.findPath("text").asText();

            //create question object
            Question question = new Question(questionText);

            //add question to category
            question.setCategory(category);

            //parse answers
            JsonNode answersNode = questionNode.findPath("answers");

            if(answersNode == null)
                return badRequest(ControllerUtils.missingField("answers"));

            List<Answer> answers = new ArrayList<>();
            Iterator<JsonNode> itAnswer = answersNode.elements();

            if(!itAnswer.hasNext())
                return badRequest(ControllerUtils.generalError("NO_ANSWERS","Answers for the question not found!"));

            //iterate through answers
            while (itAnswer.hasNext()) {

                JsonNode answerNode = itAnswer.next();

                String answerText = answerNode.findValue("text").asText();

                //create answer object
                Answer answer = new Answer(answerText);

                JsonNode characteristicsNode = answerNode.findPath("attributes");

                if(characteristicsNode == null)
                    return badRequest(ControllerUtils.missingField("attributes"));

                List<AnswerAttribute> answerAttrs = new ArrayList<>();
                Iterator<JsonNode> itCharacteristics = characteristicsNode.elements();

                if(!itCharacteristics.hasNext())
                    return badRequest(ControllerUtils.generalError("NO_CHARACTERISTICS","Characteristics for the answer not found!"));

                //iterate through characteristics
                while (itCharacteristics.hasNext()) {

                    JsonNode characteristicNode = itCharacteristics.next();

                    //detecting missing fields
                    if(characteristicNode.findValue("name") == null)
                        return badRequest(ControllerUtils.missingField("characteristic name"));
                    if(characteristicNode.findValue("operator") == null)
                        return badRequest(ControllerUtils.missingField("characteristic operator"));
                    if(characteristicNode.findValue("value") == null)
                        return badRequest(ControllerUtils.missingField("characteristic value"));
                    if(characteristicNode.findValue("score") == null)
                        return badRequest(ControllerUtils.missingField("characteristic score"));

                    //verify if the attribute exists in the database
                    String characteristicName = characteristicNode.findValue("name").asText();
                    Attribute attr = attrService.findByName(characteristicName);

                    if (attr == null)
                        return badRequest(ControllerUtils.generalError("INVALID_ATTRIBUTE", "There is no attribute with this name: " + characteristicName));

                    //validate operator
                    String characteristicsOperator = characteristicNode.findValue("operator").asText();

                    if (!AnswerAttribute.Operators.isValid(characteristicsOperator))
                        return badRequest(ControllerUtils.generalError("INVALID_OPERATOR", "Operators must be equal to one of these: < <= > >= = !="));

                    //validate value
                    String characteristicsValue = characteristicNode.findValue("value").asText();

                    //validate attribute operator relation
                    if(attr.getType().equals(Attribute.Type.CATEGORICAL) && !AnswerAttribute.Operators.isValidForCategorical(characteristicsOperator))
                        return badRequest(ControllerUtils.generalError("INVALID_ATTRIBUTE_OPERATOR_RELATION", "The operator(" + characteristicsOperator + ") is not valid with the attribute " + characteristicName));

                    //validate attribute
                    if(attr.getType().equals(Attribute.Type.NUMERIC)) {
                        try {
                            float f = Float.parseFloat(characteristicsValue);

                        } catch (NumberFormatException nfe) {
                            return badRequest(ControllerUtils.generalError("INVALID_VALUE", "Value must be parsable to float"));
                        }
                    }

                    String chScore = characteristicNode.findValue("score").asText();

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
            List<Question> sameCategoryQuestions = questionService.findByCategoryCode(categoryCode, false);
            if(sameCategoryQuestions != null) {
                for (Question q: sameCategoryQuestions) {
                    if(!q.equals(question)) {
                        QuestionEdge questionEdge = new QuestionEdge(question, q);
                        questionEdgeService.createOrUpdate(questionEdge, 0);
                        questionEdge = new QuestionEdge(q, question);
                        questionEdgeService.createOrUpdate(questionEdge, 0);
                    }
                }
            }

            //adding answer to DB
            questionService.createOrUpdate(question, 2);
        }

        return ok(Json.newObject().put("Success", "Question successfully created"));
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
