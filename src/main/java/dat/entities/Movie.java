package dat.entities;

import dat.DTO.MovieDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

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

    public void setDirector(Director director){
        this.director = director;
        director.addMovie(this);
    }

    public void removeDirector(){
        if (director != null){
            this.director.removeMovie(this);
            director = null;

        }
    }

    @ManyToMany
    @JoinTable(
            name = "movie_actor",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    private List<Actor> actors;

    // Adds actors to the movie entity and adds the movie to the actor entity
    public void addActor(Actor actor){
        if (actors == null){
            actors = new ArrayList<>();
        }
        actors.add(actor);
        actor.addMovie(this);
    }

    // Removes actors from the movie entity and removes the movie from the actor entity
    public void removeActor(Actor actor){
        if (actors != null){
            actors.remove(actor);
            actor.removeMovie(this);
        }
    }

    @ManyToMany
    @JoinTable(
            name = "movie_genre",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres;

    // Adds genres to the movie entity and adds the movie to the genre entity
    public void addGenre(Genre genre){
        if (genres == null){
            genres = new ArrayList<>();
        }
        genres.add(genre);
        genre.addMovie(this);
    }

    // Removes genres from the movie entity and removes the movie from the genre entity
    public void removeGenre(Genre genre){
        if (genres != null){
            genres.remove(genre);
            genre.removeMovie(this);
        }
    }

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





