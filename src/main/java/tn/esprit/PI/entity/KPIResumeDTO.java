package tn.esprit.PI.entity;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KPIResumeDTO {
    
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String periode;
    
    // KPI principaux sous forme simplifiée
    private KPISimpleDTO mttr;
    private KPISimpleDTO mtbf;
    private KPISimpleDTO tauxExecution;
    private KPISimpleDTO disponibilite;
    
    // Alertes et recommandations
    private List<String> alertes;
    private List<String> recommandations;
    
    // Score global de performance (0-100)
    private Double scorePerformance;
}
