import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import filters.AccessControlFilter;
import neo4j.Neo4jSessionFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.config.DriverConfiguration;
import org.neo4j.ogm.service.Components;
import play.libs.ws.*;
import play.libs.ws.ahc.AhcWSClient;
import play.test.TestServer;
import play.test.WithServer;

import javax.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static play.test.Helpers.*;

@SuppressWarnings("Duplicates")
public class APITest extends WithServer {

    /**
     * TODO
     * Test with:
     * Bad number of parameters;
     * Missing parameters;
     * Bad syntax;
     */

    private static Configuration configuration = new Configuration();
    private static DriverConfiguration driverConfiguration = new DriverConfiguration(configuration);

    private final static ObjectMapper mapper = new ObjectMapper();
    @Inject
    private WSClient ws;
    @Inject
    Materializer materializer;

    int i = 0;


    @BeforeClass
    public static void setUp() {
        driverConfiguration.setDriverClassName("org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver");
        Components.configure(configuration);
    }

    @Before
    public void resetDatabase() {
        Neo4jSessionFactory.getInstance().getNeo4jSession().query("MATCH (n) DETACH DELETE n;", Collections.EMPTY_MAP);

        /*try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }*/
        ws = WS.newClient(testServer.port());
    }

    @After
    public void tearDown() throws IOException {
        ws.close();
    }

    WSResponse request(String route, String type, JsonNode body, JsonNode parameters) throws Exception {

        WSResponse response = null;
        try {

            WSRequest request = ws
                    .url("http://localhost:" + testServer.port() + "/" + route)
                    .setRequestTimeout(2000);

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
            CompletionStage<WSResponse> recoverPromise;
            switch (type) {
                case "GET":
                    stage = request.get();
                    recoverPromise = stage.toCompletableFuture().handle((result, error) -> {
                        if (error != null) {
                            return request.get().toCompletableFuture();
                        } else {
                            return CompletableFuture.completedFuture(result);
                        }
                    }).thenCompose(Function.identity());
                    break;
                case "POST":
                    stage = request.post(body);
                    recoverPromise = stage.toCompletableFuture().handle((result, error) -> {
                        if (error != null) {
                            return request.post(body).toCompletableFuture();
                        } else {
                            return CompletableFuture.completedFuture(result);
                        }
                    }).thenCompose(Function.identity());
                    break;
                case "PUT":
                    stage = request.put(body);
                    recoverPromise = stage.toCompletableFuture().handle((result, error) -> {
                        if (error != null) {
                            return request.put(body).toCompletableFuture();
                        } else {
                            return CompletableFuture.completedFuture(result);
                        }
                    }).thenCompose(Function.identity());
                    break;
                case "DELETE":
                    stage = request.delete();
                    recoverPromise = stage.toCompletableFuture().handle((result, error) -> {
                        if (error != null) {
                            return request.delete().toCompletableFuture();
                        } else {
                            return CompletableFuture.completedFuture(result);
                        }
                    }).thenCompose(Function.identity());
                    break;
                default:
                    return null;
            }

            try {
                Thread.sleep(250);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            response = recoverPromise.toCompletableFuture().get();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    JsonNode readJsonFromFile(String fileLocation) throws IOException {
        return mapper.readValue(new File(fileLocation), JsonNode.class);
    }
}
