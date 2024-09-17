package dat.entities;


import jakarta.persistence.*;
import lombok.*;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name="directors")
@Entity

public class Director {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
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
   private String Characters;

   @Column(nullable = false)
   private String profile_path;

   @Column(nullable = false)
   private Integer gender;

   @Column(nullable = false)
   private float popularity;


}
