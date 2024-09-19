package dat.entities;

import dat.DTO.MovieDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Long tmdb_id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String original_title;

    @Column(nullable = false)
    private LocalDate release_date;

    @Column(nullable = false, length = 1024)
    private String overview;

    @Column(nullable = false)
    private boolean adult;

    @Column(nullable = false)
    private String original_language;

    @Column(length = 512)
    private String poster_path;

    @Column(nullable = false)
    private float popularity;

    @Column(nullable = false)
    private float vote_average;

    @Column(nullable = false)
    private Integer vote_count;

    @Column(nullable = false)
    private boolean video;

    @ManyToOne
    @JoinColumn(name = "director_id")
    private Director director;

    @ManyToMany
    @JoinTable(
            name = "movie_actor",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    private List<Actor> actors;

    @ManyToMany
    @JoinTable(
            name = "movie_genre",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres;

    // Constructor to create a Movie object from a MovieDTO object
    public Movie(MovieDTO movieDTO) {
        this.id = movieDTO.getId();
        this.tmdb_id = movieDTO.getTmdb_id();
        this.title = movieDTO.getTitle();
        this.original_title = movieDTO.getOriginal_title();
        this.release_date = movieDTO.getRelease_date();
        this.overview = movieDTO.getOverview();
        this.adult = movieDTO.isAdult();
        this.original_language = movieDTO.getOriginal_language();
        this.poster_path = movieDTO.getPoster_path();
        this.popularity = movieDTO.getPopularity();
        this.vote_average = movieDTO.getVote_average();
        this.vote_count = movieDTO.getVote_count();
    }
}





