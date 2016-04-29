package controllers;

import javax.inject.*;

import com.fasterxml.jackson.databind.JsonNode;
import neo4j.models.nodes.Category;
import neo4j.models.nodes.Question;
import neo4j.services.CategoryService;
import neo4j.services.QuestionService;

import java.util.*;

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
        QuestionService service = new QuestionService();
        Iterable<Question> questions = service.getQuestionsFromCategory(category);

        // Get random question:
        List<Question> questionList = new ArrayList<>();
        questions.forEach(questionList::add);
        Question randomQuestion = questionList.get(new Random().nextInt(questionList.size()));

        return ok(randomQuestion.toString() + category);
    }


    public Result retrieveAllQuestions()
    {
        QuestionService service = new QuestionService();


        Iterable<Question> res = service.findAll();


        return ok(res.toString());
    }

    public Result getQuestionByCategory(String category)
    {
        QuestionService service = new QuestionService();

        Iterable<Question> res = service.getQuestionsFromCategory(category);

        return ok(res.toString());
    }

    public Result retrieveQuestion(Long id)
    {
        QuestionService service = new QuestionService();

        Question res = service.find(id);

        return ok(res.toString());
    }

    public Result createOrUpdateQuestion(String text, String categoryCodename)
    {
        // Get the category:
        CategoryService categoryService = new CategoryService();
        Category category = categoryService.findByCodename(categoryCodename);

        if (category == null)
            return ok("no");

        // Falta retornos de jeito e de acordo com a especificacao

        QuestionService questionService = new QuestionService();
        Question question = new Question(text, category);

        return ok(questionService.createOrUpdate(question).getText());
    }

    public Result deleteQuestion(Long id)
    {
        QuestionService service = new QuestionService();

        service.delete(id);

        return ok(Long.toString(id));
    }

}
