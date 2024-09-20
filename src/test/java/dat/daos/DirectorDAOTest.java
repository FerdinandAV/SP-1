package dat.daos;

import dat.DTO.ActorDTO;
import dat.DTO.DirectorDTO;
import dat.DTO.MovieDTO;
import dat.config.HibernateConfig;
import dat.entities.Director;
import dat.entities.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DirectorDAOTest {

    private static DirectorDAO directorDAO; // Declare an instance of ActorDAO
    private static MovieDAO movieDAO;       // Declare an instance of MovieDAO

    private static EntityManagerFactory emf; // Declare an instance of EntityManagerFactory

    @BeforeAll
    static void setUp() {
        // Setting up the test connecting to the database by HibernateConfig
        emf = HibernateConfig.getEntityManagerFactory("sp1");

        directorDAO = new DirectorDAO(); // Initialize an instance of DirectorDAO
        movieDAO = new MovieDAO(); // Initialize an instance of MovieDAO
    }


    @Test
    void testfindMoviesByDirectorId() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        // Create a new director
        Director director = new Director();
        director.setName("Test Director");
        em.persist(director);

        // Create a new movie
        Movie movie1 = new Movie();
        movie1.setTitle("Test Movie 1");
        movie1.setDirector(director);
        em.persist(movie1);

        // Create another movie
        Movie movie2 = new Movie();
        movie2.setTitle("Test Movie 2");
        movie2.setDirector(director);
        em.persist(movie2);

        em.getTransaction().commit();
        em.close();

        // Call the method findMoviesByDirectorId fetching the director by id
        List<MovieDTO> movies = directorDAO.findMoviesByDirectorId(director.getId());

        // Asserting that it is nor null
        assertNotNull(movies,"Movies list should not be null");

        //Expecting 2 movies from the full list.
        assertEquals(2,movies.size(),"There should be 2 movies returned");

        // assert it is true that the movies are matching their titles
        assertTrue(movies.stream().anyMatch(movie -> movie.getTitle().equals("Test Movie 1")), "Movie 1 should be present");
        assertTrue(movies.stream().anyMatch(movie -> movie.getTitle().equals("Test Movie 2")), "Movie 2 should be present");
    }

    @Test
    void findMoviesByDirectorId() {
        // Second method test for findMoviesByDirectorId()
        // 21 is Thomas Winterberg
        directorDAO.findMoviesByDirectorId(21).forEach(movieDTO -> System.out.println(movieDTO.getTitle()));
    }

    @Test
    void getAllDirectors() {
        List<DirectorDTO> directors = directorDAO.getAllDirectors(); // Fetching all directors
        assertNotNull(directors, "Actors list should not be null");
        assertFalse(directors.isEmpty(), "Actors list should not be empty");


        // Here we get the directorDTO printing out each name
        directors.forEach(directorDTO -> System.out.println(directorDTO.getName()));
    }
}
