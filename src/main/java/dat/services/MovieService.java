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
import java.util.concurrent.*;

public class MovieService {

    public static final String API_KEY = System.getenv("API_KEY"); // Getting API key from the environment variables
    private static final String BASE_URL_MOVIE = "https://api.themoviedb.org/3/movie/"; // URL for fetching a movie by ID
    public static final String BASE_URL_MOVIE_Danish = "https://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&with_origin_country=DK"; // URL for fetching Danish movies

    public static List<MovieDTO> fetchAllMovies(int page) throws IOException, InterruptedException {
        MovieDAO movieDAO = new MovieDAO();
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

        System.out.println(url);

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
            List<GenreDTO> genreDTOS = new ArrayList<>();

            JsonNode movieNode = resultsNode.get(i);
            MovieDTO movie = objectMapper.treeToValue(movieNode, MovieDTO.class);
            System.out.println(movie.getTitle());

            JsonNode genreNode = movieNode.get("genre_ids");

            for (int g = 0; g < Math.min(20, genreNode.size()); g++) {
                Long id = (long) genreNode.get(g).asInt();
                genreDTOS.add(GenreService.getGenre(id));
            }

            // Ensure genre is created before adding them to the movie
            List<GenreDTO> genresDTOfromDB = GenreDAO.createGenres(genreDTOS);

            MovieDTO movieDTO = MovieDAO.createMovie(movie);
            movies.add(movieDTO);

            System.out.println(movieDTO);

            // Add genres to the movie
            movieDAO.addGenresToMovie(movieDTO, genresDTOfromDB);
        }

        return movies;
    }

    public static void FillDBUpLast5yearsDanish(int totalPages) throws IOException, InterruptedException {
        List<MovieDTO> movieDTOS = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(12);

        //Movies are fetched in parallel using threads
        List<Callable<List<MovieDTO>>> movieTasks = new ArrayList<>();

        for (int page = 1; page <= totalPages; page++) {
            int finalPage = page;
            movieTasks.add(() -> fetchAllMovies(finalPage));
        }

        List<Future<List<MovieDTO>>> futures = executor.invokeAll(movieTasks);
        for (Future<List<MovieDTO>> future : futures) {
            try {
                List<MovieDTO> movies = future.get();
                movieDTOS.addAll(movies);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // Actors, directors are added to the movies in parallel using threads
        List<Callable<Void>> actorDirectorTasks = new ArrayList<>();
        for (MovieDTO movieDTO : movieDTOS) {
            MovieDTO finalMovieDTO = movieDTO;
            if (finalMovieDTO.getId() == null) {
                throw new IllegalArgumentException("Movie ID is missing for: " + finalMovieDTO.getTitle());
            }
            actorDirectorTasks.add(() -> {
                addActorsAndDirectorsForMovie(finalMovieDTO);
                return null;
            });
        }

        List<Future<Void>> actorDirectorFutures = executor.invokeAll(actorDirectorTasks);
        for (Future<Void> future : actorDirectorFutures) {
            try {
                future.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        executor.shutdown();

    }

    public static void addActorsAndDirectorsForMovie(MovieDTO movieDTO) throws IOException, InterruptedException {
        MovieDAO movieDAO = new MovieDAO();

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

        // Get crew node
        JsonNode crewNode = rootNode.get("crew");

        List<ActorDTO> actorDTOS = new ArrayList<>();
        List<DirectorDTO> directorDTOS = new ArrayList<>();
        DirectorDTO directorDTO = null;

        for (int i = 0; i < castNode.size(); i++) {
            JsonNode actorNode = castNode.get(i);
            //Check if the cast is an actor

            String text = String.valueOf(actorNode.get("known_for_department"));

            if (text.contains("Acting")) {
                Long id = actorNode.get("id").asLong();
                ActorDTO actorDTO = ActorService.getActor(id);
                actorDTOS.add(actorDTO);
            }
        }

        for (int i = 0; i < crewNode.size(); i++) {
            JsonNode actorNode = crewNode.get(i);
            //Check if the cast is an actor

            String text = String.valueOf(actorNode.get("known_for_department"));

            if (text.contains("Acting")) {
                Long id = actorNode.get("id").asLong();
                actorDTOS.add(ActorService.getActor(id));
            }

            if (text.contains("Directing")) {
                Long id = actorNode.get("id").asLong();
                //If director is not found yet, set it.
                if (directorDTO == null) {
                    directorDTO = DirectorService.getDirector(id);
                    //directorDTOS.add(directorDTO);
                }
                //If director is found, still add the rest of directors to the list
                else {
                    directorDTOS.add(DirectorService.getDirector(id));
                }
            }
        }

        // Ensure actors are created before adding them to the movie
        List<ActorDTO> actorsDTOfromDB = ActorDAO.createActors(actorDTOS);

        // Ensure directors are created before adding them to the movie
        DirectorDAO.createDirectors(directorDTOS);

        // Ensure director is created before adding them to the movie
        DirectorDTO directorDTOfromDB = DirectorDAO.createDirector(directorDTO);

        // Add actors, director and genres to the movie
        movieDAO.addActorsToMovie(movieDTO, actorsDTOfromDB);
        movieDAO.addDirectorToMovie(movieDTO, directorDTOfromDB);


    }


}

