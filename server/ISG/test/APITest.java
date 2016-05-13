import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import neo4j.Neo4jSessionFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.service.Components;
import play.libs.ws.WS;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;
import play.test.WithServer;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletionStage;

public class APITest extends WithServer {

    /** TODO
     * Test with:
     *  Bad number of parameters;
     *  Missing parameters;
     *  Bad syntax;
     */

    private final static ObjectMapper mapper = new ObjectMapper();
    protected WSResponse response;

    @BeforeClass
    public static void databaseConfiguration() {
        Components.configuration()
                .driverConfiguration()
                .setDriverClassName("org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver");
        //.setDriverClassName("org.neo4j.ogm.drivers.http.driver.HttpDriver")
        //.setURI("http://neo4j:neo@104.167.113.111:7474");
        //.setURI("http://neo4j:neo@localhost:7474");
    }

    @Before
    public void resetDatabase() {
        Neo4jSessionFactory.getInstance().getNeo4jSession().query("MATCH (n) DETACH DELETE n;", Collections.EMPTY_MAP);
    }

    protected WSResponse request(String route, String type, JsonNode body /*TODO maybe change to File*/, JsonNode parameters) throws Exception {
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

    protected JsonNode readJsonFromFile(String fileLocation) throws IOException {
        return mapper.readValue(new File(fileLocation), JsonNode.class);
    }
}
