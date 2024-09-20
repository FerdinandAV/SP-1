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

    static ActorDAO actorDAO; // Declare an instance of ActorDAO
    static MovieDAO movieDAO; // Declare an instance of MovieDAO

    @BeforeAll
    static void setUp() {
        actorDAO = new ActorDAO(); // Initialize the ActorDAO instance
        movieDAO = new MovieDAO(); // Initialize the MovieDAO instance
    }

    @Test
    void createActor() {

        ActorDTO actorDTO = new ActorDTO();   // Create a new ActorDTO object
        actorDTO.setName("Mads Mikkelsen");   // Setting the name to Mads Mikkelsen

        // Call the createActor method with the actorDTO,
        // which creates the Actor entity in the database
        // and returns the result as an ActorDTO, stored in the createdActor variable.
        ActorDTO createdActor = ActorDAO.createActor(actorDTO);


        // Assert that the created actor is not null,
        // confirming the actor was created successfully
        assertNotNull(createdActor, "Actor should be created successfully");

        //Assert that the created actor's ID is not null,
        // meaning the entity was persisted
        assertNotNull(createdActor.getId(), "Actor ID should not be null");
    }

    @Test
    void updateActor() {
        // Setup a known actor
        ActorDTO actorDTO = new ActorDTO(); // Create a new ActorDTO object
        actorDTO.setName("Mads Mikkelsen"); // Setting the name to Mads Mikkelsen

        // Call the createActor method with the actorDTO,
        // which creates the Actor entity in the database
        // and returns the result as an ActorDTO, stored in the createdActor variable.
        ActorDTO createdActor = ActorDAO.createActor(actorDTO);


        // Updating the actor
        createdActor.setName("Lars Mikkelsen"); // Setting the name to Mads Mikkelsen


        // Call the updateActor method with the actorDTO,
        // which updates the Actor entity in the database
        // and returns the result as an ActorDTO, stored in the updatedActor variable.
        ActorDTO updatedActor = actorDAO.updateActor(createdActor);


        // Assert that the updated actor is not null,
        // confirming the actor was updated successfully
        assertNotNull(updatedActor, "Actor should be updated successfully");


        // Assert that the updated actor's name is not null,
        // meaning the entity was updated
        assertEquals("Lars Mikkelsen", updatedActor.getName(), "Actor name should be updated");
    }

    @Test
    void deleteActor() {
        // Setup a known actor
        ActorDTO actorDTO = new ActorDTO(); // Create a new ActorDTO object
        actorDTO.setName("Mad Mikkelsen");  // Setting the name to Mads Mikkelsen

        // Call the createActor method with the actorDTO,
        // which creates the Actor entity in the database
        // and returns the result as an ActorDTO, stored in the createdActor variable.
        ActorDTO createdActor = ActorDAO.createActor(actorDTO);

        // Delete the actor
        actorDAO.deleteActor(createdActor); // Removing the created actor

        // Try to find the deleted actor by the findActor method that tries to fetch
        // the id of the created actor from the database.
        ActorDTO deletedActor = actorDAO.findActor(createdActor.getId());

        // It then confirms, it is null printing out the message:
        // "Actor should be deleted"
        assertNull(deletedActor, "Actor should be deleted");
    }

    @Test
    public void testFindMoviesByActorId() {


        EntityManager em = emf.createEntityManager(); // Creating a new EntityManager

        em.getTransaction().begin();  // Starting a new transaction

        // Create and persist an actor
        Actor actor = new Actor();
        actor.setName("Mads Mikkelsen");
        em.persist(actor);

        // Create and persist 2 movies
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

        // Calling the method findMoviesByActorId fetching the actor by its id
        List<MovieDTO> movies = actorDAO.findMoviesByActorId(actor.getId());

        // Verify results
        assertNotNull(movies, "Movies list should not be null");
        assertEquals(2, movies.size(), "There should be 2 movies returned");
        assertTrue(movies.stream().anyMatch(movie -> movie.getTitle().equals("Pusher")), "Movie 1 should be present");
        assertTrue(movies.stream().anyMatch(movie -> movie.getTitle().equals("Blinkende Lygter")), "Movie 2 should be present");
    }

    @Test
    void findMoviesByActorID() {
        // 149 should be Mads Mikkelsen by the calling method
        actorDAO.findMoviesByActorId(149).forEach(movieDTO -> System.out.println(movieDTO.getTitle()));
    }

    @Test
    void getAllActors() {
        List<ActorDTO> actors = actorDAO.getAllActors(); // fetching all actors by method
        assertNotNull(actors, "Actors list should not be null");
        assertFalse(actors.isEmpty(), "Actors list should not be empty");
        // Here we get the actorDTO printing out each name
        actors.forEach(actorDTO -> System.out.println(actorDTO.getName()));
    }
}





