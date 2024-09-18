package dat.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dat.DTO.ActorDTO;
import dat.daos.ActorDAO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ActorService {

    public static final String API_KEY = System.getenv("API_KEY");
    private static final String BASE_URL_ACTOR = "https://api.themoviedb.org/3/movie/{movie_id}/credits";

    public static void FillDBUpWithActors() throws Exception {
        // Build the request URL to fetch popular actors
        String url = BASE_URL_ACTOR + "page=1&api_key=" + API_KEY;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        // Send the HTTP request
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Parse the response
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        JsonNode rootNode = objectMapper.readTree(response.body());


        // Assuming the response contains a field "results" with an array of actors
        JsonNode resultsNode = rootNode.get("results");

        // Process the first 20 actors
        for (int i = 0; i < Math.min(20, resultsNode.size()); i++) {
            JsonNode actorNode = resultsNode.get(i);
            ActorDTO actorDTO = objectMapper.treeToValue(actorNode, ActorDTO.class);

            // Save the actor to your database using ActorDAO
            ActorDAO.createActor(actorDTO);
        }
    }
}
