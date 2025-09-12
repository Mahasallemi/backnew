package tn.esprit.PI.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.PI.entity.Curative;
import tn.esprit.PI.entity.DemandeIntervention;
import tn.esprit.PI.entity.DemandeInterventionDTO;
import tn.esprit.PI.entity.Preventive;
import tn.esprit.PI.entity.StatutDemande;
import tn.esprit.PI.entity.Testeur;
import tn.esprit.PI.entity.User;
import tn.esprit.PI.repository.DemandeInterventionRepository;
import tn.esprit.PI.repository.TesteurRepository;
import tn.esprit.PI.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DemandeInterventionService {
    @Autowired
    private DemandeInterventionRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TesteurRepository testeurRepository;

    // Ajouter une nouvelle demande


    // Récupérer une demande par ID
  /*  public Optional<DemandeInterventionDTO> getDemandeById(Long id) {
        return repository.findById(id).map(demande -> {
            String typeDemande;
            if (demande instanceof Curative) {
                typeDemande = "CURATIVE";
            } else if (demande instanceof Preventive) {
                typeDemande = "PREVENTIVE";
            } else {
                typeDemande = "INCONNU";
            }

            return new DemandeInterventionDTO(
                    demande.getId(),
                    demande.getDescription(),
                    demande.getDateDemande(),
                    demande.getStatut(),
                    demande.getPriorite(),
                    demande.getDemandeur() != null ? demande.getDemandeur().getId() : null,
                    typeDemande
            );
        });
    }*/


    // Récupérer toutes les demandes

    public List<DemandeInterventionDTO> getAllDemandes() {
        try {
            // Use native query to avoid zero date issues
            List<Map<String, Object>> rawData = repository.findAllWithNullSafeDates();
            return rawData.stream().map(row -> {
                DemandeInterventionDTO dto = new DemandeInterventionDTO();
                
                dto.setId(((Number) row.get("id")).longValue());
                dto.setDescription((String) row.get("description"));
                dto.setDateDemande((java.util.Date) row.get("date_demande"));
                dto.setStatut(StatutDemande.valueOf((String) row.get("statut")));
                dto.setPriorite((String) row.get("priorite"));
                dto.setDemandeurId(row.get("demandeur") != null ? ((Number) row.get("demandeur")).longValue() : null);
                dto.setTypeDemande((String) row.get("type_demande"));
                dto.setDateCreation((java.util.Date) row.get("date_creation"));
                dto.setDateValidation((java.util.Date) row.get("date_validation"));
                dto.setConfirmation(row.get("confirmation") != null ? ((Number) row.get("confirmation")).intValue() : 0);
                dto.setTesteurCodeGMAO((String) row.get("testeur_code_gmao"));
                dto.setTechnicienAssigneId(row.get("technicien_id") != null ? ((Number) row.get("technicien_id")).longValue() : null);
                
                return dto;
            }).toList();
            
            /*
            List<DemandeIntervention> demandes = repository.findAll();
            return demandes.stream().map(demande -> {
            DemandeInterventionDTO dto = new DemandeInterventionDTO();

            dto.setId(demande.getId());
            dto.setDescription(demande.getDescription());
            dto.setDateDemande(demande.getDateDemande());
            dto.setStatut(demande.getStatut());
            dto.setPriorite(demande.getPriorite());
            dto.setDemandeurId(demande.getDemandeur() != null ? demande.getDemandeur().getId() : null);
            dto.setTypeDemande(demande.getType_demande());
            
            // Map additional fields with null safety
            try {
                dto.setDateCreation(demande.getDateCreation());
                dto.setDateValidation(demande.getDateValidation());
                dto.setConfirmation(demande.getConfirmation());
                dto.setTesteurCodeGMAO(demande.getTesteur() != null ? demande.getTesteur().getCodeGMAO() : null);
                dto.setTechnicienAssigneId(demande.getTechnicienAssigne() != null ? demande.getTechnicienAssigne().getId() : null);
            } catch (Exception e) {
                // Handle cases where new fields don't exist in database yet
                System.out.println("⚠️ Warning: Could not map additional fields for intervention " + demande.getId() + ": " + e.getMessage());
                dto.setDateCreation(null);
                dto.setDateValidation(null);
                dto.setConfirmation(0);
                dto.setTesteurCodeGMAO(null);
                dto.setTechnicienAssigneId(null);
            }

            // Si besoin d’afficher le prénom


            // Champs spécifiques à Curative
            if (demande instanceof Curative) {
                Curative curative = (Curative) demande;
                dto.setPanne(curative.getPanne());
                dto.setUrgence(curative.isUrgence());
            }

            // Champs spécifiques à Preventive
            if (demande instanceof Preventive) {
                Preventive preventive = (Preventive) demande;
                dto.setFrequence(preventive.getFrequence());
                dto.setProchainRDV(preventive.getProchainRDV());
            }

            return dto;
        }).toList();
        */
        } catch (Exception e) {
            System.out.println("❌ Error in getAllDemandes: " + e.getMessage());
            e.printStackTrace();
            // Return empty list to prevent complete failure
            return new ArrayList<>();
        }
    }



    // Assigner un technicien à une intervention
    public DemandeInterventionDTO assignTechnicianToIntervention(Long interventionId, Long technicienId) {
        try {
            DemandeIntervention demande = repository.findById(interventionId)
                .orElseThrow(() -> new RuntimeException("Intervention non trouvée avec l'ID: " + interventionId));
            
            User technicien = userRepository.findById(technicienId)
                .orElseThrow(() -> new RuntimeException("Technicien non trouvé avec l'ID: " + technicienId));
            
            // Assigner le technicien
            demande.setTechnicienAssigne(technicien);
            
            // Changer le statut à EN_COURS si c'était EN_ATTENTE
            if (demande.getStatut() == StatutDemande.EN_ATTENTE) {
                demande.setStatut(StatutDemande.EN_COURS);
            }
            
            // Sauvegarder
            DemandeIntervention updatedDemande = repository.save(demande);
            
            // Créer le DTO de retour
            return createDTOFromEntity(updatedDemande);
            
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de l'affectation du technicien: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'affectation: " + e.getMessage());
        }
    }

    // Assigner un testeur (équipement) à une intervention
    public DemandeInterventionDTO assignTesteurToIntervention(Long interventionId, String testeurCodeGMAO) {
        try {
            DemandeIntervention demande = repository.findById(interventionId)
                .orElseThrow(() -> new RuntimeException("Intervention non trouvée avec l'ID: " + interventionId));
            
            Testeur testeur = testeurRepository.findById(testeurCodeGMAO)
                .orElseThrow(() -> new RuntimeException("Testeur/Équipement non trouvé avec le code: " + testeurCodeGMAO));
            
            // Assigner le testeur (équipement)
            demande.setTesteur(testeur);
            
            // Sauvegarder
            DemandeIntervention updatedDemande = repository.save(demande);
            
            // Créer le DTO de retour
            return createDTOFromEntity(updatedDemande);
            
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de l'affectation du testeur: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'affectation du testeur: " + e.getMessage());
        }
    }

    // Confirmer une intervention (met confirmation = 1 et dateValidation = maintenant)
    public DemandeInterventionDTO confirmerIntervention(Long interventionId) {
        try {
            DemandeIntervention demande = repository.findById(interventionId)
                .orElseThrow(() -> new RuntimeException("Intervention non trouvée avec l'ID: " + interventionId));
            
            // Confirmer l'intervention
            demande.setConfirmation(1);
            demande.setDateValidation(new java.util.Date());
            
            // Changer le statut à TERMINEE si pas déjà fait
            if (demande.getStatut() != StatutDemande.TERMINEE) {
                demande.setStatut(StatutDemande.TERMINEE);
            }
            
            // Sauvegarder
            DemandeIntervention updatedDemande = repository.save(demande);
            
            // Créer le DTO de retour
            return createDTOFromEntity(updatedDemande);
            
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de la confirmation: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la confirmation: " + e.getMessage());
        }
    }
    
    // Méthode utilitaire pour créer un DTO à partir d'une entité
    private DemandeInterventionDTO createDTOFromEntity(DemandeIntervention demande) {
        DemandeInterventionDTO dto = new DemandeInterventionDTO();
        
        dto.setId(demande.getId());
        dto.setDescription(demande.getDescription());
        dto.setDateDemande(demande.getDateDemande());
        dto.setStatut(demande.getStatut());
        dto.setPriorite(demande.getPriorite());
        dto.setDemandeurId(demande.getDemandeur() != null ? demande.getDemandeur().getId() : null);
        dto.setTypeDemande(demande.getType_demande());
        
        // Champs additionnels avec gestion des erreurs
        try {
            dto.setDateCreation(demande.getDateCreation());
            dto.setDateValidation(demande.getDateValidation());
            dto.setConfirmation(demande.getConfirmation() != null ? demande.getConfirmation() : 0);
            dto.setTesteurCodeGMAO(demande.getTesteur() != null ? demande.getTesteur().getCodeGMAO() : null);
            dto.setTechnicienAssigneId(demande.getTechnicienAssigne() != null ? demande.getTechnicienAssigne().getId() : null);
        } catch (Exception e) {
            System.out.println("⚠️ Avertissement lors de la récupération des champs additionnels: " + e.getMessage());
            dto.setDateCreation(null);
            dto.setDateValidation(null);
            dto.setConfirmation(0);
            dto.setTesteurCodeGMAO(null);
            dto.setTechnicienAssigneId(null);
        }
        
        // Champs spécifiques selon le type
        if (demande instanceof Curative) {
            Curative curative = (Curative) demande;
            dto.setPanne(curative.getPanne());
            dto.setUrgence(curative.isUrgence());
        }
        
        if (demande instanceof Preventive) {
            Preventive preventive = (Preventive) demande;
            dto.setFrequence(preventive.getFrequence());
            dto.setProchainRDV(preventive.getProchainRDV());
        }
        
        return dto;
    }

    // Mettre à jour une demande avec requête native pour éviter les erreurs JDBC
    public DemandeInterventionDTO updateDemande(Long id, DemandeInterventionDTO dto) {
        try {
            // Vérifier que la demande existe
            if (!repository.existsById(id)) {
                throw new RuntimeException("Demande d'intervention non trouvée avec l'ID: " + id);
            }
            
            // Utiliser la requête native pour éviter les problèmes JDBC
            int rowsUpdated = repository.updateDemandeBasicFields(
                id,
                dto.getDescription(),
                dto.getStatut() != null ? dto.getStatut().name() : null,
                dto.getPriorite(),
                dto.getTechnicienAssigneId()
            );
            
            if (rowsUpdated == 0) {
                throw new RuntimeException("Aucune ligne mise à jour pour l'ID: " + id);
            }
            
            // Récupérer la demande mise à jour en utilisant la requête native sécurisée
            List<Map<String, Object>> rawData = repository.findAllWithNullSafeDates();
            Map<String, Object> updatedRow = rawData.stream()
                .filter(row -> ((Number) row.get("id")).longValue() == id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Impossible de récupérer la demande mise à jour"));
            
            // Créer le DTO de retour à partir des données natives
            DemandeInterventionDTO resultDto = new DemandeInterventionDTO();
            resultDto.setId(((Number) updatedRow.get("id")).longValue());
            resultDto.setDescription((String) updatedRow.get("description"));
            resultDto.setDateDemande((java.util.Date) updatedRow.get("date_demande"));
            resultDto.setStatut(StatutDemande.valueOf((String) updatedRow.get("statut")));
            resultDto.setPriorite((String) updatedRow.get("priorite"));
            resultDto.setDemandeurId(updatedRow.get("demandeur") != null ? ((Number) updatedRow.get("demandeur")).longValue() : null);
            resultDto.setTypeDemande((String) updatedRow.get("type_demande"));
            resultDto.setDateCreation((java.util.Date) updatedRow.get("date_creation"));
            resultDto.setDateValidation((java.util.Date) updatedRow.get("date_validation"));
            resultDto.setConfirmation(updatedRow.get("confirmation") != null ? ((Number) updatedRow.get("confirmation")).intValue() : 0);
            resultDto.setTesteurCodeGMAO((String) updatedRow.get("testeur_code_gmao"));
            resultDto.setTechnicienAssigneId(updatedRow.get("technicien_id") != null ? ((Number) updatedRow.get("technicien_id")).longValue() : null);
            
            return resultDto;
            
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de la mise à jour native: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la mise à jour: " + e.getMessage());
        }
    }

    // Supprimer une demande
    public void deleteDemande(Long id) {
        repository.deleteById(id);
    }

}
