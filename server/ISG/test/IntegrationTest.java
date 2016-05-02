import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.*;

import org.neo4j.ogm.service.Components;
import play.libs.ws.WS;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithServer;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import play.mvc.Http.RequestBuilder;
import static play.test.Helpers.*;

import static org.junit.Assert.assertEquals;

public class IntegrationTest extends WithServer {

    @BeforeClass
    public static void before() {
        Components.configuration()
                .driverConfiguration()
                .setDriverClassName("org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver");
        //.setDriverClassName("org.neo4j.ogm.drivers.http.driver.HttpDriver")
        //.setURI("http://neo4j:neo@104.167.113.111:7474");
        //.setURI("http://neo4j:neo@localhost:7474");
    }

    private WSResponse request(String route, String type, JsonNode body, JsonNode parameters) throws Exception {
        String url = "http://localhost:" + testServer.port() + "/" + route;
        try (WSClient ws = WS.newClient(testServer.port())) {
            WSRequest request = ws.url(url);

            if(parameters != null){
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
            switch (type){
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

    @Test
    public void testRequest() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        WSResponse response = request("api/allCategories", "GET", null, null);

        assert response != null;
        assertEquals(OK, response.getStatus());
    }

    @Test
    public void testCreateCategory() throws Exception {
        /*RequestBuilder request = new RequestBuilder()
                .method(GET)
                .uri("/api/addCategory");

        Result result = route(request);
        assertEquals(BAD_REQUEST, result.status());*/

        ObjectMapper mapper = new ObjectMapper();

        //Adding a category. Should return empty JSON object
        WSResponse response = request("api/addCategory", "GET", null, mapper.readValue(new File("addc/Request1.json"), JsonNode.class));

        assert response != null;
        //assertEquals(mapper.readValue(new File("addc/Response1.json"), JsonNode.class), response.asJson()); //Actual response
        assertEquals(OK, response.getStatus()); //Current response

        //Checking if the category was really added
        response = request("api/category/0", "GET", null, null);//TODO how to check if category was added? This route is not on the Architecture Report / Acceptance Test Plan.

        assert response != null;
        assertEquals("Category{id=0, name=Televisoes} \n", response.getBody()); //TODO Change to JSON / Change to a proper response

        //Trying to add another category with the same name. It should return an error.
        response = request("api/addCategory", "GET", null, mapper.readValue(new File("addc/Request1.json"), JsonNode.class));

        assert response != null;
        assertEquals(OK, response.getStatus());
        //assertEquals(mapper.readValue(new File("addc/Response2.json"), JsonNode.class), response.asJson());
    }

    //@Test
    public void testCreateCategoryError() throws Exception { //TODO Can be compressed in 1 test for normal insert and error insert
        ObjectMapper mapper = new ObjectMapper();

        WSResponse response = request("api/addCategory", "GET", null, mapper.readValue(new File("addc/Request1.json"), JsonNode.class));

        assert response != null;
        assertEquals(mapper.readValue(new File("addc/Response1.json"), JsonNode.class), response.asJson());

        response = request("api/addCategory", "GET", null, mapper.readValue(new File("addc/Request1.json"), JsonNode.class));

        assert response != null;
        assertEquals(mapper.readValue(new File("addc/Response2.json"), JsonNode.class), response.asJson());
    }


    /*@Test //TODO routes don't exist
    public void testRemoveCategory() throws Exception { //TODO verify what happens when the category has questions or products
        ObjectMapper mapper = new ObjectMapper();

        WSResponse response = postRequest("api/addc", mapper.readValue(new File("addc/Request1.json"), JsonNode.class));

        assert response != null;
        assertEquals(mapper.readValue(new File("addc/Response1.json"), JsonNode.class), response.asJson());

        response = postRequest("api/removec", mapper.readValue(new File("removec/Request1.json"), JsonNode.class));

        assert response != null;
        assertEquals(mapper.readValue(new File("remvec/Response1.json"), JsonNode.class), response.asJson());
    }

    @Test
    public void testRemoveCategoryError() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        WSResponse response = postRequest("api/addc", mapper.readValue(new File("addc/Request1.json"), JsonNode.class));

        assert response != null;
        assertEquals(mapper.readValue(new File("addc/Response1.json"), JsonNode.class), response.asJson());

        response = postRequest("api/removec", mapper.readValue(new File("removec/Request2.json"), JsonNode.class));

        assert response != null;
        assertEquals(mapper.readValue(new File("remvec/Response2.json"), JsonNode.class), response.asJson());
    }

    @Test
    public void testGetNextQuestion() throws Exception { //TODO change to post
        WSResponse response = getRequest("api/getNextQuestion");
        assert response != null;
        assertEquals("[]", response.getBody());
    }*/
}
