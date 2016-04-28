import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.*;

import org.neo4j.ogm.service.Components;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.test.WithServer;

import java.io.File;
import java.util.concurrent.CompletionStage;

import static org.junit.Assert.*;

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


    private WSResponse getRequest(String route) throws Exception {
        String url = "http://localhost:" + testServer.port() + "/" + route;
        try (WSClient ws = WS.newClient(testServer.port())) {
            CompletionStage<WSResponse> stage = ws.url(url).get();
            return stage.toCompletableFuture().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private WSResponse postRequest(String route, JsonNode body) throws Exception {
        String url = "http://localhost:" + testServer.port() + "/" + route;
        try (WSClient ws = WS.newClient(testServer.port())) {
            CompletionStage<WSResponse> stage = ws.url(url).post(body);
            return stage.toCompletableFuture().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Test
    public void testCreateCategory() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        WSResponse response = postRequest("api/addc", mapper.readValue(new File("addc/Request1.json"), JsonNode.class));

        assert response != null;
        assertEquals(mapper.readValue(new File("addc/Response1.json"), JsonNode.class), response.asJson());

        //response = postRequest("api/addc", mapper.readValue(new File("addc/Request1.json"), JsonNode.class));
        response = getRequest("category/0"); //TODO how to check if category was added? This route is not on the Architecture Report / Acceptance Test Plan.

        assert response != null;
        assertEquals("Category{id=0, name=Televisoes} \n", response.getBody()); //TODO Change to JSON / Change to a proper response
        //assertEquals(mapper.readValue(new File("addc/Response2.json"), JsonNode.class), response.asJson());
    }

    @Test
    public void testCreateCategoryError() throws Exception { //TODO Can be compressed in 1 test for normal insert and error insert
        ObjectMapper mapper = new ObjectMapper();

        WSResponse response = postRequest("api/addc", mapper.readValue(new File("addc/Request1.json"), JsonNode.class));

        assert response != null;
        assertEquals(mapper.readValue(new File("addc/Response1.json"), JsonNode.class), response.asJson());

        response = postRequest("api/addc", mapper.readValue(new File("addc/Request1.json"), JsonNode.class));

        assert response != null;
        assertEquals(mapper.readValue(new File("addc/Response2.json"), JsonNode.class), response.asJson());
    }

    @Test
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
    public void testGetNextQuestion() throws Exception {
        WSResponse response = getRequest("get_all_categories");
        assert response != null;
        assertEquals("[]", response.getBody());
    }
}
