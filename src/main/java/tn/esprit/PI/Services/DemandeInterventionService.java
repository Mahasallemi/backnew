package tn.esprit.PI.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.PI.entity.Curative;
import tn.esprit.PI.entity.DemandeIntervention;
import tn.esprit.PI.entity.DemandeInterventionDTO;
import tn.esprit.PI.entity.Preventive;
import tn.esprit.PI.entity.StatutDemande;
import tn.esprit.PI.repository.DemandeInterventionRepository;
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



    // Mettre à jour une demande
    public DemandeInterventionDTO updateDemande(Long id, DemandeInterventionDTO dto) {
        return repository.findById(id).map(demande -> {
            // Mise à jour des champs communs seulement s'ils ne sont pas null
            if (dto.getDescription() != null) {
                demande.setDescription(dto.getDescription());
            }
            if (dto.getDateDemande() != null) {
                demande.setDateDemande(dto.getDateDemande());
            }
            if (dto.getStatut() != null) {
                demande.setStatut(dto.getStatut());
            }
            if (dto.getPriorite() != null) {
                demande.setPriorite(dto.getPriorite());
            }
            
            // Mise à jour du demandeur si fourni
            if (dto.getDemandeurId() != null) {
                demande.setDemandeur(userRepository.findById(dto.getDemandeurId()).orElse(null));
            }
            
            // Mise à jour des champs spécifiques selon le type
            if (demande instanceof Curative && dto.getTypeDemande().equals("CURATIVE")) {
                Curative curative = (Curative) demande;
                if (dto.getPanne() != null) {
                    curative.setPanne(dto.getPanne());
                }
                if (dto.getUrgence() != null) {
                    curative.setUrgence(dto.getUrgence());
                }
            }
            
            if (demande instanceof Preventive && dto.getTypeDemande().equals("PREVENTIVE")) {
                Preventive preventive = (Preventive) demande;
                if (dto.getFrequence() != null) {
                    preventive.setFrequence(dto.getFrequence());
                }
                if (dto.getProchainRDV() != null) {
                    preventive.setProchainRDV(dto.getProchainRDV());
                }
            }
            
            // Sauvegarde de la demande mise à jour
            DemandeIntervention updatedDemande = repository.save(demande);
            
            // Création du DTO de retour
            DemandeInterventionDTO resultDto = new DemandeInterventionDTO();
            resultDto.setId(updatedDemande.getId());
            resultDto.setDescription(updatedDemande.getDescription());
            resultDto.setDateDemande(updatedDemande.getDateDemande());
            resultDto.setStatut(updatedDemande.getStatut());
            resultDto.setPriorite(updatedDemande.getPriorite());
            resultDto.setDemandeurId(updatedDemande.getDemandeur() != null ? updatedDemande.getDemandeur().getId() : null);
            resultDto.setTypeDemande(updatedDemande.getType_demande());
            
            // Map additional fields for update response
            resultDto.setDateCreation(updatedDemande.getDateCreation());
            resultDto.setDateValidation(updatedDemande.getDateValidation());
            resultDto.setConfirmation(updatedDemande.getConfirmation());
            resultDto.setTesteurCodeGMAO(updatedDemande.getTesteur() != null ? updatedDemande.getTesteur().getCodeGMAO() : null);
            resultDto.setTechnicienAssigneId(updatedDemande.getTechnicienAssigne() != null ? updatedDemande.getTechnicienAssigne().getId() : null);
            
            // Ajout des champs spécifiques selon le type
            if (updatedDemande instanceof Curative) {
                Curative curative = (Curative) updatedDemande;
                resultDto.setPanne(curative.getPanne());
                resultDto.setUrgence(curative.isUrgence());
            }
            
            if (updatedDemande instanceof Preventive) {
                Preventive preventive = (Preventive) updatedDemande;
                resultDto.setFrequence(preventive.getFrequence());
                resultDto.setProchainRDV(preventive.getProchainRDV());
            }
            
            return resultDto;
            
        }).orElseThrow(() -> new RuntimeException("Demande d'intervention non trouvée avec l'ID: " + id));
    }

    // Supprimer une demande
    public void deleteDemande(Long id) {
        repository.deleteById(id);
    }

}
