package tn.esprit.PI.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.PI.repository.BonDeTravailRepository;
import tn.esprit.PI.repository.DemandeInterventionRepository;
import tn.esprit.PI.repository.TesteurRepository;
import tn.esprit.PI.entity.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KPIService {

    @Autowired
    private DemandeInterventionRepository demandeRepository;
    
    @Autowired
    private BonDeTravailRepository bonTravailRepository;
    
    @Autowired
    private TesteurRepository testeurRepository;

    /**
     * Calcule le dashboard KPI pour une période donnée
     */
    public KPIDashboardDTO calculerKPIDashboard(String periode) {
        LocalDate[] dates = calculerDatesParPeriode(periode);
        LocalDate dateDebut = dates[0];
        LocalDate dateFin = dates[1];
        
        System.out.println("📊 Calcul KPI pour période: " + periode + " (" + dateDebut + " à " + dateFin + ")");
        
        // Récupérer les données pour la période
        List<DemandeIntervention> demandes = demandeRepository.findByDateDemandeBetween(dateDebut, dateFin);
        List<BonDeTravail> bonsTravail = bonTravailRepository.findByDateCreationBetween(dateDebut, dateFin);
        
        // Calculer les KPI
        Double mttr = calculerMTTR(bonsTravail);
        Double mtbf = calculerMTBF(demandes);
        Double tauxExecution = calculerTauxExecution(demandes);
        Double disponibilite = calculerDisponibilite(demandes, bonsTravail);
        
        // Calculer les statistiques détaillées
        KPIDashboardDTO.KPIStatistiques stats = calculerStatistiques(demandes, bonsTravail);
        
        // Calculer les tendances (comparaison avec période précédente)
        KPIDashboardDTO.KPITendances tendances = calculerTendances(periode, mttr, mtbf, tauxExecution, disponibilite);
        
        return KPIDashboardDTO.builder()
                .dateDebut(dateDebut)
                .dateFin(dateFin)
                .periode(periode)
                .mttr(mttr)
                .mtbf(mtbf)
                .tauxExecution(tauxExecution)
                .disponibilite(disponibilite)
                .statistiques(stats)
                .tendances(tendances)
                .build();
    }

    /**
     * MTTR = Mean Time To Repair
     * Moyenne des durées de réparation (date_fin - date_debut) des interventions terminées
     */
    private Double calculerMTTR(List<BonDeTravail> bonsTravail) {
        List<BonDeTravail> bonsTermines = bonsTravail.stream()
                .filter(bon -> bon.getStatut() == StatutBonTravail.TERMINE)
                .filter(bon -> bon.getDateDebut() != null && bon.getDateFin() != null)
                .collect(Collectors.toList());
        
        if (bonsTermines.isEmpty()) {
            System.out.println("⚠️ Aucun bon de travail terminé trouvé pour MTTR");
            return 0.0;
        }
        
        double totalHeures = bonsTermines.stream()
                .mapToLong(bon -> ChronoUnit.DAYS.between(bon.getDateDebut(), bon.getDateFin()) * 24)
                .sum();
        
        double mttr = totalHeures / bonsTermines.size();
        System.out.println("🔧 MTTR calculé: " + mttr + " heures (" + bonsTermines.size() + " interventions)");
        return mttr;
    }

    /**
     * MTBF = Mean Time Between Failures
     * Moyenne du temps entre les pannes par équipement
     */
    private Double calculerMTBF(List<DemandeIntervention> demandes) {
        // Grouper les demandes curatives par équipement (utilisation du testeur comme équipement)
        List<Curative> demandesCuratives = demandes.stream()
                .filter(d -> d instanceof Curative)
                .map(d -> (Curative) d)
                .collect(Collectors.toList());
        
        if (demandesCuratives.size() < 2) {
            System.out.println("⚠️ Pas assez de pannes pour calculer MTBF");
            return 0.0;
        }
        
        // Calculer le temps moyen entre les pannes (simplifié)
        long totalJours = 0;
        for (int i = 1; i < demandesCuratives.size(); i++) {
            LocalDate datePrecedente = demandesCuratives.get(i-1).getDateDemande().toInstant()
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            LocalDate dateCourante = demandesCuratives.get(i).getDateDemande().toInstant()
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            totalJours += ChronoUnit.DAYS.between(datePrecedente, dateCourante);
        }
        
        double mtbf = (totalJours * 24.0) / (demandesCuratives.size() - 1);
        System.out.println("⚡ MTBF calculé: " + mtbf + " heures (" + demandesCuratives.size() + " pannes)");
        return mtbf;
    }

    /**
     * Taux d'exécution = Interventions clôturées / Interventions planifiées
     */
    private Double calculerTauxExecution(List<DemandeIntervention> demandes) {
        if (demandes.isEmpty()) {
            return 0.0;
        }
        
        long interventionsTerminees = demandes.stream()
                .filter(d -> d.getStatut() == StatutDemande.TERMINEE)
                .count();
        
        double taux = (interventionsTerminees * 100.0) / demandes.size();
        System.out.println("📈 Taux d'exécution: " + taux + "% (" + interventionsTerminees + "/" + demandes.size() + ")");
        return taux;
    }

    /**
     * Disponibilité = Temps fonctionnement / (Temps fonctionnement + Temps panne)
     * Basé sur les interventions curatives (pannes)
     */
    private Double calculerDisponibilite(List<DemandeIntervention> demandes, List<BonDeTravail> bonsTravail) {
        // Temps total de la période en heures
        long joursTotal = 30; // Par défaut 30 jours
        double heuresTotal = joursTotal * 24.0;
        
        // Temps de panne = somme des durées des interventions curatives
        double tempsPanne = bonsTravail.stream()
                .filter(bon -> bon.getStatut() == StatutBonTravail.TERMINE)
                .filter(bon -> bon.getDateDebut() != null && bon.getDateFin() != null)
                .mapToLong(bon -> ChronoUnit.DAYS.between(bon.getDateDebut(), bon.getDateFin()) * 24)
                .sum();
        
        double tempsFonctionnement = heuresTotal - tempsPanne;
        double disponibilite = (tempsFonctionnement / heuresTotal) * 100.0;
        
        System.out.println("🟢 Disponibilité: " + disponibilite + "% (" + tempsFonctionnement + "h/" + heuresTotal + "h)");
        return Math.max(0.0, Math.min(100.0, disponibilite));
    }

    /**
     * Calcule les statistiques détaillées
     */
    private KPIDashboardDTO.KPIStatistiques calculerStatistiques(List<DemandeIntervention> demandes, List<BonDeTravail> bonsTravail) {
        // Statistiques interventions
        long totalInterventions = demandes.size();
        long interventionsTerminees = demandes.stream()
                .filter(d -> d.getStatut() == StatutDemande.TERMINEE)
                .count();
        long interventionsEnCours = demandes.stream()
                .filter(d -> d.getStatut() == StatutDemande.EN_COURS)
                .count();
        long interventionsEnAttente = demandes.stream()
                .filter(d -> d.getStatut() == StatutDemande.EN_ATTENTE)
                .count();
        
        // Types d'interventions
        long interventionsCuratives = demandes.stream()
                .filter(d -> d instanceof Curative)
                .count();
        long interventionsPreventives = demandes.stream()
                .filter(d -> d instanceof Preventive)
                .count();
        
        // Équipements (basé sur les testeurs)
        long nombreEquipements = testeurRepository.count();
        
        // Composants utilisés
        long totalComposants = bonsTravail.stream()
                .flatMap(bon -> bon.getComposants().stream())
                .mapToLong(comp -> comp.getQuantiteUtilisee())
                .sum();
        
        return KPIDashboardDTO.KPIStatistiques.builder()
                .totalInterventions(totalInterventions)
                .interventionsTerminees(interventionsTerminees)
                .interventionsEnCours(interventionsEnCours)
                .interventionsEnAttente(interventionsEnAttente)
                .interventionsCuratives(interventionsCuratives)
                .interventionsPreventives(interventionsPreventives)
                .nombreEquipements(nombreEquipements)
                .totalComposantsUtilises(totalComposants)
                .build();
    }

    /**
     * Calcule les tendances par rapport à la période précédente
     */
    private KPIDashboardDTO.KPITendances calculerTendances(String periode, Double mttr, Double mtbf, Double tauxExecution, Double disponibilite) {
        // Calculer la période précédente
        LocalDate[] datesPrecedentes = calculerPeriodePrecedente(periode);
        
        // Récupérer les KPI de la période précédente
        KPIDashboardDTO kpiPrecedent = calculerKPIPourPeriode(datesPrecedentes[0], datesPrecedentes[1]);
        
        // Calculer les évolutions
        Double mttrTendance = calculerPourcentageEvolution(kpiPrecedent.getMttr(), mttr);
        Double mtbfTendance = calculerPourcentageEvolution(kpiPrecedent.getMtbf(), mtbf);
        Double tauxExecutionTendance = calculerPourcentageEvolution(kpiPrecedent.getTauxExecution(), tauxExecution);
        Double disponibiliteTendance = calculerPourcentageEvolution(kpiPrecedent.getDisponibilite(), disponibilite);
        
        return KPIDashboardDTO.KPITendances.builder()
                .mttrTendance(mttrTendance)
                .mtbfTendance(mtbfTendance)
                .tauxExecutionTendance(tauxExecutionTendance)
                .disponibiliteTendance(disponibiliteTendance)
                .mttrEvolution(determinerEvolution(mttrTendance, true)) // true = plus bas = mieux
                .mtbfEvolution(determinerEvolution(mtbfTendance, false)) // false = plus haut = mieux
                .tauxExecutionEvolution(determinerEvolution(tauxExecutionTendance, false))
                .disponibiliteEvolution(determinerEvolution(disponibiliteTendance, false))
                .build();
    }

    /**
     * Calcule les dates de début et fin selon la période
     */
    private LocalDate[] calculerDatesParPeriode(String periode) {
        LocalDate dateFin = LocalDate.now();
        LocalDate dateDebut;
        
        switch (periode.toUpperCase()) {
            case "AUJOURD_HUI":
                dateDebut = dateFin;
                break;
            case "7_JOURS":
                dateDebut = dateFin.minusDays(7);
                break;
            case "30_JOURS":
                dateDebut = dateFin.minusDays(30);
                break;
            case "MENSUEL":
                dateDebut = dateFin.withDayOfMonth(1);
                break;
            case "GLOBAL":
                dateDebut = LocalDate.of(2020, 1, 1); // Date très ancienne
                break;
            default:
                dateDebut = dateFin.minusDays(30);
        }
        
        return new LocalDate[]{dateDebut, dateFin};
    }

    /**
     * Calcule la période précédente pour les tendances
     */
    private LocalDate[] calculerPeriodePrecedente(String periode) {
        LocalDate[] datesCourantes = calculerDatesParPeriode(periode);
        LocalDate dateDebut = datesCourantes[0];
        LocalDate dateFin = datesCourantes[1];
        
        long duree = ChronoUnit.DAYS.between(dateDebut, dateFin);
        
        LocalDate finPrecedente = dateDebut.minusDays(1);
        LocalDate debutPrecedente = finPrecedente.minusDays(duree);
        
        return new LocalDate[]{debutPrecedente, finPrecedente};
    }

    /**
     * Calcule les KPI pour une période spécifique
     */
    private KPIDashboardDTO calculerKPIPourPeriode(LocalDate dateDebut, LocalDate dateFin) {
        List<DemandeIntervention> demandes = demandeRepository.findByDateDemandeBetween(dateDebut, dateFin);
        List<BonDeTravail> bonsTravail = bonTravailRepository.findByDateCreationBetween(dateDebut, dateFin);
        
        return KPIDashboardDTO.builder()
                .mttr(calculerMTTR(bonsTravail))
                .mtbf(calculerMTBF(demandes))
                .tauxExecution(calculerTauxExecution(demandes))
                .disponibilite(calculerDisponibilite(demandes, bonsTravail))
                .build();
    }

    /**
     * Calcule le pourcentage d'évolution entre deux valeurs
     */
    private Double calculerPourcentageEvolution(Double valeurPrecedente, Double valeurCourante) {
        if (valeurPrecedente == null || valeurPrecedente == 0.0) {
            return valeurCourante != null && valeurCourante > 0 ? 100.0 : 0.0;
        }
        
        if (valeurCourante == null) {
            return -100.0;
        }
        
        return ((valeurCourante - valeurPrecedente) / valeurPrecedente) * 100.0;
    }

    /**
     * Détermine si l'évolution est une amélioration, dégradation ou stable
     */
    private String determinerEvolution(Double pourcentage, boolean plusBasMieux) {
        if (pourcentage == null || Math.abs(pourcentage) < 5.0) {
            return "STABLE";
        }
        
        if (plusBasMieux) {
            return pourcentage < 0 ? "AMELIORATION" : "DEGRADATION";
        } else {
            return pourcentage > 0 ? "AMELIORATION" : "DEGRADATION";
        }
    }

    /**
     * Récupère les KPI par atelier
     */
    public Map<String, KPIDashboardDTO> getKPIParAtelier(String periode) {
        List<Testeur> testeurs = testeurRepository.findAll();
        
        return testeurs.stream()
                .map(Testeur::getAtelier)
                .distinct()
                .collect(Collectors.toMap(
                    atelier -> atelier,
                    atelier -> calculerKPIParAtelier(atelier, periode)
                ));
    }

    /**
     * Calcule les KPI pour un atelier spécifique
     */
    private KPIDashboardDTO calculerKPIParAtelier(String atelier, String periode) {
        LocalDate[] dates = calculerDatesParPeriode(periode);
        
        // Filtrer les données par atelier (logique à adapter selon votre modèle)
        List<DemandeIntervention> demandes = demandeRepository.findByDateDemandeBetween(dates[0], dates[1]);
        List<BonDeTravail> bonsTravail = bonTravailRepository.findByDateCreationBetween(dates[0], dates[1]);
        
        return KPIDashboardDTO.builder()
                .dateDebut(dates[0])
                .dateFin(dates[1])
                .periode(periode)
                .mttr(calculerMTTR(bonsTravail))
                .mtbf(calculerMTBF(demandes))
                .tauxExecution(calculerTauxExecution(demandes))
                .disponibilite(calculerDisponibilite(demandes, bonsTravail))
                .statistiques(calculerStatistiques(demandes, bonsTravail))
                .build();
    }

    /**
     * Récupère l'historique des KPI pour graphiques
     */
    public List<KPIDashboardDTO> getHistoriqueKPI(String periode, int nombrePeriodes) {
        return java.util.stream.IntStream.range(0, nombrePeriodes)
                .mapToObj(i -> {
                    LocalDate[] dates = calculerDatesDecalees(periode, i);
                    return calculerKPIPourPeriode(dates[0], dates[1]);
                })
                .collect(Collectors.toList());
    }

    /**
     * Calcule les dates décalées pour l'historique
     */
    private LocalDate[] calculerDatesDecalees(String periode, int decalage) {
        LocalDate dateFin = LocalDate.now().minusDays((long) decalage * getDureeEnJours(periode));
        LocalDate dateDebut;
        
        switch (periode.toUpperCase()) {
            case "7_JOURS":
                dateDebut = dateFin.minusDays(7);
                break;
            case "30_JOURS":
                dateDebut = dateFin.minusDays(30);
                break;
            case "MENSUEL":
                dateDebut = dateFin.withDayOfMonth(1);
                dateFin = dateDebut.plusMonths(1).minusDays(1);
                break;
            default:
                dateDebut = dateFin.minusDays(30);
        }
        
        return new LocalDate[]{dateDebut, dateFin};
    }

    /**
     * Retourne la durée en jours d'une période
     */
    private int getDureeEnJours(String periode) {
        switch (periode.toUpperCase()) {
            case "7_JOURS": return 7;
            case "30_JOURS": return 30;
            case "MENSUEL": return 30;
            default: return 30;
        }
    }
}
