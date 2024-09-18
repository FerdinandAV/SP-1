package dat.DTO;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import dat.entities.Actor;
import dat.entities.Director;
import dat.entities.Movie;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDTO {
    /*@JsonSetter("backdrop_path")
    private String backdrop_path;*/

    @JsonIgnore
    private Integer id;

    @JsonSetter("id")
    private Long tmdb_id;

    @JsonSetter("title")
    private String title;

    @JsonSetter("original_title")
    private String original_title;

    @JsonSetter("overview")
    private String overview;

    @JsonSetter("poster_path")
    private String poster_path;

    @JsonSetter("media_type")
    private String media_type;

    @JsonSetter("adult")
    private boolean adult;

    @JsonSetter("original_language")
    private String original_language;

    @JsonSetter("genre_ids")
    private List<Integer> genre_ids;

    @JsonSetter("popularity")
    private float popularity;

    @JsonSetter("release_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate release_date;

    @JsonSetter("video")
    private boolean video;

    @JsonSetter("vote_average")
    private float vote_average;

    @JsonSetter("vote_count")
    private Integer vote_count;

    /*private List<Actor> actors;
    private Director director;*/

    public MovieDTO(Movie movie) {
        this.id = movie.getId();
        this.tmdb_id = movie.getTmdb_id();
        this.title = movie.getTitle();
        this.original_title = movie.getOriginal_title();
        this.release_date = movie.getRelease_date();
        this.overview = movie.getOverview();
        this.adult = movie.isAdult();
        this.original_language = movie.getOriginal_language();
        //this.backdrop_path = movie.getBackdrop_path();
        this.poster_path = movie.getPoster_path();
        this.popularity = movie.getPopularity();
        this.vote_average = movie.getVote_average();
        this.vote_count = movie.getVote_count();
        this.video = movie.isVideo();
        //this.actors = movie.getActors();
        //this.actors = new Actor(movie.getActors());
        //this.director = getDirector();
        //this.director = new Director(movie.getDirector());
    }
}
