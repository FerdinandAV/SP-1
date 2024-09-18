package dat.daos;

import dat.config.HibernateConfig;
import dat.entities.Actor;
import dat.entities.Movie;
import dat.DTO.ActorDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

public class ActorDAO {

    static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("SP1");

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

}
