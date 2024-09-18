package dat.daos;

import dat.DTO.MovieDTO;
import dat.config.HibernateConfig;
import dat.entities.Actor;
import dat.entities.Movie;
import dat.DTO.ActorDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Set;

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

    public static void createActors(Set<ActorDTO> actorDTOS) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            System.out.println("Fill her up!!!!");

            for (ActorDTO actorDTO : actorDTOS) {
                Actor actor = new Actor(actorDTO);

                //Check if movie already exists
                TypedQuery<Actor> query = em.createQuery("SELECT a FROM Actor a WHERE a.imdbId = :imdb_id", Actor.class);
                query.setParameter("imdb_id", actorDTO.getImdbId());
                if (query.getResultList().isEmpty()) {
                    em.merge(actor);
                }
                else {
                    System.out.println("Actor already exists");
                }
            }
            em.getTransaction().commit();
        }
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

}
