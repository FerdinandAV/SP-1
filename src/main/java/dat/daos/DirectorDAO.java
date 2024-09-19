package dat.daos;

import dat.DTO.ActorDTO;
import dat.DTO.DirectorDTO;
import dat.DTO.MovieDTO;
import dat.config.HibernateConfig;
import dat.entities.Actor;
import dat.entities.Director;
import dat.DTO.DirectorDTO;
import dat.entities.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DirectorDAO {

    static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("sp1");

    public static DirectorDTO createDirector(DirectorDTO directorDTO) {
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

    public static void createDirectors(Set<DirectorDTO> directorDTOS) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            System.out.println("Fill her up!!!!");

            for (DirectorDTO directorDTO : directorDTOS) {
                Director director = new Director(directorDTO);

                //Check if movie already exists
                TypedQuery<Director> query = em.createQuery("SELECT d FROM Director d WHERE d.imdbId = :imdb_id", Director.class);
                query.setParameter("imdb_id", directorDTO.getImdbId());
                if (query.getResultList().isEmpty()) {
                    em.merge(director);
                }
                else {
                    System.out.println("Actor already exists");
                }
            }
            em.getTransaction().commit();
        }
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

    public List<MovieDTO> findMoviesByDirectorId(int Id) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m JOIN Director d WHERE d.id = :directorId", Movie.class);
            query.setParameter("directorId", Id);
            List<MovieDTO> moviesDTOS = new ArrayList<>();

            query.getResultList().forEach((movie) -> moviesDTOS.add(new MovieDTO(movie)));
            return moviesDTOS;
        }
    }
}
