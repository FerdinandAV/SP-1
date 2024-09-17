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

    @Column(nullable = false)
    private List<Integer> genre_ids;

    @Column(nullable = false)
    private List<Actor> actors;

    @Column(nullable = false)
    private List<Director> dirctors;

}
