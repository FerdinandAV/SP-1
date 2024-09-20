package dat.daos;

import dat.DTO.MovieDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class MovieDAOTest {

    static MovieDAO movieDAO;

    //MovieDTO movieDTO = new MovieDTO(1145,954801L,"fuck you", "fuck you", "dwadoangorjangoagnaojgn", "dpwakdkpawd", "apkdwapkd", true, "0idawdaw", new ArrayList<>(), 18.486f, LocalDate.now(), false, 6.7f, 69);
    @BeforeAll
    static void setUp() {
        movieDAO = new MovieDAO();
    }

    @Test
    void createMovie() {
        MovieDTO movieDTO = MovieDTO.builder()
                .adult(false)
                .overview("dwadaw")
                .title("new movie")
                .build();
        MovieDTO movieDTOFromDB = MovieDAO.createMovie(movieDTO);
        assertNotNull(movieDTOFromDB.getId());
    }


    @Test
    void updateMovie() {
        MovieDTO newMovieDTO = movieDAO.findMovieByTitle("a").get(0);
        newMovieDTO.setTitle("test update");
        newMovieDTO.setRelease_date(LocalDate.now());
        movieDAO.updateMovie(newMovieDTO);
        assertNotNull(movieDAO.findMovieByTitle("test update").get(0).getTitle());
    }

    @Test
    void deleteMovie() {
        MovieDTO movieDTO = movieDAO.findMovieByTitle("new movie").get(0);
        movieDAO.deleteMovie(movieDTO);
        assertNull(movieDAO.findMovieByTitle("new movie"));
    }

    @Test
    void findMovie() {
    }

    @Test
    void findMovieByTitle() {
        List<MovieDTO> movieDTOList = movieDAO.findMovieByTitle("druk");
        movieDTOList.forEach(movieDTO -> System.out.println("Title: " + movieDTO.getTitle() + ", Original title: " + movieDTO.getOriginal_title()));
    }

    @Test
    void getTotalAverageRating() {
        System.out.println(movieDAO.getTotalAverageRating());;
    }

    @Test
    void getTopTenBestMovies() {
        movieDAO.getTopTenBestMovies().forEach(movieDTO -> System.out.println(movieDTO.getTitle() + " " + movieDTO.getVote_average() + " " + movieDTO.getVote_count()));
    }

    @Test
    void getTopTenWorstMovies() {
        movieDAO.getTopTenWorstMovies().forEach(movieDTO -> System.out.println(movieDTO.getTitle() + " " + movieDTO.getVote_average() + " " + movieDTO.getVote_count()));
    }

    @Test
    void getTopTenMostPopularMovies() {
        movieDAO.getTopTenMostPopularMovies().forEach(movieDTO -> System.out.println(movieDTO.getTitle() + " " + movieDTO.getPopularity()));
    }

    @Test
    void getAllMovies() {
        movieDAO.getAllMovies().forEach(movieDTO -> System.out.println(movieDTO.getTitle()));
    }
}