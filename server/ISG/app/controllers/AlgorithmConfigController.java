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
        if (jsonRequest.get("alfa") == null)
            return badRequest(ControllerUtils.missingField("alfa"));

        if (jsonRequest.get("beta") == null)
            return badRequest(ControllerUtils.missingField("beta"));

        if (jsonRequest.get("gamma") == null)
            return badRequest(ControllerUtils.missingField("gamma"));

        if (jsonRequest.get("numberOfProducts") == null)
            return badRequest(ControllerUtils.missingField("numberOfProducts"));

        Float alfa = (float) jsonRequest.get("alfa").asDouble();
        Float beta = (float) jsonRequest.get("beta").asDouble();
        Float gamma = (float) jsonRequest.get("gamma").asDouble();
        Integer numberOfProducts = jsonRequest.get("numberOfProducts").asInt();

        // Check if fields are valid:
        if (alfa < 0 && alfa > 1)
            return badRequest(ControllerUtils.generalError("INVALID_PARAMETER", "The parameter 'alfa' is invalid!"));

        if (beta < 0 && beta > 1)
            return badRequest(ControllerUtils.generalError("INVALID_PARAMETER","The parameter 'beta' is invalid!"));

        if (gamma < 0 && gamma > 1)
            return badRequest(ControllerUtils.generalError("INVALID_PARAMETER","The parameter 'gamma' is invalid!"));

        if (numberOfProducts <= 0)
            return badRequest(ControllerUtils.generalError("INVALID_PARAMETER","The parameter 'numberOfProducts' is invalid!"));

        AlgorithmParametersService service = new AlgorithmParametersService();
        AlgorithmParameters parameters = service.getAlgorithmParameters();

        parameters.setAlfa(alfa);
        parameters.setBeta(beta);
        parameters.setGamma(gamma);
        parameters.setNumberOfProducts(numberOfProducts);

        service.createOrUpdate(parameters);

        return ok(Json.newObject());
    }
}
