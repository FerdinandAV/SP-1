package dat.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Actor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private long id;
    private String name;

}
