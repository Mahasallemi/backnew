package tn.esprit.PI.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BonDeTravail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String description;

    @CreationTimestamp
    @Column(nullable = false)
    LocalDate dateCreation;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    StatutBonTravail statut;

    @ManyToOne
    @JoinColumn(name = "technicien_id", nullable = false)
    User technicien;





    @OneToMany(mappedBy = "bon", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<BonTravailComponent> composants;



}

// Enumération des statuts du Bon de Travail
