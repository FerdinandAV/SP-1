package dat.daos;

import dat.DTO.ActorDTO;
import dat.DTO.MovieDTO;
import dat.entities.Actor;
import dat.entities.Movie;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dat.daos.ActorDAO.emf;
import static org.junit.jupiter.api.Assertions.*;

public class ActorDAOTest {

    static ActorDAO actorDAO;
    static MovieDAO movieDAO; // Assuming you have a MovieDAO class for movie operations

    @BeforeAll
    static void setUp() {
        actorDAO = new ActorDAO();
        movieDAO = new MovieDAO(); // Initialize MovieDAO if needed
    }

    @Test
    void createActor() {
        ActorDTO actorDTO = new ActorDTO();
        actorDTO.setName("Mads Mikkelsen");
        ActorDTO createdActor = ActorDAO.createActor(actorDTO);
        assertNotNull(createdActor, "Actor should be created successfully");
        assertNotNull(createdActor.getId(), "Actor ID should not be null");
    }

    @Test
    void updateActor() {
        // Setup a known actor
        ActorDTO actorDTO = new ActorDTO();
        actorDTO.setName("Mads Mikkelsen");
        ActorDTO createdActor = ActorDAO.createActor(actorDTO);

        // Update the actor
        createdActor.setName("Lars Mikkelsen");
        ActorDTO updatedActor = actorDAO.updateActor(createdActor);

        assertNotNull(updatedActor, "Actor should be updated successfully");
        assertEquals("Lars Mikkelsen", updatedActor.getName(), "Actor name should be updated");
    }

    @Test
    void deleteActor() {
        // Setup a known actor
        ActorDTO actorDTO = new ActorDTO();
        actorDTO.setName("Test Actor");
        ActorDTO createdActor = ActorDAO.createActor(actorDTO);

        // Delete the actor
        actorDAO.deleteActor(createdActor);

        // Try to find the deleted actor
        ActorDTO deletedActor = actorDAO.findActor(createdActor.getId());
        assertNull(deletedActor, "Actor should be deleted");
    }

    @Test
    public void testFindMoviesByActorId() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        // Create and persist an actor
        Actor actor = new Actor();
        actor.setName("Mads Mikkelsen");
        em.persist(actor);

        // Create and persist movies
        Movie movie1 = new Movie();
        movie1.setTitle("Pusher");
        movie1.setActors(List.of(actor));
        em.persist(movie1);

        Movie movie2 = new Movie();
        movie2.setTitle("Blinkende Lygter");
        movie2.setActors(List.of(actor));
        em.persist(movie2);

        em.getTransaction().commit();
        em.close();

        // Call the method
        List<MovieDTO> movies = actorDAO.findMoviesByActorId(actor.getId());

        // Verify results
        assertNotNull(movies, "Movies list should not be null");
        assertEquals(2, movies.size(), "There should be 2 movies returned");
        assertTrue(movies.stream().anyMatch(movie -> movie.getTitle().equals("Pusher")), "Movie 1 should be present");
        assertTrue(movies.stream().anyMatch(movie -> movie.getTitle().equals("Blinkende Lygter")), "Movie 2 should be present");
    }

    @Test
    void findMoviesByActorID() {
        // 149 is Mads Mikkelsen
        actorDAO.findMoviesByActorId(149).forEach(movieDTO -> System.out.println(movieDTO.getTitle()));
    }

    @Test
    void getAllActors() {
        List<ActorDTO> actors = actorDAO.getAllActors();
        assertNotNull(actors, "Actors list should not be null");
        assertFalse(actors.isEmpty(), "Actors list should not be empty");
        actors.forEach(actorDTO -> System.out.println(actorDTO.getName()));
    }
}





