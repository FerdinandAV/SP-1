package dat.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import dat.entities.Genre;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

@JsonIgnoreProperties(ignoreUnknown = true)
public class GenreDTO {

    @JsonIgnore
    private Integer id;

    @JsonSetter("id")
    private Integer tmdbId;

    @JsonSetter("name")
    private String genre;

    public GenreDTO(Genre genre) {
        this.id = genre.getId();
        this.tmdbId = genre.getId();
        this.genre = genre.getGenre();
    }

}
