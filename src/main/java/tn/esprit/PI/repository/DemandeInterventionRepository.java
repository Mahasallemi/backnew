package tn.esprit.PI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.PI.entity.DemandeIntervention;
import tn.esprit.PI.entity.StatutDemande;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface DemandeInterventionRepository extends JpaRepository<DemandeIntervention, Long> {
    
    @Query("SELECT d FROM DemandeIntervention d WHERE DATE(d.dateDemande) BETWEEN :dateDebut AND :dateFin")
    List<DemandeIntervention> findByDateDemandeBetween(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);
    
    @Query("SELECT d FROM DemandeIntervention d WHERE d.statut = :statut AND DATE(d.dateDemande) BETWEEN :dateDebut AND :dateFin")
    List<DemandeIntervention> findByStatutAndDateDemandeBetween(@Param("statut") StatutDemande statut, @Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);
    
    @Query("SELECT COUNT(d) FROM DemandeIntervention d WHERE d.statut = :statut AND DATE(d.dateDemande) BETWEEN :dateDebut AND :dateFin")
    Long countByStatutAndDateDemandeBetween(@Param("statut") StatutDemande statut, @Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);
    
    @Query(value = "SELECT id, description, date_demande, statut, priorite, demandeur, type_demande, " +
                   "CASE WHEN date_creation = '0000-00-00 00:00:00' THEN NULL ELSE date_creation END as date_creation, " +
                   "CASE WHEN date_validation = '0000-00-00 00:00:00' THEN NULL ELSE date_validation END as date_validation, " +
                   "COALESCE(confirmation, 0) as confirmation, " +
                   "testeur_code_gmao, technicien_id " +
                   "FROM demande_intervention", nativeQuery = true)
    List<Map<String, Object>> findAllWithNullSafeDates();
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE demande_intervention SET " +
                   "description = COALESCE(?2, description), " +
                   "statut = COALESCE(?3, statut), " +
                   "priorite = COALESCE(?4, priorite), " +
                   "technicien_id = COALESCE(?5, technicien_id) " +
                   "WHERE id = ?1", nativeQuery = true)
    int updateDemandeBasicFields(Long id, String description, String statut, String priorite, Long technicienId);
}
