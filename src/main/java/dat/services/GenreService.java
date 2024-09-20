package dat.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dat.DTO.ActorDTO;
import dat.DTO.GenreDTO;
import dat.DTO.MovieDTO;
import dat.daos.GenreDAO;
import dat.entities.Genre;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class GenreService {

    public static final String API_KEY = System.getenv("API_KEY");

    public static GenreDTO getGenre(Long id) throws IOException, InterruptedException {
        // Build the request URL to fetch actors based on the movie ID and page
        String url = "https://api.themoviedb.org/3/genre/movie/list" + "?api_key=" + API_KEY;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        System.out.println(url);

        // Send the HTTP request
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Log the response body for debugging purposes
        System.out.println("API Response: " + response.body());

        // Parse the response
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        JsonNode genresNode = objectMapper.readTree(response.body()).get("genres");

        for (int i = 0; i < genresNode.size(); i++) {
            if (genresNode.get(i).get("id").asInt() == id) {
                GenreDTO genreDTO = objectMapper.treeToValue(genresNode.get(i), GenreDTO.class);
                return genreDTO;
            }
        }

        return null;
    }

}
