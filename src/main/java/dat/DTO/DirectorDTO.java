package dat.DTO;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

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
    private int gender;

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

    @JsonSetter("popularity")
    private double popularity;

    @JsonSetter("profile_path")
    private String profilePath;
}
