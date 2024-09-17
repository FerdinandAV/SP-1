package dat.entities;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity

@Table(name="movies")

public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String original_title;

    @Column(nullable = false)
    private LocalDateTime release_date;

    @Column(nullable = false)
    private String overview;

    @Column(nullable = false)
    private boolean adult;

    @Column(nullable = false)
    private String original_language;

    @Column(nullable = false)
    private String backdrop_path;

    @Column(nullable = false)
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
            //@JoinTable annotation defines
            // the intermediary table movie_genre that connects movies and genres.
            name = "movie_actor",

            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    private List<Actor> actors;



    @ManyToMany
    @JoinTable(
            //The @JoinTable annotation defines the name of the intermediary table,
            // movie_actor, which connects the movies and actors tables.
            name = "movie_genre",

            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres;

}
