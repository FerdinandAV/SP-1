package dat.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dat.DTO.ActorDTO;
import dat.DTO.DirectorDTO;
import dat.DTO.GenreDTO;
import dat.DTO.MovieDTO;
import dat.daos.ActorDAO;
import dat.daos.DirectorDAO;
import dat.daos.GenreDAO;
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

    public static void FillDBUpLast5yearsDanish2(int totalPages) throws IOException, InterruptedException {
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
        executor.shutdown();
        MovieDAO.createMovies(movieDTOS);
    }

    public static void addActorsAndDirectorsForMovies(List<MovieDTO> movieDTOS) throws IOException, InterruptedException {
        MovieDAO movieDAO = new MovieDAO();
        ActorDAO actorDAO = new ActorDAO();
        DirectorDAO directorDAO = new DirectorDAO();
        for (MovieDTO movieDTO : movieDTOS) {

            // Build the request URL to fetch actors based on the movie ID and page
            String url = "https://api.themoviedb.org/3/movie/" + movieDTO.getTmdb_id() + "/credits" + "?api_key=" + API_KEY;

            // Create an HTTP client
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
            JsonNode rootNode = objectMapper.readTree(response.body());

            // Get cast node
            JsonNode castNode = rootNode.get("cast");

            List<ActorDTO> actorDTOS = new ArrayList<>();
            DirectorDTO directorDTO = null;

            boolean directorFound = false;

            for (int i = 0; i < Math.min(20, castNode.size()); i++) {
                JsonNode actorNode = castNode.get(i);
                //Check if the cast is an actor

                String text = String.valueOf(actorNode.get("known_for_department"));

                if (text.contains("Acting")) {
                    Long id = actorNode.get("id").asLong();
                    actorDTOS.add(actorDAO.findActorByTMDBID(id));
                }

                if (text.contains("Directing") && !directorFound) {
                    Long id = actorNode.get("id").asLong();
                    //directorDTO = directorDAO.findDirectorByTMDBID(id);
                    directorFound = true;
                }
            }

            movieDAO.addActorsToMovie(movieDTO, actorDTOS);
            //movieDAO.addDirectorToMovie(movieDTO, directorDTO);

        }
    }

    public static void addGenresToMovies(List<MovieDTO> movieDTOS) throws IOException, InterruptedException {
        MovieDAO movieDAO = new MovieDAO();
        GenreDAO genreDAO = new GenreDAO();

        for (MovieDTO movieDTO : movieDTOS) {

            // Build the request URL to fetch actors based on the movie ID and page
            String url = "https://api.themoviedb.org/3/movie/" + movieDTO.getTmdb_id() + "?api_key=" + API_KEY;

            // Create an HTTP client
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
            JsonNode rootNode = objectMapper.readTree(response.body());

            // Get cast node
            JsonNode genresNode = rootNode.get("genres");

            List<GenreDTO> genreDTOS = new ArrayList<>();

            for (int i = 0; i < Math.min(20, genresNode.size()); i++) {
                JsonNode genreNode = genresNode.get(i);
                Long id = genreNode.get("id").asLong();
                genreDTOS.add(genreDAO.findGenreByTMDBID(id));
            }

            movieDAO.addGenresToMovie(movieDTO, genreDTOS);

        }

    }
}

