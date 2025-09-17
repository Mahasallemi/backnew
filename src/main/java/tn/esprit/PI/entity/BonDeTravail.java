package tn.esprit.PI.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "sousProjets", "password", "token", "resetToken"})
    User technicien;

    // Association avec l'intervention (optionnelle pour compatibilité)
    @ManyToOne
    @JoinColumn(name = "intervention_id", nullable = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    DemandeIntervention intervention;

    // Association avec le testeur (équipement) (optionnelle pour compatibilité)
    @ManyToOne
    @JoinColumn(name = "testeur_code_gmao", nullable = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    Testeur testeur;

    @OneToMany(mappedBy = "bon", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<BonTravailComponent> composants;



}

// Enumération des statuts du Bon de Travail
