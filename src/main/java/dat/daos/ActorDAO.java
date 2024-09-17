package dat.daos;

import dat.config.HibernateConfig;
import dat.entities.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

public class ActorDAO {

    EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("SP1");

    public ActorDAO createActor(ActorDTO actorDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            //Convert DTO to Entity
            Actor actor = new Actor(actorDTO);
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
        try (EntityManager em = emf.createEntityManager()) {
            //Convert DTO to Entity
            Actor actor = new actor(actorDTO);
            em.getTransaction().begin();

            //Update actor
            em.merge(actor);
            em.getTransaction().commit();
        }

        return new ActorDTO(actor);
    }

    public void deleteActor(ActorDTO actorDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            //Convert DTO to Entity
            Actor actor = new Actor(actorDTO);
            em.getTransaction().begin();

            //Delete actor
            em.remove(em.find(Actor.class, actor.getId()));
            em.getTransaction().commit();
        }

        return new ActorDTO(actor);
    }

    public ActorDTO findActor(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            Actor actor = em.find(Actor.class, id);
            return new ActorDTO(actor);
        }
    }

}
