package algorithm;

import neo4j.models.edges.QuestionEdge;
import neo4j.models.nodes.AlgorithmParameters;
import neo4j.models.nodes.Answer;
import neo4j.models.nodes.Product;
import neo4j.models.nodes.Question;
import neo4j.services.AlgorithmParametersService;
import neo4j.services.ProductService;
import neo4j.services.QuestionEdgeService;
import neo4j.services.QuestionService;
import scala.Console;
import utils.RandomCollection;
import utils.Statistics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Miguel on 06-05-2016.
 */
public class QuestionPicker
{
    public static Question getFirstQuestion(String category)
    {
        // Get all questions:
        QuestionService service = new QuestionService();
        List<Question> questions = service.findByCategoryCode(category, true);

        // If we don't have more questions in this category:
        if (questions.isEmpty())
            return null;

        RandomCollection<Question> cumulativeRandom = new RandomCollection<>();
        questions.forEach(question ->
        {
            Float heuristicValue = getQuestionHeuristicValue(category, question);
            Console.println("First Question * Heuristic for: " + question + " is " + heuristicValue);
            cumulativeRandom.add(heuristicValue, question);
        });

        // Return a random question, proportional to its weight:
        return cumulativeRandom.next();
    }

    public static Question getNextQuestion(String category, List<String> answeredQuestionCodes)
    {
        AlgorithmParametersService algorithmService = new AlgorithmParametersService();
        QuestionEdgeService questionEdgeService = new QuestionEdgeService();

        // Get algorithm parameters:
        AlgorithmParameters parameters = algorithmService.getAlgorithmParameters();

        // Get all the question connections from the current question:
        String lastQuestionCode = answeredQuestionCodes.get(answeredQuestionCodes.size() - 1);
        List<QuestionEdge> questionEdges = questionEdgeService.getNextQuestions(lastQuestionCode);

        // Remove the questions connections already answered:
        removeAnsweredQuestions(questionEdges, answeredQuestionCodes);

        // If we don't have more questions in this category:
        if (questionEdges.isEmpty())
            return null;

        // Calculate heuristic for each value:
        RandomCollection<Question> cumulativeRandom = new RandomCollection<>();
        questionEdges.forEach(questionEdge ->
        {
            Float heuristicValue =
                    (parameters.getAlpha() * getQuestionHeuristicValue(category, questionEdge.getNextQuestion()))
                    + (parameters.getBeta() * getVarianceGain(questionEdge))
                    + (parameters.getGamma() * getGoodSequenceRatio(questionEdge));

            Console.print("Question heuristic: " + getQuestionHeuristicValue(category, questionEdge.getNextQuestion()) + " ");
            Console.println("NextQuestion * Heuristic for: " + questionEdge.getNextQuestion() + " is " + heuristicValue);
            cumulativeRandom.add(heuristicValue, questionEdge.getNextQuestion());
        });

        return cumulativeRandom.next();
    }

    private static void removeAnsweredQuestions(List<QuestionEdge> questionEdges, List<String> answeredQuestionCodes)
    {
        for(String answeredQuestionCode: answeredQuestionCodes)
        {
            // Remove the edges that point out to answers already answered:
            for(Iterator<QuestionEdge> iterator = questionEdges.iterator(); iterator.hasNext();)
            {
                if (iterator.next().getNextQuestion().getCode().equals(answeredQuestionCode))
                {
                    iterator.remove();
                    break;
                }
            }
        }
    }

    private static Float getQuestionHeuristicValue(String category, Question question)
    {
        ProductService service = new ProductService();
        int totalNumberOfProducts = (int) (long) service.getNumberOfProductsByCategory(category);

        Float metricResult = (float) 0.0;

        for(Answer answer: question.getAnswers())
        {
            Float frequency = getFrequency(question, answer);
            Float productRatio = totalNumberOfProducts == 0 ? 0 :
                    ((float) service.getNumProductsAffected(answer)) / totalNumberOfProducts;
            Float mediumScore = service.getMediumScore(answer);

            if (productRatio > 1)
                Console.println("******* ERROR PRODUCT RATIO ABOVE 1 ***************");

            // If the productRatio is 0, then the part to add from this answer is 0 because it doesn't affect any product!
            if (productRatio != 0)
                metricResult += frequency * (1 - productRatio) * mediumScore;
        }

        return (float) (metricResult + 0.00001); // Add just bit to never have an heuristic with value zero
    }

    private static Float getFrequency(Question question, Answer answer)
    {
        List<Answer> answers = question.getAnswers();

        if (question.getNumberOfTimesChosen() == 0) {
            if(answers.size() == 0)
                return (float) 0;
            else {
                return (float) 1/answers.size();
            }
        }
        else
            return ((float) answer.getNumberOfTimesChosen()) / question.getNumberOfTimesChosen();
    }

    private static Float getVarianceGain(QuestionEdge questionEdge)
    {
        return questionEdge.getVarianceGainMean();
    }

    private static Float getGoodSequenceRatio(QuestionEdge questionEdge)
    {
        if (questionEdge.getNumberOfTimesChosen() == 0)
            return (float) 0;
        else
            return ((float) questionEdge.getNumberOfTimesGoodFeedback()) / questionEdge.getNumberOfTimesChosen();
    }

    public static Float calculateScoreVariance(Map<Product, Float> scores)
    {
        return Statistics.getVariance(new ArrayList<>(scores.values()));
    }
}
