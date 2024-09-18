package dat.daos;

import dat.DTO.MovieDTO;
import dat.config.HibernateConfig;
import dat.entities.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.*;
import java.util.stream.Collectors;

public class MovieDAO {

    static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("sp1");

    public static MovieDTO createMovie(MovieDTO movieDTO) {
        Movie movie = new Movie(movieDTO);
        try (EntityManager em = emf.createEntityManager()) {
            //Convert DTO to Entity
            em.getTransaction().begin();

            //Check if movie already exists
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m WHERE m.title = :title", Movie.class);
            query.setParameter("title", movie.getTitle());
            if (query.getResultList().isEmpty()) {
                em.merge(movie);
            }
            else {
                /*movie.setId(query.getResultList().get(0).getId());
                em.merge(movie);*/
                System.out.println("Movie already exists");
                movie = query.getSingleResult();
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

                //Check if movie already exists
                TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m WHERE m.tmdb_id = :tmdb_id", Movie.class);
                query.setParameter("tmdb_id", movie.getTmdb_id());
                if (query.getResultList().isEmpty()) {
                    em.merge(movie);
                }
                else {
                    System.out.println("Movie already exists");
                    movie = query.getSingleResult();
                }
            }
            em.getTransaction().commit();
        }
    }

    public MovieDTO updateMovie(MovieDTO movieDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            //Convert DTO to Entity
            em.getTransaction().begin();

            Movie existingMovie = em.find(Movie.class, movieDTO.getId());
            if (existingMovie == null) {
                throw new IllegalArgumentException("Movie not found with id: " + movieDTO.getId());
            }

            // Set new values
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

            System.out.println(existingMovie);

            //Update movie
            Movie updatedMovie = em.merge(existingMovie);
            em.getTransaction().commit();

            // return movie with new values
            return new MovieDTO(updatedMovie);
        }
    }

    public void deleteMovie(MovieDTO movieDTO) {
        Movie movie = new Movie(movieDTO);
        try (EntityManager em = emf.createEntityManager()) {
            //Convert DTO to Entity
            em.getTransaction().begin();

            //Delete movie
            em.remove(em.find(Movie.class, movie.getId()));
            em.getTransaction().commit();
        }

    }

    public MovieDTO findMovie(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            Movie movie = em.find(Movie.class, id);
            return new MovieDTO(movie);
        }
    }

    public List<MovieDTO> findMovieByTitle(String title) {
        List<MovieDTO> movieDTOS = new ArrayList<>();

        try (EntityManager em = emf.createEntityManager()) {
            // Convert DTO to Entity
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m", Movie.class);
            // Query to retrive all movies
            List<Movie> movies = query.getResultList();

            // Maps movies to MovieDTOs, filters by title and adds them to a list
            movieDTOS = movies.stream()
                    .filter(movie -> movie.getTitle().toLowerCase().contains(title.toLowerCase()))
                    .map(movie -> new MovieDTO(movie))
                    .collect(Collectors.toList());
        }
        return movieDTOS;
    }

    public double getTotalAverageRating() {
        try (EntityManager em = emf.createEntityManager()) {
            // Convert DTO to Entity
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m", Movie.class);
            // Query to retrive all movies
            List<Movie> movies = query.getResultList();

            // Maps movies to MovieDTOs, finds movies with Vote_count higher than 20, maps to double and calculates average
            double number = movies.stream()
                    .filter(movie -> movie.getVote_count() > 20)
                    .mapToDouble(Movie::getVote_average)
                    .average()
                    .orElse(0.0);

            //DoubleStream stream = DoubleStream.of(movies.stream().mapToInt(m -> m.getVote_count()).sum());
            //System.out.println(number);

            return number;
        }
    }

    public List<MovieDTO> getTopTenBestMovies() {
        try (EntityManager em = emf.createEntityManager()) {
            // Convert DTO to Entity
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m", Movie.class);
            // Query to retrive all movies
            List<Movie> movies = query.getResultList();

            // Maps movies to MovieDTOs, sorts by vote_average and reversed, so they go from highest to lowest, filters by vote_count higher than 50, limits to 10 and adds them to a list
            List<MovieDTO> movieDTOS = movies.stream()
                    .sorted(Comparator.comparing(Movie::getVote_average).reversed())
                    .filter(movie -> movie.getVote_count() > 50)
                    .limit(10)
                    .map(movie -> new MovieDTO(movie))
                    .collect(Collectors.toList());
            return movieDTOS;
        }
    }

    public List<MovieDTO> getTopTenWorstMovies() {
        try (EntityManager em = emf.createEntityManager()) {
            // Convert DTO to Entity
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m ORDER BY m.vote_average ASC", Movie.class);
            // Query to retrive all movies and orders them by vote_average in ascending order
            List<Movie> movies = query.getResultList();
            // Maps movies to MovieDTOs, filters by vote_count higher than 50, limits to 10 and adds them to a list
            List<MovieDTO> movieDTOS = movies.stream()
                    .filter(movie -> movie.getVote_count() > 50)
                    .limit(10)
                    .map(movie -> new MovieDTO(movie))
                    .collect(Collectors.toList());
            return movieDTOS;
        }
    }

    public List<MovieDTO> getTopTenMostPopularMovies() {
        try (EntityManager em = emf.createEntityManager()) {
            // Convert DTO to Entity
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m ORDER BY m.popularity DESC", Movie.class);
            // Query to retrive all movies and orders them by popularity in descending order
            List<Movie> movies = query.getResultList();
            // Maps movies to MovieDTOs, limits to 10 and adds them to a list
            List<MovieDTO> movieDTOS = movies.stream()
                    .limit(10)
                    .map(movie -> new MovieDTO(movie))
                    .collect(Collectors.toList());
            return movieDTOS;
        }
    }

    public Set<MovieDTO> getAllMovies() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m", Movie.class);
            List<Movie> movies = query.getResultList();

            Set<MovieDTO> movieDTOS = new HashSet<>();

            for (Movie movie : movies) {
                movieDTOS.add(new MovieDTO(movie));
            }

            return movieDTOS;
        }
    }

}
