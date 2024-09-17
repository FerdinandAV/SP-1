package dat.DTO;


import com.fasterxml.jackson.annotation.JsonSetter;
import dat.entities.Actor;
import dat.entities.Director;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class MovieDTO {
    @JsonSetter("backdrop_path")
    private String backdrop_path;
    @JsonSetter("id")
    private Integer id;
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
    private LocalDateTime release_date;
    @JsonSetter("video")
    private boolean video;
    @JsonSetter("vote_average")
    private float vote_average;
    @JsonSetter("vote_count")
    private Integer vote_count;

    private List<Actor> actors;
    private List<Director> dirctors;

    public MovieDTO(ActorDTO person, DirectorDTO director) {
        this.actors = actors;
        this.dirctors = dirctors;
    }
}
