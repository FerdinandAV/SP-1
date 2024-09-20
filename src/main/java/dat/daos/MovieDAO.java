package dat.daos;

import dat.DTO.ActorDTO;
import dat.DTO.DirectorDTO;
import dat.DTO.GenreDTO;
import dat.DTO.MovieDTO;
import dat.config.HibernateConfig;
import dat.entities.Actor;
import dat.entities.Director;
import dat.entities.Genre;
import dat.entities.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.*;
import java.util.stream.Collectors;

public class MovieDAO {

    static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("sp1");

    public static MovieDTO createMovie(MovieDTO movieDTO) {
        Movie movie = new Movie(movieDTO);
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Retrieve the movie by title from the database
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m WHERE m.title = :title", Movie.class);
            query.setParameter("title", movie.getTitle());

            // Check if the list of movies is empty and merge the movie entity
            if (query.getResultList().isEmpty()) {
                em.merge(movie);
            } else {
                System.out.println("Movie already exists");
            }

            em.getTransaction().commit();

            return new MovieDTO(query.getSingleResult());
        }
    }

    public static void createMovies(List<MovieDTO> movieDTOS) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();


            for (MovieDTO movieDTO : movieDTOS) {
                Movie movie = new Movie(movieDTO);

                // Retrieve the movie by tmdbID from the database
                TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m WHERE m.tmdb_id = :tmdb_id", Movie.class);
                query.setParameter("tmdb_id", movie.getTmdb_id());

                // Check if the list of movies is empty and merge the movie entity
                if (query.getResultList().isEmpty()) {
                    em.merge(movie);
                } else {
                    System.out.println("Movie already exists");
                }
            }
            em.getTransaction().commit();
        }
    }

    public MovieDTO updateMovie(MovieDTO movieDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Find existing movie in database by ID
            Movie existingMovie = em.find(Movie.class, movieDTO.getId());

            // If movie does not exist, throw exception
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

            //Update movie
            Movie updatedMovie = em.merge(existingMovie);
            em.getTransaction().commit();

            // Return movie with new values
            return new MovieDTO(updatedMovie);
        }
    }

    public void deleteMovie(MovieDTO movieDTO) {
        Movie movie = new Movie(movieDTO);
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            //Delete movie
            em.remove(em.find(Movie.class, movie.getId()));
            em.getTransaction().commit();
        }

    }

    public MovieDTO findMovie(int id) {
        try (EntityManager em = emf.createEntityManager()) {

            // Find the movie by ID
            Movie movie = em.find(Movie.class, id);
            return new MovieDTO(movie);
        }
    }

    public List<MovieDTO> findMovieByTitle(String title) {
        List<MovieDTO> movieDTOS;
        try (EntityManager em = emf.createEntityManager()) {

            // Query to retrive all movies
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m", Movie.class);

            // Get the list of movies from the query
            List<Movie> movies = query.getResultList();

            // Maps movies to MovieDTOs, filters by title and adds them to a list
            movieDTOS = movies.stream()
                    .filter(movie -> movie.getTitle().toLowerCase().contains(title.toLowerCase()) || movie.getOriginal_title() != null && movie.getOriginal_title().toLowerCase().contains(title.toLowerCase()))
                    .map(movie -> new MovieDTO(movie))
                    .collect(Collectors.toList());

            // Check if the list of movies is empty and print a message if it is
            if (movieDTOS.isEmpty()) {
                System.out.println("There was no movies with this title");
                return null;
            }

        }
        return movieDTOS;
    }

    public double getTotalAverageRating() {
        try (EntityManager em = emf.createEntityManager()) {

            // Query to retrive all movies
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m", Movie.class);

            // Get the list of movies from the query
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

            // Query to retrive all movies
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m", Movie.class);

            // Get the list of movies from the query
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

            // Query to retrive all movies and orders them by vote_average in ascending order
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m ORDER BY m.vote_average ASC", Movie.class);

            // Get the list of movies from the query
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

            // Query to retrive all movies and orders them by popularity in descending order
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m ORDER BY m.popularity DESC", Movie.class);

            // Get the list of movies from the query
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

            // Query to retrive all movies from the database
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m", Movie.class);

            // Get the list of movies from the query
            List<Movie> movies = query.getResultList();

            // Create a set of MovieDTOs
            Set<MovieDTO> movieDTOS = new HashSet<>();

            // Convert the list of Movie entities to a set of MovieDTOs
            for (Movie movie : movies) {
                movieDTOS.add(new MovieDTO(movie));
            }

            return movieDTOS;
        }
    }

    public void addActorToMovie(int movieId, int actorId) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Find movie and actor by ID
            Movie movie = em.find(Movie.class, movieId);
            Actor actor = em.find(Actor.class, actorId);

            // If movie and actor are not null, add actor to movie
            if (movie != null && actor != null) {
                if (movie.getActors() == null) {
                    movie.setActors(new ArrayList<>()); // Initialize the list if null
                }

                //If the list doesn't contain the actor, add it
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

    public void addActorsToMovie(MovieDTO movieDTO, List<ActorDTO> actorDTOS) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Movie movie = em.find(Movie.class, movieDTO.getId());// Find movie by ID
            if (movie != null) {
                System.out.println("252");
                if (movie.getActors() == null) {
                    movie.setActors(new ArrayList<>()); // Initialize the list if null
                    System.out.println("254");
                }
                for (ActorDTO actorDTO : actorDTOS) {
                    Actor actor = em.find(Actor.class, actorDTO.getId()); // Find actor by ID
                    if (actor != null) {
                        System.out.println("260");

                        //If the list doesnt contain the actor, add it
                        if (!movie.getActors().contains(actor)) {
                            movie.getActors().add(actor);
                            System.out.println("264");
                        }
                    } else {
                        System.out.println("267");
                        System.out.println("Actor not found with ID: " + actorDTO.getId());
                    }
                }
                em.merge(movie);
            } else {
                System.out.println("273");
                System.out.println("Movie not found with ID: " + movieDTO.getId());
            }
            em.getTransaction().commit();
        }

    }

    public void addDirectorToMovie(MovieDTO movieDTO, DirectorDTO directorDTO) {

        // Check if directorDTO is null and print a message if it is
        if (directorDTO == null) {
            System.out.println("There was no director on this movie:" + movieDTO.getTitle());
        }
        else {

            try (EntityManager em = emf.createEntityManager()) {
                em.getTransaction().begin();

                // Find movie and director by ID
                Movie movie = em.find(Movie.class, movieDTO.getId());
                Director director = em.find(Director.class, directorDTO.getId());
                // If movie and director are not null, add director to movie
                if (movie != null && director != null) {
                    if (movie.getDirector() != null) {
                        movie.getDirector().removeMovie(movie);
                    }
                    movie.setDirector(director);
                    em.merge(movie);
                } else {
                    System.out.println("Movie or Director not found with IDs: " + movieDTO.getId() + ", " + directorDTO.getId());
                }
                em.getTransaction().commit();
            }
        }
    }

    public void addGenresToMovie(MovieDTO movieDTO, List<GenreDTO> genreDTOS) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Find movie by ID
            Movie movie = em.find(Movie.class, movieDTO.getId());

            if (movie != null) { // If movie is not null, add genres to movie
                if (movie.getGenres() == null) {
                    movie.setGenres(new ArrayList<>()); // Initialize the list if null
                }
                for (GenreDTO genreDTO : genreDTOS) {
                    if (genreDTO.getId() != null) { // Ensure genreDTO id is not null
                        Genre genre = em.find(Genre.class, genreDTO.getId());
                        if (genre != null) {
                            // If the list doesn't contain the genre, add it
                            if (!movie.getGenres().contains(genre)) {
                                movie.getGenres().add(genre);
                            }
                        } else {
                            System.out.println("Genre not found with ID: " + genreDTO.getId());
                        }
                    } else {
                        System.out.println("GenreDTO ID is null");
                    }
                }
                em.merge(movie);
            } else {
                System.out.println("Movie not found with ID: " + movieDTO.getId());
            }
            em.getTransaction().commit();
        }
    }
}

