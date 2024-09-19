package dat.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import dat.entities.Director;
import dat.entities.Genre;
import dat.enums.Gender;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

// Annotation to ignore unknown properties in the JSON file
@JsonIgnoreProperties(ignoreUnknown = true)
public class DirectorDTO {
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

    @JsonSetter("place_of_birth")
    private String placeOfBirth;

    /*@JsonSetter("popularity")
    private double popularity;*/

    @JsonSetter("profile_path")
    private String profilePath;

    @JsonIgnore
    private List<MovieDTO> movies;

    @JsonIgnore
    private List<Genre> genres;

    // Constructor to convert Director to DirectorDTO

    public DirectorDTO(Director director) {
        this.id = director.getId();
        this.name = director.getName();
        this.adult = director.isAdult();
        //this.popularity = director.getPopularity();
        this.gender = director.getGender();
        this.profilePath = director.getProfile_path();
    }
}
