package dat.daos;

import dat.DTO.ActorDTO;
import dat.DTO.GenreDTO;
import dat.DTO.MovieDTO;
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

                // Retrieve the genre by name from the database
                TypedQuery<Genre> query = em.createQuery("SELECT g FROM Genre g WHERE g.genre = :genre", Genre.class);
                query.setParameter("genre", genre.getGenre());

                // Check if the list of genres is empty and merge the genre entity
                if (query.getResultList().isEmpty()) {
                    em.merge(genre);
                } else {
                    System.out.println("Genre already exists");
                }
                // Add the genre to the list of genres
                genreDTOS.add(new GenreDTO(query.getSingleResult()));
            }

            em.getTransaction().commit();
        }
        return genreDTOS;
    }

    public static List<GenreDTO> getAllGenres() {
        try (EntityManager em = emf.createEntityManager()) {

            // Retrieve all genres from the database
            TypedQuery<Genre> query = em.createQuery("SELECT g FROM Genre g", Genre.class);

            // Get the list of genres from the query
            List<Genre> genres = query.getResultList();

            // Create a list of GenreDTOs
            List<GenreDTO> genreDTOs = new ArrayList<>();

            // Convert the list of Genre entities to a list of GenreDTOs
            for (Genre genre : genres) {
                genreDTOs.add(new GenreDTO(genre));
            }

            return genreDTOs;
        }
    }

    public static GenreDTO getGenreByTMDBID(Long id) {
        try (EntityManager em = emf.createEntityManager()) {

            // Retrieve the genre by tmdbID from the database
            TypedQuery<Genre> query = em.createQuery("SELECT g FROM Genre g WHERE g.tmdbId = :id", Genre.class);
            query.setParameter("id", id);

            // Returns genre by tmdbID
            return new GenreDTO(query.getSingleResult());
        }
    }

    public GenreDTO findGenreByTMDBID(Long tmdb_id) {
        try (EntityManager em = emf.createEntityManager()) {

            // Retrieve the genre by tmdbID from the database
            TypedQuery<Genre> query = em.createQuery("SELECT g FROM Genre g WHERE g.tmdbId = :tmdb_id", Genre.class);
            query.setParameter("tmdb_id", tmdb_id);

            // Get the list of genres from the query
            List<Genre> result = query.getResultList();

            // Check if the list of genres is empty and throw an exception
            if (result.isEmpty()) {
                throw new RuntimeException("Genre not found with TMDB ID: " + tmdb_id);
            }
            return new GenreDTO(result.get(0));
        }
    }

    public List<MovieDTO> searchByGenre(String genre) {
        try (EntityManager em = emf.createEntityManager()) {

            // Retrieve movies by genre from the database
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m JOIN m.genres g WHERE g.genre = :genre", Movie.class);
            query.setParameter("genre", genre);

            // Get the list of movies from the query
            List<Movie> movies = query.getResultList();

            // Create a list of MovieDTOs
            List<MovieDTO> movieDTOS = new ArrayList<>();

            // Convert the list of Movie entities to a list of MovieDTOs
            for (Movie movie : movies) {
                movieDTOS.add(new MovieDTO(movie));
            }

            return movieDTOS;
        }
    }
}
