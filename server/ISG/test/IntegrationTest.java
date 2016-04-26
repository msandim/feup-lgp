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
        JsonNode rootNode = mapper.readValue(new File("addc/Request1.json"), JsonNode.class);

        WSResponse response = postRequest("api/addc", rootNode);

        assert response != null;
        assertEquals("{}", response.getBody()); //TODO: Response should be {}

        response = getRequest("category/0");

        assert response != null;
        assertEquals("Category{id=0, name=Televisoes} \n", response.getBody()); //TODO change to JSON
        //assertEquals(rootNode, response.asJson());
    }

    @Test
    public void testGetNextQuestion() throws Exception {
        WSResponse response = getRequest("get_all_categories");
        assert response != null;
        assertEquals("[]", response.getBody());
    }
}
