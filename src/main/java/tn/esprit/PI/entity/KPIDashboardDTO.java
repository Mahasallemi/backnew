package tn.esprit.PI.entity;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KPIDashboardDTO {
    
    // Période d'analyse
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String periode; // "AUJOURD_HUI", "7_JOURS", "30_JOURS", "MENSUEL", "GLOBAL"
    
    // KPI Principaux
    private Double mttr; // Mean Time To Repair (en heures)
    private Double mtbf; // Mean Time Between Failures (en heures)
    private Double tauxExecution; // Pourcentage d'interventions clôturées
    private Double disponibilite; // Pourcentage de disponibilité
    
    // Statistiques détaillées
    private KPIStatistiques statistiques;
    
    // Tendances (comparaison avec période précédente)
    private KPITendances tendances;
    
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class KPIStatistiques {
        // Interventions
        private Long totalInterventions;
        private Long interventionsTerminees;
        private Long interventionsEnCours;
        private Long interventionsEnAttente;
        
        // Types d'interventions
        private Long interventionsCuratives;
        private Long interventionsPreventives;
        
        // Temps moyens
        private Double tempsReparationMoyen; // MTTR en heures
        private Double tempsEntreDefaillancesMoyen; // MTBF en heures
        
        // Équipements
        private Long nombreEquipements;
        private Long equipementsEnPanne;
        private Long equipementsFonctionnels;
        
        // Composants
        private Long totalComposantsUtilises;
        private Double coutTotalComposants;
    }
    
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class KPITendances {
        private Double mttrTendance; // % d'évolution vs période précédente
        private Double mtbfTendance;
        private Double tauxExecutionTendance;
        private Double disponibiliteTendance;
        
        private String mttrEvolution; // "AMELIORATION", "DEGRADATION", "STABLE"
        private String mtbfEvolution;
        private String tauxExecutionEvolution;
        private String disponibiliteEvolution;
    }
}
