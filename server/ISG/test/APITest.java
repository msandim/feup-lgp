import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import neo4j.Neo4jSessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.neo4j.ogm.service.Components;
import play.libs.ws.WS;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;
import play.test.WithServer;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletionStage;

public class APITest extends WithServer {

    private final static ObjectMapper mapper = new ObjectMapper();

    @Inject private WSClient ws;

    @BeforeClass
    public static void setUp() {
        Components.configuration()
                .driverConfiguration()
                .setDriverClassName("org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver");
    }

    @Before
    public void resetDatabase() {
        Neo4jSessionFactory.getInstance().getNeo4jSession().query("MATCH (n) DETACH DELETE n;", Collections.EMPTY_MAP);
        //TODO Test using Neo4jSessionFactory.getInstance().getNeo4jSession().query("CREATE (x:AlgorithmParameters {alfa: 0.333, beta: 0.333, gamma: 0.333, numberOfProducts: 10, numberOfQuestions: 3})", Collections.EMPTY_MAP); Add
        /*try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }*/
        ws = WS.newClient(testServer.port());
    }

    @After
    public void tearDown() {
        try {
            ws.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    WSResponse request(String route, String type, JsonNode body, JsonNode parameters) throws Exception {

        WSResponse response = null;
        try {

            WSRequest request = ws
                    .url("http://localhost:" + testServer.port() + "/" + route)
                    .setRequestTimeout(10000);

            if (parameters != null) {
                Map parametersMap = mapper.convertValue(parameters, Map.class);

                for (Object o : parametersMap.entrySet()) {
                    Map.Entry thisEntry = (Map.Entry) o;
                    Object key = thisEntry.getKey();
                    Object value = thisEntry.getValue();
                    request.setQueryParameter((String) key, (String) value);
                }
            }

            CompletionStage<WSResponse> stage;
            //CompletionStage<WSResponse> recoverPromise;
            switch (type) {
                case "GET":
                    stage = request.get();
                    /*recoverPromise = stage.toCompletableFuture().handle((result, error) -> {
                        if (error != null) {
                            return request.get().toCompletableFuture();
                        } else {
                            return CompletableFuture.completedFuture(result);
                        }
                    }).thenCompose(Function.identity());*/
                    break;
                case "POST":
                    stage = request.post(body);
                    /*recoverPromise = stage.toCompletableFuture().handle((result, error) -> {
                        if (error != null) {
                            return request.post(body).toCompletableFuture();
                        } else {
                            return CompletableFuture.completedFuture(result);
                        }
                    }).thenCompose(Function.identity());*/
                    break;
                case "PUT":
                    stage = request.put(body);
                    /*recoverPromise = stage.toCompletableFuture().handle((result, error) -> {
                        if (error != null) {
                            return request.put(body).toCompletableFuture();
                        } else {
                            return CompletableFuture.completedFuture(result);
                        }
                    }).thenCompose(Function.identity());*/
                    break;
                case "DELETE":
                    request.setHeader("Content-type","application/json");
                    request.setHeader("Accept","application/json");
                    request.setBody(body);
                    stage = request.delete();
                    /*recoverPromise = stage.toCompletableFuture().handle((result, error) -> {
                        if (error != null) {
                            return request.delete().toCompletableFuture();
                        } else {
                            return CompletableFuture.completedFuture(result);
                        }
                    }).thenCompose(Function.identity());*/
                    break;
                default:
                    return null;
            }

            /*try {
                Thread.sleep(250);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }*/

            //response = recoverPromise.toCompletableFuture().get();

            response = stage.toCompletableFuture().get();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    JsonNode readJsonFromFile(String fileLocation) throws IOException {
        return mapper.readValue(new File(fileLocation), JsonNode.class);
    }
}
