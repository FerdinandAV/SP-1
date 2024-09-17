package dat.entities;

import java.util.List;
import dat.enums.Gender;
import jakarta.persistence.*;
import lombok.*;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity

@Table(name="directors")

public class Director {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Integer id;

   @Column(nullable = false)
   private String name;

   @OneToMany(mappedBy = "director")
   private List<Movie> movies;

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

   @Column(nullable = false)
   private String popularity;


}
