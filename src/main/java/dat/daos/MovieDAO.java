package dat.daos;

import dat.DTO.MovieDTO;
import dat.config.HibernateConfig;
import dat.entities.Director;
import dat.entities.Movie;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MovieDAO {

    static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("SP1");

    public static MovieDTO createMovie(MovieDTO movieDTO) {
        Movie movie = new Movie(movieDTO);
        try (EntityManager em = emf.createEntityManager()) {
            //Convert DTO to Entity
            em.getTransaction().begin();

            //Check if movie already exists
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m WHERE m.title = :title", Movie.class);
            query.setParameter("title", movie.getTitle());
            if (query.getResultList().isEmpty()) {
                try {
                    em.persist(movie);
                } catch (EntityExistsException e) {
                    System.out.println("Movie already exists");
                }
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
                TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m WHERE m.title = :title", Movie.class);
                query.setParameter("title", movie.getTitle());
                if (query.getResultList().isEmpty()) {
                    em.persist(movie);
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
        Movie movie = new Movie(movieDTO);
        try (EntityManager em = emf.createEntityManager()) {
            //Convert DTO to Entity
            em.getTransaction().begin();

            //Update movie
            em.merge(movie);
            em.getTransaction().commit();
        }

        return new MovieDTO(movie);
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
