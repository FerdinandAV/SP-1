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
import dat.services.MovieService;
import jakarta.persistence.EntityManagerFactory;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {

        MovieDAO movieDao = new MovieDAO();
        GenreDAO genreDAO = new GenreDAO();

        try {

            //Fill database up with movies
            MovieService.FillDBUpLast5yearsDanish2(1);

            //Get all movies from database
            Set<MovieDTO> movies = movieDao.getAllMovies();

            //Fill database up with actors based on movies
            ActorService.fillDBWithActors(movies);

            //Set actors and directors for movies
            MovieService.addActorsAndDirectorsForMovies(new ArrayList<>(movies));

            //Fill database up with genres
            GenreService.fillDBWithGenres();

            //Set genres for movies
            MovieService.addGenresToMovies(new ArrayList<>(movies));


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
