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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MovieService {

    public static final String API_KEY = System.getenv("API_KEY"); // Getting API key from the environment variables
    private static final String BASE_URL_MOVIE = "https://api.themoviedb.org/3/movie/"; // URL for fetching a movie by ID
    public static final String BASE_URL_MOVIE_Danish = "https://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&with_origin_country=DK"; // URL for fetching Danish movies

    public static MovieDTO getMovieById(String id) throws Exception, InterruptedIOException {

        // // Build the request URL with the movie ID and API key

        String url = BASE_URL_MOVIE_Danish + id + "&page="+ "?api_key=" + API_KEY;


        // Create an HTTP client
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


        MovieDTO movie = objectMapper.treeToValue(rootNode, MovieDTO.class);
        return movie;
    }



    public static List<MovieDTO> fetchAllMovies(int page) throws IOException, InterruptedException {
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
                "&page=" + page + "&api_key=" + API_KEY;

        // Create an HTTP client
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

        List<MovieDTO> movies = new ArrayList<>();

        // Process the first 20 movies
        for (int i = 0; i < Math.min(20, resultsNode.size()); i++) {
            JsonNode movieNode = resultsNode.get(i);
            MovieDTO movie = objectMapper.treeToValue(movieNode, MovieDTO.class);
            System.out.println(movie.getTitle());
            movies.add(movie);
        }
        return movies;
    }

    public static List<MovieDTO> FillDBUpLast5yearsDanish(int totalPages) throws IOException, InterruptedException {
        List<MovieDTO> movieDTOS = new ArrayList<>();

        // Fetch all movies from the last 5 years
        for (int page = 1; page <= totalPages; page++) {
            List<MovieDTO> movies = fetchAllMovies(page);
            System.out.println("Movies fetched from page " + page + ": " + movies.size());
            movieDTOS.addAll(movies);
        }
        MovieDAO.createMovies(movieDTOS);

        return movieDTOS;
    }

    public static List<MovieDTO> FillDBUpLast5yearsDanish2(int totalPages) throws IOException, InterruptedException {
        List<MovieDTO> movieDTOS = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(6);

        List<Callable<List<MovieDTO>>> movieTasks = new ArrayList<>();

        for (int page = 1; page <= totalPages; page++) {

            int finalPage = page;
            movieTasks.add(() -> fetchAllMovies(finalPage));
        }

        List<Future<List<MovieDTO>>> futures = null;
        try {
            futures = executor.invokeAll(movieTasks);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        for (Future<List<MovieDTO>> future : futures) {
            try {
                List<MovieDTO> movies = future.get();
                movieDTOS.addAll(movies);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        MovieDAO.createMovies(movieDTOS);

        return movieDTOS;
    }
}

