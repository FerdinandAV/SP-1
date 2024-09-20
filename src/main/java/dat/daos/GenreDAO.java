package dat.daos;

import dat.DTO.ActorDTO;
import dat.DTO.GenreDTO;
import dat.config.HibernateConfig;
import dat.entities.Actor;
import dat.entities.Genre;
import dat.entities.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

public class GenreDAO {

    static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("sp1");

    public static List<GenreDTO> createGenres(List<GenreDTO> genres) {
        List<GenreDTO> genreDTOS = new ArrayList<>();

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            for (GenreDTO genreDTO : genres) {
                // Create a Genre entity from the DTO
                Genre genre = new Genre(genreDTO);

                //Check if genre already exists
                TypedQuery<Genre> query = em.createQuery("SELECT g FROM Genre g WHERE g.genre = :genre", Genre.class);
                query.setParameter("genre", genre.getGenre());
                if (query.getResultList().isEmpty()) {
                    em.merge(genre);
                }
                else {
                    System.out.println("Genre already exists");
                }
                genreDTOS.add(new GenreDTO(query.getSingleResult()));
            }

            em.getTransaction().commit();
        }
        return genreDTOS;
    }

    public List<GenreDTO> getAllGenres() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Genre> query = em.createQuery("SELECT g FROM Genre g", Genre.class);
            List<Genre> genres = query.getResultList();

            List<GenreDTO> genreDTOs = new ArrayList<>();

            for (Genre genre : genres) {
                genreDTOs.add(new GenreDTO(genre));
            }

            return genreDTOs;
        }
    }

    public GenreDTO findGenreByTMDBID(Long tmdb_id) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Genre> query = em.createQuery("SELECT g FROM Genre g WHERE g.tmdbId = :tmdb_id", Genre.class);
            query.setParameter("tmdb_id", tmdb_id);
            List<Genre> result = query.getResultList();
            if (result.isEmpty()) {
                throw new RuntimeException("Genre not found with TMDB ID: " + tmdb_id);
            }
            return new GenreDTO(result.get(0));
        }
    }
}
