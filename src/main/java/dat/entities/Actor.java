package dat.entities;


import com.fasterxml.jackson.annotation.JsonSetter;
import dat.DTO.ActorDTO;
import dat.DTO.DirectorDTO;
import dat.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name="actors")
public class Actor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(nullable = true)
    private String imdbId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String original_name;

    @Column(nullable = false)
    private String media_type;

    @Column(nullable = false)
    private boolean adult;

    @Column(nullable = true, length = 5000)
    private String character;

    @Column(nullable = false)
    private String profile_path;

    /*@Column(nullable = true)
    private Gender gender;*/

    /*@Column(nullable = false)
    private double popularity;*/


    // Constructor to create an Actor object from an ActorDTO object
    public Actor(ActorDTO actorDTO) {
        this.id = actorDTO.getId();
        this.name = actorDTO.getName();
        this.original_name = actorDTO.getOriginal_name();
        this.media_type = getMedia_type();
        this.adult = actorDTO.isAdult();
        this.character = actorDTO.getBiography();
        this.profile_path = actorDTO.getProfilePath();

        //this.gender = actorDTO.getGender();
        this.imdbId = actorDTO.getImdbId();

        //this.popularity = getPopularity();
    }


}
