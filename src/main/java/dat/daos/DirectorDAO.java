package dat.daos;

import dat.DTO.DirectorDTO;
import dat.config.HibernateConfig;
import dat.entities.Director;
import dat.DTO.DirectorDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

public class DirectorDAO {

    EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("SP1");

    public DirectorDTO createDirector(DirectorDTO directorDTO) {
        Director director = new Director(directorDTO);
        try (EntityManager em = emf.createEntityManager()) {
            //Convert DTO to Entity
            em.getTransaction().begin();

            //Check if director already exists
            TypedQuery<Director> query = em.createQuery("SELECT d FROM Director d WHERE d.name = :name", Director.class);
            query.setParameter("name", director.getName());
            if (query.getResultList().isEmpty()) {
                em.persist(director);
            }
            else {
                System.out.println("Director already exists");
                director = query.getSingleResult();
            }

            em.getTransaction().commit();
        }
        return new DirectorDTO(director);

    }

    public DirectorDTO updateDirector(DirectorDTO directorDTO) {
        Director director = new Director(directorDTO);
        try (EntityManager em = emf.createEntityManager()) {
            //Convert DTO to Entity
            em.getTransaction().begin();

            //Update actor
            em.merge(director);
            em.getTransaction().commit();
        }

        return new DirectorDTO(director);
    }

    public void deleteDirector(DirectorDTO directorDTO) {
        Director director = new Director(directorDTO);
        try (EntityManager em = emf.createEntityManager()) {
            //Convert DTO to Entity
            em.getTransaction().begin();

            //Delete director
            em.remove(em.find(Director.class, director.getId()));
            em.getTransaction().commit();
        }

    }

    public DirectorDTO findDirector(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            Director director = em.find(Director.class, id);
            return new DirectorDTO(director);
        }
    }

}
