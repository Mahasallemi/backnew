package tn.esprit.PI.RestControlleur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.PI.Services.DemandeInterventionService;
import tn.esprit.PI.Services.BonDeTravailService;
import tn.esprit.PI.entity.DemandeInterventionDTO;
import tn.esprit.PI.entity.BonDeTravail;
import tn.esprit.PI.entity.BonTravailRequest;
import tn.esprit.PI.entity.StatutDemande;
import tn.esprit.PI.entity.*;
import tn.esprit.PI.repository.DemandeInterventionRepository;
import tn.esprit.PI.repository.UserRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/PI/demandes")
public class DemandeInterventionController {

    @Autowired
    private DemandeInterventionService demandeInterventionService;

    @Autowired
    private DemandeInterventionRepository demandeInterventionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BonDeTravailService bonDeTravailService;

    @PostMapping("/create")
    public ResponseEntity<?> createIntervention(@RequestBody Map<String, Object> requestData) {
        try {
            // Debug logging
            System.out.println("🔍 Request data received: " + requestData);
            
            String typeDemande = (String) requestData.get("type_demande");
            // Also check for "type" field name (frontend compatibility)
            if (typeDemande == null) {
                typeDemande = (String) requestData.get("type");
            }
            // Also check for "typeDemande" field name (Swagger UI compatibility)
            if (typeDemande == null) {
                typeDemande = (String) requestData.get("typeDemande");
            }
            System.out.println("🔍 Type demande received: '" + typeDemande + "'");
            
            // Normalize the type to uppercase for comparison
            if (typeDemande != null) {
                typeDemande = typeDemande.toUpperCase().trim();
            }
            
            Long demandeurId = ((Number) requestData.get("demandeurId")).longValue();
            User demandeur = userRepository.findById(demandeurId)
                    .orElseThrow(() -> new RuntimeException("Demandeur non trouvé"));

            String description = (String) requestData.get("description");
            String priorite = (String) requestData.get("priorite");
            Date dateDemande = new Date(); // ou convertis à partir de requestData si besoin

            // Récupérer le statut de la demande depuis la requête
            String statutStr = (String) requestData.get("statut");
            StatutDemande statut = StatutDemande.valueOf(statutStr.toUpperCase()); // Conversion du statut en valeur de l'énumération

            if ("CURATIVE".equals(typeDemande) || "CORRECTIVE".equals(typeDemande)) {
                Curative curative = new Curative();
                curative.setDescription(description);
                curative.setDateDemande(dateDemande);
                curative.setStatut(statut); // Appliquer le statut fourni
                curative.setPriorite(priorite);
                curative.setDemandeur(demandeur);
                curative.setPanne((String) requestData.get("panne"));
                curative.setUrgence((Boolean) requestData.get("urgence"));
                return ResponseEntity.ok(demandeInterventionRepository.save(curative));
            } else if ("PREVENTIVE".equals(typeDemande)) {
                Preventive preventive = new Preventive();
                preventive.setDescription(description);
                preventive.setDateDemande(dateDemande);
                preventive.setStatut(statut); // Appliquer le statut fourni
                preventive.setPriorite(priorite);
                preventive.setDemandeur(demandeur);
                preventive.setFrequence((String) requestData.get("frequence"));
                preventive.setProchainRDV(new SimpleDateFormat("yyyy-MM-dd").parse((String) requestData.get("prochainRDV")));
                return ResponseEntity.ok(demandeInterventionRepository.save(preventive));
            } else {
                System.out.println("❌ Type de demande non supporté: '" + typeDemande + "'");
                System.out.println("📝 Types supportés: CURATIVE, CORRECTIVE, PREVENTIVE");
                return ResponseEntity.badRequest().body("Type de demande non pris en charge: '" + typeDemande + "'. Types supportés: CURATIVE, CORRECTIVE, PREVENTIVE");
            }

        } catch (Exception e) {
            System.out.println("❌ Exception during intervention creation:");
            e.printStackTrace();
            System.out.println("❌ Exception message: " + e.getMessage());
            System.out.println("❌ Exception class: " + e.getClass().getSimpleName());
            if (e.getCause() != null) {
                System.out.println("❌ Root cause: " + e.getCause().getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur lors de la création: " + e.getMessage());
        }
    }

    @GetMapping("/recuperer/{id}")
    public ResponseEntity<DemandeInterventionDTO> getDemandeById(@PathVariable Long id) {
        Optional<DemandeInterventionDTO> demande = demandeInterventionService.getDemandeById(id);
        return demande.map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/recuperer/all")
    public ResponseEntity<List<DemandeInterventionDTO>> getAllDemandes() {
        List<DemandeInterventionDTO> demandes = demandeInterventionService.getAllDemandes();
        return new ResponseEntity<>(demandes, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllDemandesShort() {
        try {
            List<DemandeInterventionDTO> demandes = demandeInterventionService.getAllDemandes();
            return new ResponseEntity<>(demandes, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("❌ Error in getAllDemandesShort:");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur lors de la récupération des demandes: " + e.getMessage());
        }
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateDemande(@PathVariable Long id, @RequestBody DemandeInterventionDTO dto) {
        try {
            DemandeInterventionDTO updatedDemande = demandeInterventionService.updateDemande(id, dto);
            return new ResponseEntity<>(updatedDemande, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of(
                "error", "Erreur lors de la mise à jour",
                "message", e.getMessage()
            ), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of(
                "error", "Erreur interne du serveur",
                "message", e.getMessage()
            ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/assign/{interventionId}/technicien/{technicienId}")
    public ResponseEntity<?> assignTechnicianToIntervention(
            @PathVariable Long interventionId, 
            @PathVariable Long technicienId) {
        try {
            DemandeInterventionDTO updatedIntervention = demandeInterventionService
                .assignTechnicianToIntervention(interventionId, technicienId);
            return new ResponseEntity<>(updatedIntervention, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of(
                "error", "Erreur lors de l'affectation",
                "message", e.getMessage()
            ), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of(
                "error", "Erreur interne du serveur",
                "message", e.getMessage()
            ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/assign/{interventionId}/testeur/{testeurCodeGMAO}")
    public ResponseEntity<?> assignTesteurToIntervention(
            @PathVariable Long interventionId, 
            @PathVariable String testeurCodeGMAO) {
        try {
            DemandeInterventionDTO updatedIntervention = demandeInterventionService
                .assignTesteurToIntervention(interventionId, testeurCodeGMAO);
            return new ResponseEntity<>(updatedIntervention, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of(
                "error", "Erreur lors de l'affectation du testeur",
                "message", e.getMessage()
            ), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of(
                "error", "Erreur interne du serveur",
                "message", e.getMessage()
            ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/confirmer/{interventionId}")
    public ResponseEntity<?> confirmerIntervention(@PathVariable Long interventionId) {
        try {
            DemandeInterventionDTO updatedIntervention = demandeInterventionService
                .confirmerIntervention(interventionId);
            return new ResponseEntity<>(updatedIntervention, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of(
                "error", "Erreur lors de la confirmation",
                "message", e.getMessage()
            ), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of(
                "error", "Erreur interne du serveur",
                "message", e.getMessage()
            ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteDemande(@PathVariable Long id) {
        demandeInterventionService.deleteDemande(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Créer un bon de travail pour une intervention
    @PostMapping("/{interventionId}/bon-travail/technicien/{technicienId}")
    public ResponseEntity<?> createBonDeTravailForIntervention(
            @PathVariable Long interventionId,
            @PathVariable Long technicienId,
            @RequestBody BonTravailRequest request) {
        try {
            BonDeTravail bon = bonDeTravailService.createBonDeTravailFromIntervention(
                interventionId, technicienId, request);
            return new ResponseEntity<>(bon, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of(
                "error", "Erreur lors de la création du bon de travail",
                "message", e.getMessage()
            ), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of(
                "error", "Erreur interne du serveur",
                "message", e.getMessage()
            ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Récupérer tous les bons de travail d'une intervention
    @GetMapping("/{interventionId}/bons-travail")
    public ResponseEntity<?> getBonsDeTravailForIntervention(@PathVariable Long interventionId) {
        try {
            List<BonDeTravail> bons = bonDeTravailService.getBonsDeTravailByIntervention(interventionId);
            return new ResponseEntity<>(bons, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of(
                "error", "Erreur lors de la récupération des bons de travail",
                "message", e.getMessage()
            ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
