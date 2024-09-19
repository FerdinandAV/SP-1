package dat.daos;

import dat.DTO.DirectorDTO;
import dat.DTO.MovieDTO;
import dat.config.HibernateConfig;
import dat.entities.Movie;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DirectorDAOTest {

    private static DirectorDAO directorDAO;
    private static MovieDAO movieDAO; // Assume you have a MovieDAO class for movie operations

    private static EntityManagerFactory emf;

    @BeforeAll
    static void setUp() {
        emf = HibernateConfig.getEntityManagerFactory("sp1");
        directorDAO = new DirectorDAO();
        movieDAO = new MovieDAO(); // Initialize MovieDAO if needed
    }




}
