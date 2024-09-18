package dat.entities;


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

@Entity
@Table(name="actors")
public class Actor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String original_name;

    @Column(nullable = false)
    private String media_type;

    @Column(nullable = false)
    private boolean adult;

    @Column(nullable = false)
    private String character;

    @Column(nullable = false)
    private String profile_path;

    @Column(nullable = false)
    private Gender gender;

    /*@Column(nullable = false)
    private double popularity;*/


    public Actor(ActorDTO actorDTO) {
        this.id = actorDTO.getId();
        this.name = actorDTO.getName();
        this.original_name = getOriginal_name();
        this.media_type = getMedia_type();
        this.adult = actorDTO.isAdult();
        this.character = actorDTO.getBiography();
        this.profile_path = actorDTO.getProfilePath();
        this.gender = getGender();
        //this.popularity = getPopularity();
    }


}
