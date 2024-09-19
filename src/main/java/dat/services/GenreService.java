package dat.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    public static List<GenreDTO> fetchAllGenres() throws IOException, InterruptedException {
        String url = "https://api.themoviedb.org/3/genre/movie/list" + "?api_key=" + API_KEY;


        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        // Send the HTTP request
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response.body());

        // Assuming the response contains a field "genres" with an array of movies
        JsonNode genresNode = rootNode.get("genres");

        List<GenreDTO> genres = new ArrayList<>();

        for (int i = 0; i < Math.min(20, genresNode.size()); i++) {
            JsonNode genreNode = genresNode.get(i);
            GenreDTO genreDTO = objectMapper.treeToValue(genreNode, GenreDTO.class);
            System.out.println(genreDTO.getGenre());
            genres.add(genreDTO);
        }

        return genres;
    }

    public static void fillDBWithGenres() {
        try {
            List<GenreDTO> genres = fetchAllGenres();
            GenreDAO.createGenres(genres);

            // GenreDAO.createGenres(genreEntities);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
