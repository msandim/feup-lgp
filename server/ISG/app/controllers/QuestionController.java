package controllers;

import javax.inject.*;

import com.fasterxml.jackson.databind.JsonNode;
import neo4j.models.Question;
import neo4j.services.QuestionService;
import neo4j.services.QuestionServiceImpl;

import java.util.*;

import play.api.libs.json.JsPath;
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
    public Result getNextQuestion()
    {
        // Parse the parameters:
        JsonNode jsonRequest = request().body().asJson();
        String category = jsonRequest.findPath("category").asText();

        Console.print("NOME DA CATEGORIA: " + category + "\n");

        if (category == null)
            return badRequest("Missing parameter [category]");

        // Get all questions:
        QuestionService service = new QuestionServiceImpl();
        Iterable<Question> questions = service.getQuestionsFromCategory(category);

        // Get random question:
        List<Question> questionList = new ArrayList<>();
        questions.forEach(questionList::add);
        Question randomQuestion = questionList.get(new Random().nextInt(questionList.size()));

        return ok(randomQuestion.toString() + category);
    }


    public Result retrieveAllQuestions()
    {
        QuestionService service = new QuestionServiceImpl();


        Iterable<Question> res = service.findAll();


        return ok(res.toString());
    }

    public Result getQuestionByCategory(String category)
    {
        QuestionService service = new QuestionServiceImpl();

        Iterable<Question> res = service.getQuestionsFromCategory(category);

        return ok(res.toString());
    }

    public Result retrieveQuestion(Long id)
    {
        QuestionService service = new QuestionServiceImpl();


        Question res = service.find(id);


        return ok(res.toString());
    }

    public Result createOrUpdateQuestion(String questionText, String category)
    {
        QuestionService service = new QuestionServiceImpl();

        Question temp = new Question(questionText, category);
        service.createOrUpdate(temp);


        return ok(service.createOrUpdate(temp).getText());
    }

    public Result deleteQuestion(Long id)
    {
        QuestionService service = new QuestionServiceImpl();

        service.delete(id);


        return ok(Long.toString(id));
    }

}
