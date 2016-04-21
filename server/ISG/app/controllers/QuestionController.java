package controllers;

import javax.inject.*;

import neo4j.models.Question;
import neo4j.services.QuestionService;
import neo4j.services.QuestionServiceImpl;
import java.util.Collections;
import java.util.Map;
import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import play.mvc.*;

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
        QuestionService service = new QuestionServiceImpl();

        Iterable<Question> questions = service.getQuestionsFromCategory();

        return ok(questions.toString());
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

        Iterable<Question> res = service.getQuestionsFromCategory();

        return ok(res.toString());
    }

    public Result retrieveQuestion(Long id)
    {
        QuestionService service = new QuestionServiceImpl();


        Question res = service.find(id);


        return ok(res.toString());
    }

    public Result createOrUpdateQuestion(String questionText)
    {
        QuestionService service = new QuestionServiceImpl();

        Question temp = new Question(questionText, "TV", "ROOM");
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
