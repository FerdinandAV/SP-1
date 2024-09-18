package dat.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dat.DTO.ActorDTO;
import dat.DTO.MovieDTO;
import dat.daos.ActorDAO;

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

import static dat.services.MovieService.fetchAllMovies;

public class ActorService {

    public static final String API_KEY = System.getenv("API_KEY");
    private static final String BASE_URL_ACTOR = "https://api.themoviedb.org/3/movie/";
    private static final String BASE_URL_SEARCH_ACTOR = "https://api.themoviedb.org/3/person/";

    public static ActorDTO getActor(Long actorId) throws IOException, InterruptedException {
        // Build the request URL to fetch actors based on the movie ID and page
        String url = BASE_URL_SEARCH_ACTOR + actorId + "?api_key=" + API_KEY;

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

        ActorDTO actorDTO = objectMapper.readValue(response.body(), ActorDTO.class);

        //Return the fetched actor
        return actorDTO;

    }



    public static Set<Long> fetchAllActorsID(Long movieID) throws Exception {
        // Build the request URL to fetch actors based on the movie ID and page
        String url = BASE_URL_ACTOR + movieID + "/credits" + "?api_key=" + API_KEY;

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

        //Get all ids from the different actors
        Set<Long> ids = new HashSet<>();

        for (int i = 0; i < Math.min(20, castNode.size()); i++) {
            JsonNode actorNode = castNode.get(i);
            //System.out.println(actorNode.get("known_for_department"));
            //Check if the cast is an actor

            String text = String.valueOf(actorNode.get("known_for_department"));

            if (text.contains("Acting")) {
                Long id = actorNode.get("id").asLong();
                ids.add(id);
            }
        }

        //System.out.println(Thread.currentThread().getName());


        return ids;
    }

    public static Set<ActorDTO> fillDBWithActors(Set<MovieDTO> movies) throws Exception {
        Set<ActorDTO> actorDTOS = new HashSet<>();
        Set<Long> ids = new HashSet<>();

        ExecutorService executor = Executors.newFixedThreadPool(12);

        Set<Callable<Set<Long>>> idTasks = new HashSet<>();


        for (MovieDTO movieDTO : movies) {
            idTasks.add(() -> fetchAllActorsID(movieDTO.getTmdb_id()));
        }

        List<Future<Set<Long>>> futuresId = null;
        try {
            futuresId = executor.invokeAll(idTasks);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        for (Future<Set<Long>> future : futuresId) {
            try {
                Set<Long> futureId = future.get();
                ids.addAll(futureId);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }


        Set<Callable<ActorDTO>> actorTasks = new HashSet<>();

        //Get all actors using threads
        for (Long id : ids) {
            Long finalId = id;
            actorTasks.add(() -> getActor(finalId));
        }

        List<Future<ActorDTO>> futuresActor = null;
        try {
            futuresActor = executor.invokeAll(actorTasks);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        for (Future<ActorDTO> future : futuresActor) {
            try {
                ActorDTO actorDTO = future.get();
                actorDTOS.add(actorDTO);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        executor.shutdown();

        System.out.println("Now adding actors entities to database");
        ActorDAO.createActors(actorDTOS);

        return actorDTOS;
    }
}
