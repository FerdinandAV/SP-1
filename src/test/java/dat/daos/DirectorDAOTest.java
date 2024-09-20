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

    private static DirectorDAO directorDAO;
    private static MovieDAO movieDAO; // Assume you have a MovieDAO class for movie operations

    private static EntityManagerFactory emf;

    @BeforeAll
    static void setUp() {
        emf = HibernateConfig.getEntityManagerFactory("sp1");
        directorDAO = new DirectorDAO();
        movieDAO = new MovieDAO(); // Initialize MovieDAO if needed
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

        // Call the method
        List<MovieDTO> movies = directorDAO.findMoviesByDirectorId(director.getId());

        assertNotNull(movies,"Movies list should not be null");
        assertEquals(2,movies.size(),"There should be 2 movies returned");
        assertTrue(movies.stream().anyMatch(movie -> movie.getTitle().equals("Test Movie 1")), "Movie 1 should be present");
        assertTrue(movies.stream().anyMatch(movie -> movie.getTitle().equals("Test Movie 2")), "Movie 2 should be present");
    }

    @Test
    void findMoviesByDirectorId() {
        // 21 is Thomas Winterberg
        directorDAO.findMoviesByDirectorId(21).forEach(movieDTO -> System.out.println(movieDTO.getTitle()));
    }

    @Test
    void getAllDirectors() {
        List<DirectorDTO> directors = directorDAO.getAllDirectors();
        assertNotNull(directors, "Actors list should not be null");
        assertFalse(directors.isEmpty(), "Actors list should not be empty");
        directors.forEach(actorDTO -> System.out.println(actorDTO.getName()));
    }
}
