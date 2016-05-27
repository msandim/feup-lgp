package controllers;

import neo4j.models.nodes.AlgorithmParameters;
import neo4j.services.AlgorithmParametersService;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Controller;
import utils.ControllerUtils;

/**
 * Created by Miguel on 27-05-2016.
 */
public class AlgorithmConfigController extends Controller
{
    public Result configAlgorithm(Float alfa, Float beta, Float gamma, Integer numberOfProducts)
    {
        if (numberOfProducts <= 0)
            return badRequest(ControllerUtils.generalError("INVALID_PARAMETER","The parameter 'numberOfProducts' is invalid!"));

        if (alfa < 0 && alfa > 1)
            return badRequest(ControllerUtils.generalError("INVALID_PARAMETER","The parameter 'alfa' is invalid!"));

        if (beta < 0 && beta > 1)
            return badRequest(ControllerUtils.generalError("INVALID_PARAMETER","The parameter 'beta' is invalid!"));

        if (gamma < 0 && gamma > 1)
            return badRequest(ControllerUtils.generalError("INVALID_PARAMETER","The parameter 'gamma' is invalid!"));

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
