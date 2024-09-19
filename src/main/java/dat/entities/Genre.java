package dat.entities;


import jakarta.persistence.*;
import lombok.*;
import java.util.List;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity

@Table(name="genre")


public class Genre {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Integer id;

 /*@ManyToMany(mappedBy = "genres")
 private List<Movie> movies;*/

 @Column(nullable = false)
 private String genre;

}
