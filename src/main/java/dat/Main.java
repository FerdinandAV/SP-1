package dat;

import dat.config.HibernateConfig;
import dat.services.MovieService;
import jakarta.persistence.EntityManagerFactory;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("sp1");

        try {
            MovieService.FillDBUpLast5yearsDanish(String.valueOf(2));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}