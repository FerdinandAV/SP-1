package dat;

import dat.DTO.ActorDTO;
import dat.DTO.MovieDTO;
import dat.config.HibernateConfig;
import dat.daos.MovieDAO;
import dat.entities.Movie;
import dat.services.ActorService;
import dat.services.MovieService;
import jakarta.persistence.EntityManagerFactory;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        try {
            MovieService.FillDBUpLast5yearsDanish(58);

            Set<MovieDTO> movies = dao.getAllMovies();

            ActorService.fillDBWithActors(movies);


            /*for (MovieDTO movieDTO : dao.getAllMovies()) {
                List<ActorDTO> actorDTOS = ActorService.fillDBWithActors(movieDTO, 1);
                System.out.println("Actors added to DB for movie ID " + movieDTO.getId() + ": " + actorDTOS.size());
            }

            System.out.println("Movies added to DB: " + movieDTOS.size());*/
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}