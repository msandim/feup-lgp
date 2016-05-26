import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.BeforeClass;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver;
import org.neo4j.ogm.service.Components;
import play.libs.ws.WS;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;
import play.test.WithServer;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletionStage;

@SuppressWarnings("Duplicates")
class APITest extends WithServer {

    /** TODO
     * Test with:
     *  Bad number of parameters;
     *  Missing parameters;
     *  Bad syntax;
     */

    private final static ObjectMapper mapper = new ObjectMapper();
    //EmbeddedDriver driver = new EmbeddedDriver(Components.configuration().driverConfiguration().setDriverClassName("org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver"));

    @BeforeClass
    public static void databaseConfiguration() {
        Components.setDriver(new EmbeddedDriver(Components.configuration().driverConfiguration().setDriverClassName("org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver")));
        //Components.configuration().driverConfiguration().setDriverClassName("org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver");
        //.setDriverClassName("org.neo4j.ogm.drivers.http.driver.HttpDriver")
        //.setURI("http://neo4j:neo@104.167.113.111:7474");
        //.setURI("http://neo4j:neo@localhost:7474");
    }

    @After
    public void resetDatabase() {
        EmbeddedDriver driver = (EmbeddedDriver) Components.driver();

        GraphDatabaseService databaseService = driver.getGraphDatabaseService();

        try (Transaction tx = databaseService.beginTx()) {
            databaseService.execute("MATCH (n) DETACH DELETE n");
            tx.success();
            tx.close();
        }
        //Neo4jSessionFactory.getInstance().getNeo4jSession().query("MATCH (n) DETACH DELETE n;", Collections.EMPTY_MAP);

        try {
            Thread.sleep(1500);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

    }

    WSResponse request(String route, String type, JsonNode body, JsonNode parameters) throws Exception {
        try {
            WSClient ws = WS.newClient(testServer.port());
            WSRequest request = ws.url("http://localhost:" + testServer.port() + "/" + route).setRequestTimeout(5000);
            //request.setRequestTimeout(5000);

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
            WSResponse result = stage.toCompletableFuture().get();
            ws.close();
            return result;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    WSResponse requestFile(String route, String type, File body, JsonNode parameters) throws Exception {
        try {
            String url = "http://localhost:" + testServer.port() + "/" + route;
            WSClient ws = WS.newClient(testServer.port());
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
            request.setHeader( "Content-Type", "multipart/form-data" );
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

    JsonNode readJsonFromFile(String fileLocation) throws IOException {
        return mapper.readValue(new File(fileLocation), JsonNode.class);
    }
}
