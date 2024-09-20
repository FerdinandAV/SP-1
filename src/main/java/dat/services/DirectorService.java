package dat.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dat.DTO.DirectorDTO;
import dat.DTO.MovieDTO;
import dat.daos.DirectorDAO;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DirectorService {
    public static final String API_KEY = System.getenv("API_KEY");
    private static final String BASE_URL_DIRECTOR = "https://api.themoviedb.org/3/movie/";
    private static final String BASE_URL_SEARCH_DIRECTOR = "https://api.themoviedb.org/3/person/";

    public static DirectorDTO getDirector(Long directorId) throws IOException, InterruptedException {
        // Build the request URL to fetch directors based on the movie ID and page
        String url = BASE_URL_SEARCH_DIRECTOR + directorId + "?api_key=" + API_KEY;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        // Send the HTTP request
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Log the response body for debugging purposes
        System.out.println("API Response: " + response.body());

        // Parse the response
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        DirectorDTO directorDTO = objectMapper.readValue(response.body(), DirectorDTO.class);

        //Return the fetched director
        return directorDTO;

    }

}
