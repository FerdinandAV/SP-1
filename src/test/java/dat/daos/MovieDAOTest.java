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
        MovieDTO movieDTO = new MovieDTO(1145,954801L,"fuck you", "fuck you", "dwadoangorjangoagnaojgn", "dpwakdkpawd", "apkdwapkd", true, "0idawdaw", new ArrayList<>(), 18.486f, LocalDate.now(), false, 6.7f, 69);
        //System.out.println(movie);
    }


    @Test
    void updateMovie() {
        MovieDTO newMovieDTO = movieDAO.findMovieByTitle("fuck you").get(0);
        System.out.println(newMovieDTO);
        newMovieDTO.setTitle("fuck you 2");
        newMovieDTO.setOriginal_title("fuck you 2");
        newMovieDTO.setRelease_date(LocalDate.now());
        System.out.println(newMovieDTO);
        movieDAO.updateMovie(newMovieDTO);
    }

    @Test
    void deleteMovie() {
        //movieDAO.deleteMovie(movieDTO);
    }

    @Test
    void findMovie() {
    }

    @Test
    void findMovieByTitle() {
        List<MovieDTO> movieDTOList = movieDAO.findMovieByTitle("druk");
        movieDTOList.forEach(movieDTO -> System.out.println(movieDTO.getTitle()));
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