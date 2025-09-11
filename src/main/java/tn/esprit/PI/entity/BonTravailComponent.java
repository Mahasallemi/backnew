package tn.esprit.PI.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BonTravailComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "bon_id")
    @JsonIgnore
    @JsonBackReference
    private BonDeTravail bon;


    @ManyToOne
    @JoinColumn(name = "component_id")
    private Component component;

    @Column(nullable = false)
    private int quantiteUtilisee;
}