package dat.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import dat.entities.Actor;
import dat.enums.Gender;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

@JsonIgnoreProperties(ignoreUnknown = true)
public class ActorDTO {
    @JsonSetter("adult")
    private boolean adult;

    @JsonSetter("also_known_as")
    private List<String> alsoKnownAs;

    @JsonSetter("biography")
    private String biography;

    @JsonSetter("birthday")
    private String birthday;

    @JsonSetter("deathday")
    private String deathday;

    @JsonSetter("gender")
    private Gender gender;

    @JsonSetter("homepage")
    private String homepage;

    @JsonSetter("id")
    private int id;

    @JsonSetter("imdb_id")
    private String imdbId;

    @JsonSetter("known_for_department")
    private String knownForDepartment;

    @JsonSetter("name")
    private String name;

    @JsonSetter("original_name")
    private String original_name;

    @JsonSetter("place_of_birth")
    private String placeOfBirth;

    @JsonSetter("popularity")
    private double popularity;

    @JsonSetter("profile_path")
    private String profilePath;

    public ActorDTO(Actor actor) {
        this.id = actor.getId();
        this.name = actor.getName();
        this.original_name = actor.getOriginal_name();
        this.adult = actor.isAdult();
        this.profilePath = actor.getProfile_path();
        this.gender = actor.getGender();
        this.popularity = actor.getPopularity();
    }
}
