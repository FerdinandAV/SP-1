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
import java.util.stream.Collectors;

public class DirectorDAO {

    static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("sp1");

    public static DirectorDTO createDirector(DirectorDTO directorDTO) {
        if (directorDTO == null) {
            System.out.println("DirectorDTO is null");
        } else {
            Director director = new Director(directorDTO);
            try (EntityManager em = emf.createEntityManager()) {
                em.getTransaction().begin();

                // Retrieve the director by name from the database
                TypedQuery<Director> query = em.createQuery("SELECT d FROM Director d WHERE d.name = :name", Director.class);
                query.setParameter("name", director.getName());
                // Check if the list of directors is empty and merge the director entity
                if (query.getResultList().isEmpty()) {
                    em.merge(director);
                } else {
                    System.out.println("Director already exists");
                    director = query.getSingleResult();
                }

                em.getTransaction().commit();
                return new DirectorDTO(director);
            }
        }

        return null;

    }

    public static void createDirectors(List<DirectorDTO> directorDTOS) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Iterate over the list of directors
            for (DirectorDTO directorDTO : directorDTOS) {
                Director director = new Director(directorDTO);

                // Retrieve the director by imdbID from the database
                TypedQuery<Director> query = em.createQuery("SELECT d FROM Director d WHERE d.imdbId = :imdb_id", Director.class);
                query.setParameter("imdb_id", directorDTO.getImdbId());

                // Check if the list of directors is empty and merge the director entity
                if (query.getResultList().isEmpty()) {
                    em.merge(director);
                } else {
                    System.out.println("Actor already exists");
                }
            }
            em.getTransaction().commit();
        }
    }

    public DirectorDTO updateDirector(DirectorDTO directorDTO) {
        Director director = new Director(directorDTO);
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            //Update director
            em.merge(director);
            em.getTransaction().commit();
        }

        return new DirectorDTO(director);
    }

    public void deleteDirector(DirectorDTO directorDTO) {
        Director director = new Director(directorDTO);
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            //Delete director
            em.remove(em.find(Director.class, director.getId()));
            em.getTransaction().commit();
        }

    }

    public DirectorDTO findDirector(int id) {
        try (EntityManager em = emf.createEntityManager()) {

            // Find the director by ID
            Director director = em.find(Director.class, id);
            return new DirectorDTO(director);
        }
    }

    public List<MovieDTO> findMoviesByDirectorId(int directorId) {
        try (EntityManager em = emf.createEntityManager()) {

            // Find the director by ID
            Director director = em.find(Director.class, directorId);
            if (director == null) {
                throw new RuntimeException("Director not found with this ID" + directorId);
            }
            // Retrieve the movies by directorID from the database
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m JOIN m.director d WHERE d.id = :directorId", Movie.class);
            query.setParameter("directorId", directorId);
            List<MovieDTO> moviesDTOS = new ArrayList<>();

            // Iterate over the list of movies and add them to the list of movieDTOs
            query.getResultList().forEach((movie) -> moviesDTOS.add(new MovieDTO(movie)));
            return moviesDTOS;
        }
    }

    public DirectorDTO findDirectorByTMDBID(Long tmdb_id) {
        try (EntityManager em = emf.createEntityManager()) {

            // Retrieve the director by tmdbID from the database
            TypedQuery<Director> query = em.createQuery("SELECT d FROM Director d WHERE d.imdbId = :tmdb_id", Director.class);
            query.setParameter("tmdb_id", tmdb_id);

            // Check if the list of directors is empty and throw an exception if it is
            if (query.getResultList().isEmpty()) {
                throw new RuntimeException("Director not found with TMDB ID: " + tmdb_id);
            } else {
                Director result = query.getSingleResult();
                return new DirectorDTO(result);
            }
        }
    }

    public List<DirectorDTO> getAllDirectors() {
        try (EntityManager em = emf.createEntityManager()) {

            // Retrieve all directors from the database
            TypedQuery<Director> query = em.createQuery("SELECT d FROM Director d", Director.class);
            List<Director> directors = query.getResultList();

            // Convert the list of directors to a list of directorDTOs
            return directors.stream()
                    .map(DirectorDTO::new)
                    .collect(Collectors.toList());
        }
    }
}
