package dat.entities;


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
    private int gender;

    @Column(nullable = false)
    private double popularity;

}
