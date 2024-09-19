package dat.daos;

import dat.DTO.DirectorDTO;
import dat.DTO.MovieDTO;
import dat.config.HibernateConfig;
import dat.entities.Actor;
import dat.entities.Director;
import dat.entities.Movie;
import dat.DTO.ActorDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.stream.Collectors;

public class ActorDAO {

    static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("sp1");

    public static ActorDTO createActor(ActorDTO actorDTO) {
        Actor actor = new Actor(actorDTO);
        try (EntityManager em = emf.createEntityManager()) {
            //Convert DTO to Entity
            em.getTransaction().begin();

            //Check if actor already exists
            TypedQuery<Actor> query = em.createQuery("SELECT a FROM Actor a WHERE a.name = :name", Actor.class);
            query.setParameter("name", actor.getName());
            if (query.getResultList().isEmpty()) {
                em.persist(actor);
            } else {
                System.out.println("Actor already exists");
                actor = query.getSingleResult();
            }

            em.getTransaction().commit();
        }
        return new ActorDTO(actor);

    }

    public ActorDTO updateActor(ActorDTO actorDTO) {
        Actor actor = new Actor(actorDTO);
        try (EntityManager em = emf.createEntityManager()) {
            //Convert DTO to Entity
            em.getTransaction().begin();

            //Update actor
            em.merge(actor);
            em.getTransaction().commit();
        }

        return new ActorDTO(actor);
    }

    public void deleteActor(ActorDTO actorDTO) {
        Actor actor = new Actor(actorDTO);
        try (EntityManager em = emf.createEntityManager()) {
            //Convert DTO to Entity
            em.getTransaction().begin();

            //Delete actor
            em.remove(em.find(Actor.class, actor.getId()));
            em.getTransaction().commit();
        }

    }

    public ActorDTO findActor(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            Actor actor = em.find(Actor.class, id);
            return new ActorDTO(actor);
        }
    }

    public ActorDTO getMoviesByActor(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            // Convert DTO to Entity
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m JOIN m.actors a WHERE a.id = :actorId", Movie.class);
            // Query to select movies by actor id
            query.setParameter("actorId", id);

            // Get the list of movies
            List<Movie> movies = query.getResultList();

            // Get the actor by id
            Actor actor = em.find(Actor.class, id);

            // Convert the list of movies to a list of movieDTOs and return the ActorDTO with the actor and the list of corresponding movies
            List<MovieDTO> movieDTOs = movies.stream()
                    .map(MovieDTO::new)
                    .collect(Collectors.toList());

            return new ActorDTO(actor, movieDTOs);
        }
    }

    public ActorDTO getMoviesByDirector(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            // Convert DTO to Entity
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m JOIN m.director a WHERE a.id = :directorId", Movie.class);
            // Query to select movies by director id
            query.setParameter("directorId", id);

            // Get the list of movies
            List<Movie> movies = query.getResultList();


            // Get the director by id
            Director director = em.find(Director.class, id);

            // Convert the list of movies to a list of movieDTOs and return the DirectorDTO with the director and the list of corresponding movies
            List<MovieDTO> movieDTOs = movies.stream()
                    .map(MovieDTO::new)
                    .collect(Collectors.toList());

            return new DirectorDTO(director, movieDTOs);
        }
    }

}
