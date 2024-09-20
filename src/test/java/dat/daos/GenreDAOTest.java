package dat.daos;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GenreDAOTest {

    static GenreDAO genreDAO;

    @BeforeAll
    static void setup() {
        genreDAO = new GenreDAO();
    }

    @Test
    void createGenres() {
    }

    // Fetching all genres
    @Test
    void getAllGenres() {
        GenreDAO.getAllGenres().forEach(genreDTO -> System.out.println(genreDTO.getGenre()));
    }

    @Test
    void getGenreByTMDBID() {
    }

    @Test
    void findGenreByTMDBID() {
    }

    // Calling the method searchByGenre
    // here we are searching for the genre "Action"
    @Test
    void searchByGenre() {
        genreDAO.searchByGenre("Action").forEach(movieDTO -> System.out.println(movieDTO.getTitle()));
    }
}