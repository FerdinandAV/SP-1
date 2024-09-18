package dat;

import dat.DTO.MovieDTO;
import dat.config.HibernateConfig;
import dat.daos.MovieDAO;
import dat.services.MovieService;
import jakarta.persistence.EntityManagerFactory;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        try {
            List<MovieDTO> movieDTOS = MovieService.FillDBUpLast5yearsDanish(58);
            //MovieDAO.createMovie(movieDTOS.get(0));
            System.out.println("Movies added to DB: " + movieDTOS.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}