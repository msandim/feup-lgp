package controllers;

import javax.inject.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import neo4j.models.edges.AnswerAttribute;
import neo4j.models.nodes.Answer;
import neo4j.models.nodes.Attribute;
import neo4j.models.nodes.Category;
import neo4j.models.nodes.Question;
import neo4j.services.AttributeService;
import neo4j.services.CategoryService;
import neo4j.services.QuestionService;

import java.util.*;

import org.apache.commons.lang3.math.NumberUtils;
import play.libs.Json;
import play.mvc.*;
import scala.Console;

@Singleton
public class QuestionController extends Controller {

    //public  QuestionService;

    /*@Inject
    public CountController(Counter counter) {
       this.counter = counter;
       // SessionFactory sessionFactory = new SessionFactory("neo4j.models");
        //Session session = sessionFactory.openSession();
        //this.quest = session.load(Question.class, new Long(193));

       // this.quest = session.load(Question.class, new Long(193));

    }*/

    @BodyParser.Of(BodyParser.Json.class)
    public Result getNextQuestion() {
        // Parse the parameters:
        JsonNode jsonRequest = request().body().asJson();
        String category = jsonRequest.findPath("category").asText();

        Console.print("NOME DA CATEGORIA: " + category + "\n");

        if (category == null)
            return badRequest("Missing parameter [category]");

        // Get all questions:
        QuestionService service = new QuestionService();
        Iterable<Question> questions = service.getQuestionsFromCategory(category);

        // Get random question:
        List<Question> questionList = new ArrayList<>();
        questions.forEach(questionList::add);
        Question randomQuestion = questionList.get(new Random().nextInt(questionList.size()));

        return ok(randomQuestion.toString() + category);
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

                    String chValue = chNode.findValue("value").asText();

                    // TODO Verificar relação entre os operadores e valores

                    String chScore = chNode.findValue("score").asText();

                    //verify if the score is a float
                    if (NumberUtils.isNumber(chScore)) {
                        result.put("Error", "Invalid answer score");
                        result.put("Message", "It must be a number");
                        return ok(result);
                    }

                    AnswerAttribute answerAttr = new AnswerAttribute(answer, attr, chOperator, chValue, chScore);
                    answerAttrs.add(answerAttr);
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

    public Result retrieveAllQuestions() {
        QuestionService service = new QuestionService();

        Iterable<Question> res = service.findAll();


        return ok(res.toString());
    }

    public Result getQuestionByCategory(String category) {
        QuestionService service = new QuestionService();

        Iterable<Question> res = service.getQuestionsFromCategory(category);

        return ok(res.toString());
    }

    public Result retrieveQuestion(Long id) {
        QuestionService service = new QuestionService();

        Question res = service.find(id);

        return ok(res.toString());
    }

    public Result deleteQuestion(Long id) {
        QuestionService service = new QuestionService();

        service.delete(id);

        return ok(Long.toString(id));
    }

}
