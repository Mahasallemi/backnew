package tn.esprit.PI.Services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.PI.entity.*;
import tn.esprit.PI.repository.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BonDeTravailServiceTest {

    @Mock
    private BonDeTravailRepository bonDeTravailRepository;

    @Mock
    private UserRepository technicienRepository;

    @Mock
    private ComponentRp composantRepository;

    @Mock
    private DemandeInterventionRepository interventionRepository;

    @Mock
    private TesteurRepository testeurRepository;

    @InjectMocks
    private BonDeTravailService bonDeTravailService;

    private BonDeTravail mockBonDeTravail;
    private User mockTechnicien;
    private Component mockComponent;
    private DemandeIntervention mockIntervention;
    private Testeur mockTesteur;
    private BonTravailRequest mockRequest;

    @BeforeEach
    void setUp() {
        // Setup mock entities
        mockTechnicien = new User();
        mockTechnicien.setId(1L);
        mockTechnicien.setFirstname("John");
        mockTechnicien.setLastname("Doe");

        mockComponent = new Component();
        mockComponent.setTrartArticle("COMP001");
        mockComponent.setTrartDesignation("Composant Test");
        mockComponent.setTrartQuantite("10");

        mockTesteur = new Testeur();
        mockTesteur.setCodeGMAO("TEST001");
        mockTesteur.setAtelier("Atelier A");
        mockTesteur.setLigne("Ligne 1");

        mockIntervention = new DemandeIntervention();
        mockIntervention.setId(1L);
        mockIntervention.setDescription("Intervention test");
        mockIntervention.setTesteur(mockTesteur);

        mockBonDeTravail = new BonDeTravail();
        mockBonDeTravail.setId(1L);
        mockBonDeTravail.setDescription("Bon de travail test");
        mockBonDeTravail.setDateCreation(LocalDate.now());
        mockBonDeTravail.setStatut(StatutBonTravail.EN_ATTENTE);
        mockBonDeTravail.setTechnicien(mockTechnicien);
        mockBonDeTravail.setIntervention(mockIntervention);
        mockBonDeTravail.setTesteur(mockTesteur);

        // Setup mock request
        mockRequest = new BonTravailRequest();
        mockRequest.description = "Test description";
        mockRequest.dateCreation = LocalDate.now();
        mockRequest.dateDebut = LocalDate.now();
        mockRequest.dateFin = LocalDate.now().plusDays(1);
        mockRequest.statut = StatutBonTravail.EN_ATTENTE;
        mockRequest.technicien = 1L;
        mockRequest.interventionId = 1L;
        mockRequest.testeurCodeGMAO = "TEST001";

        BonTravailRequest.ComposantQuantite composant = new BonTravailRequest.ComposantQuantite();
        composant.id = "COMP001";
        composant.quantite = 2;
        mockRequest.composants = Arrays.asList(composant);
    }

    @Test
    void testGetAllBonDeTravail_Success() {
        // Given
        List<BonDeTravail> mockBons = Arrays.asList(mockBonDeTravail);
        when(bonDeTravailRepository.findAll()).thenReturn(mockBons);

        // When
        List<BonDeTravail> result = bonDeTravailService.getAllBonDeTravail();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockBonDeTravail.getId(), result.get(0).getId());

        verify(bonDeTravailRepository, times(1)).findAll();
    }

    @Test
    void testGetBonDeTravailById_Success() {
        // Given
        Long bonId = 1L;
        when(bonDeTravailRepository.findById(bonId)).thenReturn(Optional.of(mockBonDeTravail));

        // When
        BonDeTravail result = bonDeTravailService.getBonDeTravailById(bonId);

        // Then
        assertNotNull(result);
        assertEquals(bonId, result.getId());
        assertEquals("Bon de travail test", result.getDescription());

        verify(bonDeTravailRepository, times(1)).findById(bonId);
    }

    @Test
    void testGetBonDeTravailById_NotFound() {
        // Given
        Long bonId = 999L;
        when(bonDeTravailRepository.findById(bonId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> bonDeTravailService.getBonDeTravailById(bonId));

        assertEquals("Bon de Travail non trouvé avec l'ID: " + bonId, exception.getMessage());
        verify(bonDeTravailRepository, times(1)).findById(bonId);
    }

    @Test
    void testGetBonDeTravailById_NullId() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> bonDeTravailService.getBonDeTravailById(null));

        assertEquals("L'ID du bon de travail ne peut pas être null", exception.getMessage());
        verify(bonDeTravailRepository, never()).findById(any());
    }

    @Test
    void testCreateBonDeTravail_Success() {
        // Given
        when(technicienRepository.findById(1L)).thenReturn(Optional.of(mockTechnicien));
        when(interventionRepository.findById(1L)).thenReturn(Optional.of(mockIntervention));
        when(testeurRepository.findById("TEST001")).thenReturn(Optional.of(mockTesteur));
        when(composantRepository.findById("COMP001")).thenReturn(Optional.of(mockComponent));
        when(bonDeTravailRepository.save(any(BonDeTravail.class))).thenReturn(mockBonDeTravail);

        // When
        BonDeTravail result = bonDeTravailService.createBonDeTravail(mockRequest);

        // Then
        assertNotNull(result);
        assertEquals(mockBonDeTravail.getId(), result.getId());

        verify(technicienRepository, times(1)).findById(1L);
        verify(interventionRepository, times(1)).findById(1L);
        verify(testeurRepository, times(1)).findById("TEST001");
        verify(composantRepository, times(1)).findById("COMP001");
        verify(composantRepository, times(1)).save(mockComponent);
        verify(bonDeTravailRepository, times(1)).save(any(BonDeTravail.class));
    }

    @Test
    void testCreateBonDeTravail_TechnicienNotFound() {
        // Given
        when(technicienRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> bonDeTravailService.createBonDeTravail(mockRequest));

        assertEquals("Technicien non trouvé", exception.getMessage());
        verify(technicienRepository, times(1)).findById(1L);
        verify(bonDeTravailRepository, never()).save(any());
    }

    @Test
    void testCreateBonDeTravail_NullTechnicienId() {
        // Given
        mockRequest.technicien = null;

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> bonDeTravailService.createBonDeTravail(mockRequest));

        assertEquals("Technicien id must not be null", exception.getMessage());
        verify(technicienRepository, never()).findById(any());
    }

    @Test
    void testCreateBonDeTravail_InterventionNotFound() {
        // Given
        when(technicienRepository.findById(1L)).thenReturn(Optional.of(mockTechnicien));
        when(interventionRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> bonDeTravailService.createBonDeTravail(mockRequest));

        assertEquals("Intervention non trouvée avec l'ID: 1", exception.getMessage());
        verify(interventionRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateBonDeTravail_TesteurNotFound() {
        // Given
        when(technicienRepository.findById(1L)).thenReturn(Optional.of(mockTechnicien));
        when(interventionRepository.findById(1L)).thenReturn(Optional.of(mockIntervention));
        when(testeurRepository.findById("TEST001")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> bonDeTravailService.createBonDeTravail(mockRequest));

        assertEquals("Testeur non trouvé avec le code: TEST001", exception.getMessage());
        verify(testeurRepository, times(1)).findById("TEST001");
    }

    @Test
    void testCreateBonDeTravail_ComposantNotFound() {
        // Given
        when(technicienRepository.findById(1L)).thenReturn(Optional.of(mockTechnicien));
        when(interventionRepository.findById(1L)).thenReturn(Optional.of(mockIntervention));
        when(testeurRepository.findById("TEST001")).thenReturn(Optional.of(mockTesteur));
        when(composantRepository.findById("COMP001")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> bonDeTravailService.createBonDeTravail(mockRequest));

        assertEquals("Composant non trouvé : COMP001", exception.getMessage());
        verify(composantRepository, times(1)).findById("COMP001");
    }

    @Test
    void testCreateBonDeTravail_InsufficientStock() {
        // Given
        mockComponent.setTrartQuantite("1"); // Stock insuffisant
        when(technicienRepository.findById(1L)).thenReturn(Optional.of(mockTechnicien));
        when(interventionRepository.findById(1L)).thenReturn(Optional.of(mockIntervention));
        when(testeurRepository.findById("TEST001")).thenReturn(Optional.of(mockTesteur));
        when(composantRepository.findById("COMP001")).thenReturn(Optional.of(mockComponent));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> bonDeTravailService.createBonDeTravail(mockRequest));

        assertTrue(exception.getMessage().contains("Quantité insuffisante"));
        verify(composantRepository, never()).save(any());
    }

    @Test
    void testCreateBonDeTravailFromIntervention_Success() {
        // Given
        Long interventionId = 1L;
        Long technicienId = 1L;
        
        when(interventionRepository.existsById(interventionId)).thenReturn(true);
        when(interventionRepository.findById(interventionId)).thenReturn(Optional.of(mockIntervention));
        when(technicienRepository.findById(technicienId)).thenReturn(Optional.of(mockTechnicien));
        when(composantRepository.findById("COMP001")).thenReturn(Optional.of(mockComponent));
        when(bonDeTravailRepository.save(any(BonDeTravail.class))).thenReturn(mockBonDeTravail);

        // When
        BonDeTravail result = bonDeTravailService.createBonDeTravailFromIntervention(
            interventionId, technicienId, mockRequest);

        // Then
        assertNotNull(result);
        assertEquals(mockBonDeTravail.getId(), result.getId());

        verify(interventionRepository, times(1)).existsById(interventionId);
        verify(interventionRepository, times(1)).findById(interventionId);
        verify(technicienRepository, times(1)).findById(technicienId);
        verify(bonDeTravailRepository, times(1)).save(any(BonDeTravail.class));
    }

    @Test
    void testCreateBonDeTravailFromIntervention_InterventionNotFound() {
        // Given
        Long interventionId = 999L;
        Long technicienId = 1L;
        
        when(interventionRepository.existsById(interventionId)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> bonDeTravailService.createBonDeTravailFromIntervention(
                interventionId, technicienId, mockRequest));

        assertEquals("Intervention non trouvée avec l'ID: " + interventionId, exception.getMessage());
        verify(interventionRepository, times(1)).existsById(interventionId);
        verify(bonDeTravailRepository, never()).save(any());
    }

    @Test
    void testCreateBonDeTravailFromIntervention_NoTesteurAssociated() {
        // Given
        Long interventionId = 1L;
        Long technicienId = 1L;
        mockIntervention.setTesteur(null); // No testeur associated
        
        when(interventionRepository.existsById(interventionId)).thenReturn(true);
        when(interventionRepository.findById(interventionId)).thenReturn(Optional.of(mockIntervention));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> bonDeTravailService.createBonDeTravailFromIntervention(
                interventionId, technicienId, mockRequest));

        assertEquals("L'intervention doit avoir un testeur (équipement) associé pour créer un bon de travail", 
            exception.getMessage());
        verify(bonDeTravailRepository, never()).save(any());
    }

    @Test
    void testGetBonsDeTravailByIntervention_Success() {
        // Given
        Long interventionId = 1L;
        List<BonDeTravail> mockBons = Arrays.asList(mockBonDeTravail);
        when(bonDeTravailRepository.findByInterventionId(interventionId)).thenReturn(mockBons);

        // When
        List<BonDeTravail> result = bonDeTravailService.getBonsDeTravailByIntervention(interventionId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockBonDeTravail.getId(), result.get(0).getId());

        verify(bonDeTravailRepository, times(1)).findByInterventionId(interventionId);
    }

    @Test
    void testGetBonsDeTravailByTesteur_Success() {
        // Given
        String testeurCodeGMAO = "TEST001";
        List<BonDeTravail> mockBons = Arrays.asList(mockBonDeTravail);
        when(bonDeTravailRepository.findByTesteurCodeGMAO(testeurCodeGMAO)).thenReturn(mockBons);

        // When
        List<BonDeTravail> result = bonDeTravailService.getBonsDeTravailByTesteur(testeurCodeGMAO);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockBonDeTravail.getId(), result.get(0).getId());

        verify(bonDeTravailRepository, times(1)).findByTesteurCodeGMAO(testeurCodeGMAO);
    }

    @Test
    void testUpdateBonDeTravail_Success() {
        // Given
        Long bonId = 1L;
        BonTravailRequest updateRequest = new BonTravailRequest();
        updateRequest.description = "Updated description";
        updateRequest.statut = StatutBonTravail.EN_COURS;
        updateRequest.technicien = 2L;

        User newTechnicien = new User();
        newTechnicien.setId(2L);
        newTechnicien.setFirstname("Jane");

        when(bonDeTravailRepository.findById(bonId)).thenReturn(Optional.of(mockBonDeTravail));
        when(technicienRepository.findById(2L)).thenReturn(Optional.of(newTechnicien));
        when(bonDeTravailRepository.saveAndFlush(any(BonDeTravail.class))).thenReturn(mockBonDeTravail);

        // When
        BonDeTravail result = bonDeTravailService.updateBonDeTravail(bonId, updateRequest);

        // Then
        assertNotNull(result);
        verify(bonDeTravailRepository, times(1)).findById(bonId);
        verify(technicienRepository, times(1)).findById(2L);
        verify(bonDeTravailRepository, times(1)).saveAndFlush(any(BonDeTravail.class));
    }

    @Test
    void testUpdateBonDeTravail_TechnicienNotFound() {
        // Given
        Long bonId = 1L;
        BonTravailRequest updateRequest = new BonTravailRequest();
        updateRequest.technicien = 999L;

        when(bonDeTravailRepository.findById(bonId)).thenReturn(Optional.of(mockBonDeTravail));
        when(technicienRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> bonDeTravailService.updateBonDeTravail(bonId, updateRequest));

        assertEquals("Technicien non trouvé avec l'ID: 999", exception.getMessage());
        verify(bonDeTravailRepository, never()).saveAndFlush(any());
    }

    @Test
    void testDeleteBonDeTravail_Success() {
        // Given
        Long bonId = 1L;
        when(bonDeTravailRepository.existsById(bonId)).thenReturn(true);

        // When
        bonDeTravailService.deleteBonDeTravail(bonId);

        // Then
        verify(bonDeTravailRepository, times(1)).existsById(bonId);
        verify(bonDeTravailRepository, times(1)).deleteComponentsByBonId(bonId);
        verify(bonDeTravailRepository, times(1)).deleteById(bonId);
    }

    @Test
    void testDeleteBonDeTravail_NotFound() {
        // Given
        Long bonId = 999L;
        when(bonDeTravailRepository.existsById(bonId)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> bonDeTravailService.deleteBonDeTravail(bonId));

        assertEquals("Bon de Travail non trouvé avec l'ID: " + bonId, exception.getMessage());
        verify(bonDeTravailRepository, times(1)).existsById(bonId);
        verify(bonDeTravailRepository, never()).deleteById(any());
    }
}
