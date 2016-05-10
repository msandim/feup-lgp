package utils;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;

/**
 * Created by Miguel on 06-05-2016.
 */
public class ControllerUtils
{
    public static JsonNode missingField(String fieldName)
    {
        return Json.newObject().put("error", "MISSING_FIELD").put("msg", "The field '" + fieldName +"' is missing!");
    }

    public static JsonNode generalError(String code, String msg)
    {
        return Json.newObject().put("error", code).put("msg", msg);
    }
}
