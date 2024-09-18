package dat.daos;

import dat.DTO.MovieDTO;
import dat.config.HibernateConfig;
import dat.entities.Actor;
import dat.entities.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.*;
import java.util.stream.Collectors;

public class MovieDAO {

    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("sp1");

    public static MovieDTO createMovie(MovieDTO movieDTO) {
        Movie movie = new Movie(movieDTO);
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Check if movie already exists
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m WHERE m.title = :title", Movie.class);
            query.setParameter("title", movie.getTitle());
            List<Movie> existingMovies = query.getResultList();

            if (existingMovies.isEmpty()) {
                em.persist(movie);
            } else {
                System.out.println("Movie already exists");
                movie = existingMovies.get(0); // Get the existing movie
            }

            em.getTransaction().commit();
        }
        return new MovieDTO(movie);
    }

    public static void createMovies(List<MovieDTO> movieDTOS) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            for (MovieDTO movieDTO : movieDTOS) {
                Movie movie = new Movie(movieDTO);

                // Check if movie already exists
                TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m WHERE m.tmdb_id = :tmdb_id", Movie.class);
                query.setParameter("tmdb_id", movie.getTmdb_id());
                List<Movie> existingMovies = query.getResultList();

                if (existingMovies.isEmpty()) {
                    em.persist(movie);
                } else {
                    System.out.println("Movie already exists");
                    movie = existingMovies.get(0); // Get the existing movie
                }
            }
            em.getTransaction().commit();
        }
    }

    public MovieDTO updateMovie(MovieDTO movieDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Movie existingMovie = em.find(Movie.class, movieDTO.getId());
            if (existingMovie == null) {
                throw new IllegalArgumentException("Movie not found with id: " + movieDTO.getId());
            }

            existingMovie.setTmdb_id(movieDTO.getTmdb_id());
            existingMovie.setTitle(movieDTO.getTitle());
            existingMovie.setOriginal_title(movieDTO.getOriginal_title());
            existingMovie.setRelease_date(movieDTO.getRelease_date());
            existingMovie.setOverview(movieDTO.getOverview());
            existingMovie.setAdult(movieDTO.isAdult());
            existingMovie.setOriginal_language(movieDTO.getOriginal_language());
            existingMovie.setPoster_path(movieDTO.getPoster_path());
            existingMovie.setPopularity(movieDTO.getPopularity());
            existingMovie.setVote_average(movieDTO.getVote_average());
            existingMovie.setVote_count(movieDTO.getVote_count());
            existingMovie.setVideo(movieDTO.isVideo());

            Movie updatedMovie = em.merge(existingMovie);
            em.getTransaction().commit();

            return new MovieDTO(updatedMovie);
        }
    }

    public void deleteMovie(MovieDTO movieDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Movie movie = em.find(Movie.class, movieDTO.getId());
            if (movie != null) {
                em.remove(movie);
            } else {
                System.out.println("Movie not found with id: " + movieDTO.getId());
            }

            em.getTransaction().commit();
        }
    }

    public MovieDTO findMovie(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            Movie movie = em.find(Movie.class, id);
            return movie != null ? new MovieDTO(movie) : null;
        }
    }

    public List<MovieDTO> findMovieByTitle(String title) {
        List<MovieDTO> movieDTOS = new ArrayList<>();

        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m WHERE LOWER(m.title) LIKE LOWER(:title)", Movie.class);
            query.setParameter("title", "%" + title + "%");
            List<Movie> movies = query.getResultList();

            movieDTOS = movies.stream()
                    .map(MovieDTO::new)
                    .collect(Collectors.toList());
        }
        return movieDTOS;
    }

    public double getTotalAverageRating() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Double> query = em.createQuery(
                    "SELECT AVG(m.vote_average) FROM Movie m WHERE m.vote_count > 20", Double.class);
            return query.getSingleResult() != null ? query.getSingleResult() : 0.0;
        }
    }

    public List<MovieDTO> getTopTenBestMovies() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m WHERE m.vote_count > 50 ORDER BY m.vote_average DESC", Movie.class);
            List<Movie> movies = query.setMaxResults(10).getResultList();
            return movies.stream().map(MovieDTO::new).collect(Collectors.toList());
        }
    }

    public List<MovieDTO> getTopTenWorstMovies() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m WHERE m.vote_count > 50 ORDER BY m.vote_average ASC", Movie.class);
            List<Movie> movies = query.setMaxResults(10).getResultList();
            return movies.stream().map(MovieDTO::new).collect(Collectors.toList());
        }
    }

    public List<MovieDTO> getTopTenMostPopularMovies() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m WHERE m.vote_count > 50 ORDER BY m.popularity DESC", Movie.class);
            List<Movie> movies = query.setMaxResults(10).getResultList();
            return movies.stream().map(MovieDTO::new).collect(Collectors.toList());
        }
    }

    public Set<MovieDTO> getAllMovies() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m", Movie.class);
            List<Movie> movies = query.getResultList();

            return movies.stream()
                    .map(MovieDTO::new)
                    .collect(Collectors.toSet());
        }
    }

    public void addActorToMovie(int movieId, int actorId) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Movie movie = em.find(Movie.class, movieId);
            Actor actor = em.find(Actor.class, actorId);
            if (movie != null && actor != null) {
                if (movie.getActors() == null) {
                    movie.setActors(new ArrayList<>()); // Initialize the list if null
                }
                if (!movie.getActors().contains(actor)) {
                    movie.getActors().add(actor);
                    em.merge(movie);
                }
            } else {
                System.out.println("Movie or Actor not found with IDs: " + movieId + ", " + actorId);
            }
            em.getTransaction().commit();
        }
    }
}

