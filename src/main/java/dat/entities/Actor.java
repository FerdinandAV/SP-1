package dat.entities;


import dat.DTO.ActorDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = true)
    private String name;

    @Column(nullable = true)
    private String original_name;

    /*@Column(nullable = false)
    private String media_type;*/

    @Column(nullable = true)
    private boolean adult;

    @Column(nullable = true, length = 5000)
    private String character;

    @Column(nullable = true)
    private String profile_path;

    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private double popularity;

    @ManyToMany(mappedBy = "actors")
    private List<Movie> movies;

    public void addMovie(Movie movie){
        if (movies == null){
            movies = new ArrayList<>();
        }
        movies.add(movie);
    }

    public void removeMovie(Movie movie){
        if (movies != null){
            movies.remove(movie);
        }
    }

    // Constructor to create an Actor object from an ActorDTO object
    public Actor(ActorDTO actorDTO) {
        this.id = actorDTO.getId();
        this.name = actorDTO.getName();
        this.original_name = actorDTO.getOriginal_name();
        //this.media_type = getMedia_type();
        this.adult = actorDTO.isAdult();
        this.character = actorDTO.getBiography();
        this.profile_path = actorDTO.getProfilePath();
        //this.gender = actorDTO.getGender();
        this.imdbId = actorDTO.getImdbId();
        //this.popularity = getPopularity();
    }
}