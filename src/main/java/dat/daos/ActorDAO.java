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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


import java.util.List;

public class ActorDAO {

    static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("sp1");

    public static ActorDTO createActor(ActorDTO actorDTO) {
        Actor actor = new Actor(actorDTO);
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Check if actor already exists by querying the database based on name (or another unique field)
            TypedQuery<Actor> query = em.createQuery("SELECT a FROM Actor a WHERE a.name = :name", Actor.class);
            query.setParameter("name", actor.getName());
            List<Actor> result = query.getResultList();

            if (result.isEmpty()) {
                // If the actor does not exist, persist the new entity
                em.persist(actor);
            } else {
                // If the actor exists, merge the existing entity with new data
                actor = result.get(0);
                em.merge(actor);  // Merge updates the existing entity with any new changes
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ActorDTO(actor);
    }

    public static List<ActorDTO> createActors(List<ActorDTO> actorDTOS) {
        List<ActorDTO> actorDTOList = new ArrayList<>();
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            for (int i = 0; i < actorDTOS.size(); i++) {
                Actor actor = new Actor(actorDTOS.get(i));
                System.out.println(i);
                //Check if movie already exists
                TypedQuery<Actor> query = em.createQuery("SELECT a FROM Actor a WHERE a.imdbId = :imdb_id", Actor.class);
                query.setParameter("imdb_id", actorDTOS.get(i).getImdbId());
                if (query.getResultList().isEmpty()) {
                    em.merge(actor);
                } else {
                    System.out.println("Actor already exists");
                }
                actorDTOList.add(new ActorDTO(query.getResultList().get(0)));
            }

            em.getTransaction().commit();
        }
        return actorDTOList;
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

    public List<MovieDTO> findMoviesByActorId(int actorId) {
        try (EntityManager em = emf.createEntityManager()) {
            // Retrieve the actor by ID
            Actor actor = em.find(Actor.class, actorId);
            if (actor == null) {
                throw new RuntimeException("Actor not found with ID: " + actorId);
            }

            // Retrieve movies for the actor
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m JOIN m.actors a WHERE a.id = :actorId", Movie.class);
            query.setParameter("actorId", actorId);
            List<MovieDTO> moviesDTOS = new ArrayList<>();

            query.getResultList().forEach((movie) -> moviesDTOS.add(new MovieDTO(movie)));
            return moviesDTOS;
        }
    }

    public List<ActorDTO> getAllActors() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Actor> query = em.createQuery("SELECT a FROM Actor a", Actor.class);
            List<Actor> actors = query.getResultList();
            return actors.stream()
                    .map(ActorDTO::new)
                    .collect(Collectors.toList());
        }
    }

}
