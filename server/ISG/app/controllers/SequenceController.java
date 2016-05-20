package controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import neo4j.services.CategoryService;
import neo4j.services.StartSequenceNodeService;
import play.libs.Json;
import play.mvc.Result;
import scala.Console;
import utils.ControllerUtils;

import static play.mvc.Results.badRequest;
import static play.mvc.Results.ok;

/**
 * Created by Miguel on 12-05-2016.
 */
public class SequenceController
{
    public Result getSequencesByCategory(String code)
    {
        StartSequenceNodeService sequenceService = new StartSequenceNodeService();
        CategoryService categoryService = new CategoryService();

        if (categoryService.findByCode(code) == null)
            return badRequest(ControllerUtils.generalError("INVALID_CATEGORY", "Category not found!"));

        ArrayNode sequences = Json.newArray();
        sequenceService.findByCategoryCode(code).forEach(x -> sequences.addPOJO(x.toJson()));
        return ok(sequences);
    }
}