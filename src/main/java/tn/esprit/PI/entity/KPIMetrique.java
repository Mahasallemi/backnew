package tn.esprit.PI.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "kpi_metriques")
public class KPIMetrique {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nom; // "MTTR", "MTBF", "TAUX_EXECUTION", "DISPONIBILITE"
    
    @Column(nullable = false)
    private Double valeur;
    
    @Column(nullable = false)
    private LocalDate dateCalcul;
    
    @Column(nullable = false)
    private LocalDate dateDebut; // Début de la période analysée
    
    @Column(nullable = false)
    private LocalDate dateFin; // Fin de la période analysée
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypePeriode typePeriode;
    
    private String equipementId; // Optionnel : pour KPI par équipement
    private String atelierId; // Optionnel : pour KPI par atelier
    
    @Column(nullable = false)
    private LocalDateTime dateCreation;
    
    public enum TypePeriode {
        AUJOURD_HUI,
        SEPT_JOURS,
        TRENTE_JOURS,
        MENSUEL,
        GLOBAL
    }
}
