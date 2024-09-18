package dat.daos;

import dat.config.HibernateConfig;
import dat.entities.Actor;
import dat.entities.Movie;
import dat.DTO.ActorDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;

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
            }
            else {
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

    public List<Movie> findMoviesByActorId(int actorId) {
        try (EntityManager em = emf.createEntityManager()) {
            // Retrieve the actor by ID
            Actor actor = em.find(Actor.class, actorId);
            if (actor == null) {
                throw new RuntimeException("Actor not found with ID: " + actorId);
            }

            // Retrieve movies for the actor
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m JOIN m.actors a WHERE a.id = :actorId", Movie.class);
            query.setParameter("actorId", actorId);
            return query.getResultList();
        }
    }

}
