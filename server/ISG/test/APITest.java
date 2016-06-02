import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import javax.inject.Inject;

import neo4j.Neo4jSessionFactory;
import org.neo4j.ogm.service.Components;

import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Source;
import akka.util.ByteString;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import play.libs.ws.WS;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;
import play.test.WithServer;
import play.mvc.Http.MultipartFormData.*;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletionStage;

@SuppressWarnings("unchecked")
public class APITest extends WithServer {

    private final static ObjectMapper mapper = new ObjectMapper();

    static WSResponse response;

    @Inject
    private WSClient ws;

    @BeforeClass
    public static void setupEmbeddedDatabase() {
        Components.configuration()
                .driverConfiguration()
                .setDriverClassName("org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver");
    }

    @Before
    public void setup() {
        Neo4jSessionFactory.getInstance().getNeo4jSession().query("MATCH (n) DETACH DELETE n;", Collections.EMPTY_MAP);
        Neo4jSessionFactory.getInstance().getNeo4jSession().query("CREATE (x:AlgorithmParameters {alpha: 0.333, beta: 0.333, gamma: 0.333, numberOfProducts: 10, numberOfQuestions: 3});", Collections.EMPTY_MAP);

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

    WSResponse request(String route, String type, JsonNode body, JsonNode parameters) {

        WSResponse response = null;
        try {
            WSRequest request = ws
                    .url("http://localhost:" + testServer.port() + "/" + route)
                    .setRequestTimeout(10000);

            prepareParameters(parameters, request);

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
                    stage = request
                            .setHeader("Content-type", "application/json")
                            .setHeader("Accept", "application/json")
                            .setBody(body)
                            .delete();
                    break;
                default:
                    return null;
            }

            response = stage.toCompletableFuture().get();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    WSResponse requestAddProducts(String route, File file, String category) {
        WSResponse response = null;
        try {
            WSRequest request = ws
                    .url("http://localhost:" + testServer.port() + "/" + route)
                    .setRequestTimeout(10000);

            CompletionStage<WSResponse> stage;

            DataPart dataPart = new DataPart("code", category);

            if (file != null) {
                Source<ByteString, ?> fileSource = FileIO.fromFile(file);
                FilePart<Source<ByteString, ?>> filePart = new FilePart<>("csv", "", "", fileSource);
                stage = request.post(Source.from(Arrays.asList(dataPart, filePart)));
            } else {
                stage = request.post(Source.from(Collections.singletonList(dataPart)));
            }

            response = stage.toCompletableFuture().get();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    private void prepareParameters(JsonNode parameters, WSRequest request) {
        if (parameters != null) {
            Map parametersMap = mapper.convertValue(parameters, Map.class);

            for (Object o : parametersMap.entrySet()) {
                Map.Entry thisEntry = (Map.Entry) o;
                Object key = thisEntry.getKey();
                Object value = thisEntry.getValue();
                request.setQueryParameter((String) key, (String) value);
            }
        }
    }

    JsonNode readJsonFromFile(String fileLocation) {
        try {
            return mapper.readValue(new File(fileLocation), JsonNode.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    void populateDatabase(){
        File file = new File("populateDB.cql");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (String line; (line = br.readLine()) != null; ) {
                Neo4jSessionFactory
                        .getInstance()
                        .getNeo4jSession()
                        .query(
                                line,
                                Collections.EMPTY_MAP
                        );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
