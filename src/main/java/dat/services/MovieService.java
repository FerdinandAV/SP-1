package dat.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dat.DTO.MovieDTO;
import dat.daos.MovieDAO;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MovieService {

    public static final String API_KEY = System.getenv("API_KEY");
    private static final String BASE_URL_MOVIE = "https://api.themoviedb.org/3/movie/";
    public static final String BASE_URL_MOVIE_Danish = "https://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&with_origin_country=DK";

    public static MovieDTO getMovieById(String id) throws Exception, InterruptedIOException {

        // // Build the request URL with the movie ID and API key

        String url = BASE_URL_MOVIE_Danish + id + "&page="+ "?api_key=" + API_KEY;


        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        // Now you Send the HTTP request
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        JsonNode rootNode = objectMapper.readTree(response.body());

        MovieDTO movie = objectMapper.treeToValue(rootNode, MovieDTO.class);
        return movie;
    }



    public static void FillDBUpLast5yearsDanish(String id) throws IOException, InterruptedException {
        // Calculate dates for the last 5 years
        LocalDate currentDate = LocalDate.now();
        LocalDate fiveYearsAgo = currentDate.minusYears(5);

        // Format dates to YYYY-MM-DD
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String currentDateString = currentDate.format(formatter);
        String fiveYearsAgoString = fiveYearsAgo.format(formatter);

        // Build the request URL to fetch Danish movies from the last 5 years
        String url = BASE_URL_MOVIE_Danish +
                "&primary_release_date.gte=" + fiveYearsAgoString +
                "&primary_release_date.lte=" + currentDateString +
                "&page=1&api_key=" + API_KEY;

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

        // Assuming the response contains a field "results" with an array of movies
        JsonNode resultsNode = rootNode.get("results");


        // Process the first 20 movies
        for (int i = 0; i < Math.min(20, resultsNode.size()); i++) {
            JsonNode movieNode = resultsNode.get(i);
            MovieDTO movie = objectMapper.treeToValue(movieNode, MovieDTO.class);
            System.out.println(movie.getTitle());
            System.out.println(movie);
            MovieDAO.createMovie(movie);
        }
    }
}
