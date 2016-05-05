import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import neo4j.Neo4jSessionFactory;
import org.junit.*;

import org.junit.runners.MethodSorters;
import org.neo4j.ogm.service.Components;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithServer;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import static play.test.Helpers.*;

import static org.junit.Assert.assertEquals;

public class BackOfficeAPITest extends WithServer {

    private ObjectMapper mapper = new ObjectMapper();
    private WSResponse response;

    @BeforeClass
    public static void databaseConfiguration() {
        Components.configuration()
                .driverConfiguration()
                .setDriverClassName("org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver");
        //.setDriverClassName("org.neo4j.ogm.drivers.http.driver.HttpDriver")
        //.setURI("http://neo4j:neo@104.167.113.111:7474");
        //.setURI("http://neo4j:neo@localhost:7474");
    }

    //@Before
    public void resetDatabase() {
        Neo4jSessionFactory.getInstance().getNeo4jSession().query("MATCH (n) DETACH DELETE n;", Collections.EMPTY_MAP);
    }

    private WSResponse request(String route, String type, JsonNode body, JsonNode parameters) throws Exception {
        String url = "http://localhost:" + testServer.port() + "/" + route;
        try (WSClient ws = WS.newClient(testServer.port())) {
            WSRequest request = ws.url(url);

            if (parameters != null) {
                ObjectMapper mapper = new ObjectMapper();
                Map result = mapper.convertValue(parameters, Map.class);

                for (Object o : result.entrySet()) {
                    Map.Entry thisEntry = (Map.Entry) o;
                    Object key = thisEntry.getKey();
                    Object value = thisEntry.getValue();
                    request.setQueryParameter((String) key, (String) value);
                }

                System.out.println(result);
            }

            CompletionStage<WSResponse> stage;
            switch (type) {
                case "GET":
                    stage = request.get();
                    break;
                case "POST":
                    stage = request.post(body);
                    break;
                case "PUT":
                    stage = request.put(body);
                    break;
                case "DELETE":
                    stage = request.delete();
                    break;
                default:
                    return null;
            }
            return stage.toCompletableFuture().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    //TODO Only used to test connection to embedded db and test function above
    @Test
    public void testRequest() throws Exception {

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/api/allCategories");

        Result result = route(request);
        assertEquals(OK, result.status());

        WSResponse response = request("api/allCategories", "GET", null, null);

        assert response != null;
        assertEquals(OK, response.getStatus());
    }

    /**
     * TODO add description
     * Uses the "api/addCategory" route and the "api/category/:id"
     *
     * @throws Exception
     */
    @Test
    public void testAddCategory() throws Exception {
        //Adding a category. Should return empty JSON object
        response = request("api/addCategory", "POST", null, mapper.readValue(new File("addCategory/parameters.json"), JsonNode.class));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(mapper.readValue(new File("addCategory/responseNormal.json"), JsonNode.class), response.asJson());

        //Checking if the category was really added
        response = request("api/category/0", "GET", null, null);//TODO Fix this request with the proper route according to the document

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals("Category{id=0, name=Televisoes} \n", response.getBody()); //TODO Change to JSON / Change to a proper response
    }

    @Test
    public void testAddCategoryError() throws Exception {
        //Adding a category. Should return empty JSON object
        WSResponse response = request("api/addCategory", "POST", null, mapper.readValue(new File("addCategory/parameters.json"), JsonNode.class));

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(mapper.readValue(new File("addCategory/responseNormal.json"), JsonNode.class), response.asJson());

        //Trying to add another category with the same name. It should return an error.
        response = request("api/addCategory", "GET", null, mapper.readValue(new File("addCategory/body.json"), JsonNode.class));

        assert response != null;
        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals(mapper.readValue(new File("addCategory/responseBadName.json"), JsonNode.class), response.asJson());
    }

    @Test
    @Ignore
    public void testAddQuestions() throws Exception {
        //TODO change JSON
        //TODO maybe change to not use other APIs and insert directly to the database
        request("api/addCategory", "POST", null, mapper.readValue(new File("addCategory/parameters.json"), JsonNode.class));

        //Adding a question. Should return empty JSON object
        response = request("api/addQuestions", "POST", mapper.readValue(new File("addQuestions/bodyNormal.json"), JsonNode.class), null);

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(mapper.readValue(new File("addQuestions/responseNormal.json"), JsonNode.class), response.asJson());
    }

    @Test
    @Ignore
    public void testAddProducts() throws Exception {
        //TODO change JSON
        //TODO maybe change to not use other APIs and insert directly to the database
        request("api/addCategory", "POST", null, mapper.readValue(new File("addCategory/parameters.json"), JsonNode.class));

        //Adding products. Should return empty JSON object
        response = request("api/addProducts", "POST", null, mapper.readValue(new File("addProducts/parameters.json"), JsonNode.class)); //TODO use file on body

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(mapper.readValue(new File("addQuestions/responseNormal.json"), JsonNode.class), response.asJson());
    }

    @Test
    @Ignore
    public void testRemoveCategory() throws Exception {

    }

    @Test
    @Ignore
    public void testRemoveCategoryError() throws Exception {

    }

    @Test
    @Ignore
    public void testRemoveProducts() throws Exception {

    }

    @Test
    @Ignore
    public void testRemoveQuestions() throws Exception {

    }

    @Test
    public void testGetAllCategories() throws Exception {
        //TODO maybe change to not use other APIs and insert directly to the database
        request("api/addCategory", "POST", null, Json.newObject()
                .put("name", "Televisoes")
                .put("code", "tv")
        );

        request("api/addCategory", "POST", null, Json.newObject()
                .put("name", "Máquinas de Lavar")
                .put("code", "maqla")
        );

        response = request("api/allCategories", "GET", null, null);

        assert response != null;
        assertEquals(OK, response.getStatus());
        assertEquals(mapper.readValue(new File("getAllCategories/response.json"), JsonNode.class), response.asJson());
    }

    @Test
    @Ignore
    public void testGetQuestionsByCategory() throws Exception {

    }

    @Test
    @Ignore
    public void testGetProductsByCategory() throws Exception {

    }

}
