package tn.esprit.PI.util;

import tn.esprit.PI.entity.*;

import java.time.LocalDate;
import java.util.Date;

/**
 * Classe utilitaire pour créer des données de test
 */
public class TestDataBuilder {

    public static User createTestUser(Long id, String firstname, String lastname, UserRole role) {
        User user = new User();
        user.setId(id);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setEmail(firstname.toLowerCase() + "." + lastname.toLowerCase() + "@test.com");
        user.setPhoneNumber("123456789");
        user.setAdress("123 Test Street");
        user.setRole(role);
        user.setConfirmation(1);
        user.setPassword("encodedPassword");
        return user;
    }

    public static UserDTO createTestUserDTO(Long id, String firstname, String lastname, UserRole role) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(id);
        userDTO.setFirstName(firstname);
        userDTO.setLastName(lastname);
        userDTO.setEmail(firstname.toLowerCase() + "." + lastname.toLowerCase() + "@test.com");
        userDTO.setPhoneNumber("123456789");
        userDTO.setAdress("123 Test Street");
        userDTO.setRole(role);
        userDTO.setConfirmation(1);
        return userDTO;
    }

    public static Testeur createTestTesteur(String codeGMAO, String atelier, String ligne) {
        Testeur testeur = new Testeur();
        testeur.setCodeGMAO(codeGMAO);
        testeur.setAtelier(atelier);
        testeur.setLigne(ligne);
        testeur.setBancTest("Banc Test " + codeGMAO);
        return testeur;
    }

    public static TesteurDTO createTestTesteurDTO(String codeGMAO, String atelier, String ligne) {
        TesteurDTO testeurDTO = new TesteurDTO();
        testeurDTO.setCodeGMAO(codeGMAO);
        testeurDTO.setAtelier(atelier);
        testeurDTO.setLigne(ligne);
        testeurDTO.setBancTest("Banc Test " + codeGMAO);
        testeurDTO.setNombreInterventions(0);
        return testeurDTO;
    }

    public static DemandeIntervention createTestDemandeIntervention(Long id, String description, StatutDemande statut) {
        DemandeIntervention demande = new DemandeIntervention();
        demande.setId(id);
        demande.setDescription(description);
        demande.setDateDemande(new Date());
        demande.setStatut(statut);
        demande.setPriorite("HAUTE");
        demande.setType_demande("CURATIVE");
        demande.setConfirmation(0);
        return demande;
    }

    public static DemandeInterventionDTO createTestDemandeInterventionDTO(Long id, String description, StatutDemande statut) {
        DemandeInterventionDTO dto = new DemandeInterventionDTO();
        dto.setId(id);
        dto.setDescription(description);
        dto.setDateDemande(new Date());
        dto.setStatut(statut);
        dto.setPriorite("HAUTE");
        dto.setTypeDemande("CURATIVE");
        dto.setConfirmation(0);
        return dto;
    }

    public static Curative createTestCurative(Long id, String description, String panne, boolean urgence) {
        Curative curative = new Curative();
        curative.setId(id);
        curative.setDescription(description);
        curative.setDateDemande(new Date());
        curative.setStatut(StatutDemande.EN_ATTENTE);
        curative.setPriorite("HAUTE");
        curative.setType_demande("CURATIVE");
        curative.setPanne(panne);
        curative.setUrgence(urgence);
        curative.setConfirmation(0);
        return curative;
    }

    public static Preventive createTestPreventive(Long id, String description, String frequence, Date prochainRDV) {
        Preventive preventive = new Preventive();
        preventive.setId(id);
        preventive.setDescription(description);
        preventive.setDateDemande(new Date());
        preventive.setStatut(StatutDemande.EN_ATTENTE);
        preventive.setPriorite("MOYENNE");
        preventive.setType_demande("PREVENTIVE");
        preventive.setFrequence(frequence);
        preventive.setProchainRDV(prochainRDV);
        preventive.setConfirmation(0);
        return preventive;
    }

    public static BonDeTravail createTestBonDeTravail(Long id, String description, StatutBonTravail statut) {
        BonDeTravail bon = new BonDeTravail();
        bon.setId(id);
        bon.setDescription(description);
        bon.setDateCreation(LocalDate.now());
        bon.setDateDebut(LocalDate.now());
        bon.setDateFin(LocalDate.now().plusDays(1));
        bon.setStatut(statut);
        return bon;
    }

    public static BonTravailRequest createTestBonTravailRequest(Long technicienId, Long interventionId, String testeurCodeGMAO) {
        BonTravailRequest request = new BonTravailRequest();
        request.description = "Test bon de travail";
        request.dateCreation = LocalDate.now();
        request.dateDebut = LocalDate.now();
        request.dateFin = LocalDate.now().plusDays(1);
        request.statut = StatutBonTravail.EN_ATTENTE;
        request.technicien = technicienId;
        request.interventionId = interventionId;
        request.testeurCodeGMAO = testeurCodeGMAO;
        return request;
    }

    public static Component createTestComponent(String trartArticle, String designation, String quantite) {
        Component component = new Component();
        component.setTrartArticle(trartArticle);
        component.setTrartDesignation(designation);
        component.setTrartQuantite(quantite);
        return component;
    }

    public static BonTravailRequest.ComposantQuantite createTestComposantQuantite(String id, int quantite) {
        BonTravailRequest.ComposantQuantite composant = new BonTravailRequest.ComposantQuantite();
        composant.id = id;
        composant.quantite = quantite;
        return composant;
    }

    public static BonTravailComponent createTestBonTravailComponent(BonDeTravail bon, Component component, int quantite) {
        BonTravailComponent btc = new BonTravailComponent();
        btc.setBon(bon);
        btc.setComponent(component);
        btc.setQuantiteUtilisee(quantite);
        return btc;
    }

    /**
     * Crée des données de test pour les requêtes natives (Map<String, Object>)
     */
    public static java.util.Map<String, Object> createTestInterventionMap(Long id, String description, String statut, String typeDemande) {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", id);
        map.put("description", description);
        map.put("date_demande", new Date());
        map.put("statut", statut);
        map.put("priorite", "HAUTE");
        map.put("demandeur", 1L);
        map.put("type_demande", typeDemande);
        map.put("date_creation", new Date());
        map.put("date_validation", null);
        map.put("confirmation", 0);
        map.put("testeur_code_gmao", null);
        map.put("technicien_id", null);
        
        // Champs spécifiques selon le type
        if ("CURATIVE".equals(typeDemande)) {
            map.put("panne", "Panne test");
            map.put("urgence", true);
            map.put("frequence", null);
            map.put("prochainrdv", null);
        } else if ("PREVENTIVE".equals(typeDemande)) {
            map.put("panne", null);
            map.put("urgence", null);
            map.put("frequence", "MENSUELLE");
            map.put("prochainrdv", new Date());
        }
        
        return map;
    }

    /**
     * Crée un utilisateur admin pour les tests
     */
    public static User createTestAdmin() {
        return createTestUser(1L, "Admin", "Test", UserRole.ADMIN);
    }

    /**
     * Crée un technicien curatif pour les tests
     */
    public static User createTestTechnicienCuratif() {
        return createTestUser(2L, "Technicien", "Curatif", UserRole.TECHNICIEN_CURATIF);
    }

    /**
     * Crée un technicien préventif pour les tests
     */
    public static User createTestTechnicienPreventif() {
        return createTestUser(3L, "Technicien", "Preventif", UserRole.TECHNICIEN_PREVENTIF);
    }

    /**
     * Crée un magasinier pour les tests
     */
    public static User createTestMagasinier() {
        return createTestUser(4L, "Magasinier", "Test", UserRole.MAGASINIER);
    }

    /**
     * Crée un chef de projet pour les tests
     */
    public static User createTestChefProjet() {
        return createTestUser(5L, "Chef", "Projet", UserRole.CHEF_PROJET);
    }
}
