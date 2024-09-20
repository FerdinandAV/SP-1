package dat;

import dat.DTO.ActorDTO;
import dat.DTO.GenreDTO;
import dat.DTO.MovieDTO;
import dat.config.HibernateConfig;
import dat.daos.GenreDAO;
import dat.daos.MovieDAO;
import dat.entities.Genre;
import dat.entities.Movie;
import dat.services.ActorService;
import dat.services.GenreService;
import dat.services.DirectorService;
import dat.services.MovieService;
import jakarta.persistence.EntityManagerFactory;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        try {
            //Fill database up with movies
            MovieService.FillDBUpLast5yearsDanish(5);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
