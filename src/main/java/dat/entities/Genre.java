package dat.entities;


import dat.DTO.GenreDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity

@Table(name = "genre")


public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer tmdbId;

    @ManyToMany(mappedBy = "genres")
    private List<Movie> movies;

    public void addMovie(Movie movie) {
        if (movies == null) {
            movies = new ArrayList<>();
        }
        movies.add(movie);
    }

    public void removeMovie(Movie movie) {
        if (movies != null) {
            movies.remove(movie);
        }
    }

    @Column(nullable = false)
    private String genre;

    public Genre(GenreDTO genreDTO) {
        this.genre = genreDTO.getGenre();
        this.tmdbId = genreDTO.getTmdbId();
    }

}
