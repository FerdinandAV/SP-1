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

    static MovieDAO movieDAO;  // Declare an instance of MovieDAO


    @BeforeAll
    static void setUp() {
        movieDAO = new MovieDAO(); // Initialize a new instance of MovieDAO
    }

    @Test
    void createMovie() {
        MovieDTO movieDTO = MovieDTO.builder() // Building a new MovieDTO object
                .adult(false)
                .overview("dwadaw")
                .title("new movie")
                .build();

        // Call the createMovie method with the movieDTO,
        // which creates the Movie entity in the database
        // and returns the result as an MovieDTO, stored in the movieDTOFromDB variable.
        MovieDTO movieDTOFromDB = MovieDAO.createMovie(movieDTO);

        // assert it is not null fetching the id from the database
        assertNotNull(movieDTOFromDB.getId());
    }


    @Test
    void updateMovie() {

        // Finding the movie by title, starting with the letter "a"
        MovieDTO newMovieDTO = movieDAO.findMovieByTitle("a").get(0);
        newMovieDTO.setTitle("test update"); // Setting title
        newMovieDTO.setRelease_date(LocalDate.now()); // Setting the release date
        movieDAO.updateMovie(newMovieDTO); // calling the update movie method

        // assert that the movie is not null, by method.
        assertNotNull(movieDAO.findMovieByTitle("test update").get(0).getTitle());
    }

    @Test
    void deleteMovie() {
        //Finding the movie by title
        MovieDTO movieDTO = movieDAO.findMovieByTitle("new movie").get(0);
        movieDAO.deleteMovie(movieDTO); // Delete the movie

        // Making sure the movie by title is null from the database
        assertNull(movieDAO.findMovieByTitle("new movie"));
    }

    @Test
    void findMovie() {
    }

    @Test
    void findMovieByTitle() {
        // finding a movie by the title "druk" from the list
        List<MovieDTO> movieDTOList = movieDAO.findMovieByTitle("druk");
        // It then for each movieDTO, prints out the title,
        // and the original name of the movie.
        movieDTOList.forEach(movieDTO -> System.out.println("Title: " + movieDTO.getTitle() + ", Original title: " + movieDTO.getOriginal_title()));
    }

    @Test
    void getTotalAverageRating() {
        // Calling the getTotalAverageRating() method
        System.out.println(movieDAO.getTotalAverageRating());;
    }

    @Test
    void getTopTenBestMovies() {
        // Calling the getTopTenBestMovies() method where
        // for each movieDTO, it will print out the title, and the average vote.
        movieDAO.getTopTenBestMovies().forEach(movieDTO -> System.out.println(movieDTO.getTitle() + " " + movieDTO.getVote_average() + " " + movieDTO.getVote_count()));
    }

    @Test
    void getTopTenWorstMovies() {
        // Same way is used as above
        movieDAO.getTopTenWorstMovies().forEach(movieDTO -> System.out.println(movieDTO.getTitle() + " " + movieDTO.getVote_average() + " " + movieDTO.getVote_count()));
    }

    @Test
    void getTopTenMostPopularMovies() {

        // Same way is used as above
        movieDAO.getTopTenMostPopularMovies().forEach(movieDTO -> System.out.println(movieDTO.getTitle() + " " + movieDTO.getPopularity()));
    }

    @Test
    void getAllMovies() {

        // Same way is used as above
        movieDAO.getAllMovies().forEach(movieDTO -> System.out.println(movieDTO.getTitle()));
    }
}