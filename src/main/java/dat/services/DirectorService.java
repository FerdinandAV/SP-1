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



    public static Set<Long> fetchAllDirectorsID(Long movieID) throws Exception {
        // Build the request URL to fetch directors based on the movie ID and page
        String url = BASE_URL_DIRECTOR + movieID + "/credits" + "?api_key=" + API_KEY;

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

        //Get all ids from the different directors
        Set<Long> ids = new HashSet<>();

        for (int i = 0; i < Math.min(20, castNode.size()); i++) {
            JsonNode directorNode = castNode.get(i);

            //Check if the cast is a director

            String text = String.valueOf(directorNode.get("known_for_department"));

            if (text.contains("Directing")) {
                Long id = directorNode.get("id").asLong();
                ids.add(id);
            }
        }

        //System.out.println(Thread.currentThread().getName());


        return ids;
    }

    public static Set<DirectorDTO> fillDBWithDirectors(Set<MovieDTO> movies) throws Exception {
        Set<DirectorDTO> directorDTOS = new HashSet<>();
        Set<Long> ids = new HashSet<>();

        ExecutorService executor = Executors.newFixedThreadPool(12);

        Set<Callable<Set<Long>>> idTasks = new HashSet<>();


        for (MovieDTO movieDTO : movies) {
            idTasks.add(() -> fetchAllDirectorsID(movieDTO.getTmdb_id()));
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


        Set<Callable<DirectorDTO>> directorTasks = new HashSet<>();

        //Get all directors using threads
        for (Long id : ids) {
            Long finalId = id;
            directorTasks.add(() -> getDirector(finalId));
        }

        List<Future<DirectorDTO>> futuresDirector = null;
        try {
            futuresDirector = executor.invokeAll(directorTasks);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);

        }

        for (Future<DirectorDTO> future : futuresDirector) {
            try {
                DirectorDTO directorDTO = future.get();
                directorDTOS.add(directorDTO);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        executor.shutdown();

        System.out.println("Now adding directors entities to database");
        DirectorDAO.createDirectors(directorDTOS);

        return directorDTOS;
    }
}
