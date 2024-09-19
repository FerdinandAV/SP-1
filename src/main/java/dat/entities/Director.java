package dat.entities;

import java.util.ArrayList;
import java.util.List;

import dat.DTO.DirectorDTO;
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

   @Column(nullable = true)
   private String imdbId;
  
   @Column(nullable = true)
   private String name;

   @Column(nullable = true)
   private String original_name;

   @Column(nullable = true)
   private String media_type;

   @Column(nullable = true)
   private boolean adult;

   /*@Column(nullable = false)
   private double popularity;*/

   @Column(nullable = true)
   private Gender gender;

   @Column(nullable = true)
   private String known_for_department;

   @Column(nullable = true)
   private String profile_path;

   @OneToMany(mappedBy = "director")
   private List<Movie> movies;

   public void addMovie(Movie movie) {
      if (movies == null) {
         movies = new ArrayList<>();
      }
      movies.add(movie);
   }

    public void removeMovie(Movie movie) {
        if (movies != null) {
            movies.remove(movie);
        }
    }

   // Constructor to create a Director object from a DirectorDTO object
   public  Director(DirectorDTO directorDTO) {
      this.id = directorDTO.getId();
      this.name = directorDTO.getName();
      this.original_name = getOriginal_name();
      this.media_type = getMedia_type();
      this.adult = directorDTO.isAdult();
      this.profile_path = directorDTO.getProfilePath();
      this.gender = getGender();
      this.imdbId = directorDTO.getImdbId();
      //this.popularity = getPopularity();
   }

}
