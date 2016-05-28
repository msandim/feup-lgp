package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import neo4j.models.nodes.AlgorithmParameters;
import neo4j.services.AlgorithmParametersService;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.Controller;
import utils.ControllerUtils;

/**
 * Created by Miguel on 27-05-2016.
 */
@BodyParser.Of(BodyParser.Json.class)
public class AlgorithmConfigController extends Controller
{
    public Result configAlgorithm()
    {
        JsonNode jsonRequest = request().body().asJson();

        // Check if fields exist:
        if (jsonRequest.get("alpha") == null)
            return badRequest(ControllerUtils.missingField("alpha"));

        if (jsonRequest.get("beta") == null)
            return badRequest(ControllerUtils.missingField("beta"));

        if (jsonRequest.get("gamma") == null)
            return badRequest(ControllerUtils.missingField("gamma"));

        if (jsonRequest.get("numberOfProducts") == null)
            return badRequest(ControllerUtils.missingField("numberOfProducts"));

        if (jsonRequest.get("numberOfQuestions") == null)
            return badRequest(ControllerUtils.missingField("numberOfProducts"));

        Float alpha = (float) jsonRequest.get("alpha").asDouble();
        Float beta = (float) jsonRequest.get("beta").asDouble();
        Float gamma = (float) jsonRequest.get("gamma").asDouble();
        Integer numberOfProducts = jsonRequest.get("numberOfProducts").asInt();
        Integer numberOfQuestions = jsonRequest.get("numberOfQuestions").asInt();

        System.out.println(alpha);
        System.out.println(alpha < 0);

        // Check if fields are valid:
        if (alpha < 0 || alpha > 1)
            return badRequest(ControllerUtils.generalError("INVALID_FIELD", "The field 'alpha' is invalid!"));

        if (beta < 0 || beta > 1)
            return badRequest(ControllerUtils.generalError("INVALID_FIELD","The field 'beta' is invalid!"));

        if (gamma < 0 || gamma > 1)
            return badRequest(ControllerUtils.generalError("INVALID_FIELD","The field 'gamma' is invalid!"));

        if (numberOfProducts <= 0)
            return badRequest(ControllerUtils.generalError("INVALID_FIELD","The field 'numberOfProducts' is invalid!"));

        if (numberOfQuestions <= 0)
            return badRequest(ControllerUtils.generalError("INVALID_FIELD","The field 'numberOfQuestions' is invalid!"));

        AlgorithmParametersService service = new AlgorithmParametersService();
        AlgorithmParameters parameters = service.getAlgorithmParameters();

        parameters.setAlpha(alpha);
        parameters.setBeta(beta);
        parameters.setGamma(gamma);
        parameters.setNumberOfProducts(numberOfProducts);
        parameters.setNumberOfQuestions(numberOfQuestions);

        service.createOrUpdate(parameters);

        return ok(Json.newObject());
    }
}
