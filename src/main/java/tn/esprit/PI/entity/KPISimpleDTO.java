package tn.esprit.PI.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KPISimpleDTO {
    private String nom;
    private Double valeur;
    private String unite;
    private String evolution; // "AMELIORATION", "DEGRADATION", "STABLE"
    private Double tendance; // Pourcentage d'évolution
    private String description;
}
