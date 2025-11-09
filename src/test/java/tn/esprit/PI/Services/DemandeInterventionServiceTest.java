package tn.esprit.PI.Services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.PI.entity.*;
import tn.esprit.PI.repository.DemandeInterventionRepository;
import tn.esprit.PI.repository.TesteurRepository;
import tn.esprit.PI.repository.UserRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DemandeInterventionServiceTest {

    @Mock
    private DemandeInterventionRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TesteurRepository testeurRepository;

    @InjectMocks
    private DemandeInterventionService service;

    private Map<String, Object> mockInterventionData;
    private User mockUser;
    private Testeur mockTesteur;

    @BeforeEach
    void setUp() {
        // Setup mock data
        mockInterventionData = new HashMap<>();
        mockInterventionData.put("id", 1L);
        mockInterventionData.put("description", "Test intervention");
        mockInterventionData.put("date_demande", new Date());
        mockInterventionData.put("statut", "EN_ATTENTE");
        mockInterventionData.put("priorite", "HAUTE");
        mockInterventionData.put("demandeur", 1L);
        mockInterventionData.put("type_demande", "CURATIVE");
        mockInterventionData.put("date_creation", new Date());
        mockInterventionData.put("date_validation", null);
        mockInterventionData.put("confirmation", 0);
        mockInterventionData.put("testeur_code_gmao", "TEST001");
        mockInterventionData.put("technicien_id", 2L);
        mockInterventionData.put("panne", "Panne moteur");
        mockInterventionData.put("urgence", true);
        mockInterventionData.put("frequence", null);
        mockInterventionData.put("prochainrdv", null);

        mockUser = new User();
        mockUser.setId(2L);
        mockUser.setFirstname("John");
        mockUser.setLastname("Doe");

        mockTesteur = new Testeur();
        mockTesteur.setCodeGMAO("TEST001");
        mockTesteur.setAtelier("Atelier A");
        mockTesteur.setLigne("Ligne 1");
    }

    @Test
    void testGetAllDemandes_Success() {
        // Given
        List<Map<String, Object>> mockData = Arrays.asList(mockInterventionData);
        when(repository.findAllWithNullSafeDates()).thenReturn(mockData);

        // When
        List<DemandeInterventionDTO> result = service.getAllDemandes();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        DemandeInterventionDTO dto = result.get(0);
        assertEquals(1L, dto.getId());
        assertEquals("Test intervention", dto.getDescription());
        assertEquals(StatutDemande.EN_ATTENTE, dto.getStatut());
        assertEquals("CURATIVE", dto.getTypeDemande());
        assertEquals("Panne moteur", dto.getPanne());
        assertTrue(dto.getUrgence());

        verify(repository, times(1)).findAllWithNullSafeDates();
    }

    @Test
    void testGetAllDemandes_EmptyList() {
        // Given
        when(repository.findAllWithNullSafeDates()).thenReturn(new ArrayList<>());

        // When
        List<DemandeInterventionDTO> result = service.getAllDemandes();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository, times(1)).findAllWithNullSafeDates();
    }

    @Test
    void testGetAllDemandes_ExceptionHandling() {
        // Given
        when(repository.findAllWithNullSafeDates()).thenThrow(new RuntimeException("Database error"));

        // When
        List<DemandeInterventionDTO> result = service.getAllDemandes();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository, times(1)).findAllWithNullSafeDates();
    }

    @Test
    void testGetDemandeById_Success() {
        // Given
        List<Map<String, Object>> mockData = Arrays.asList(mockInterventionData);
        when(repository.findAllWithNullSafeDates()).thenReturn(mockData);

        // When
        Optional<DemandeInterventionDTO> result = service.getDemandeById(1L);

        // Then
        assertTrue(result.isPresent());
        DemandeInterventionDTO dto = result.get();
        assertEquals(1L, dto.getId());
        assertEquals("Test intervention", dto.getDescription());
        verify(repository, times(1)).findAllWithNullSafeDates();
    }

    @Test
    void testGetDemandeById_NotFound() {
        // Given
        when(repository.findAllWithNullSafeDates()).thenReturn(new ArrayList<>());

        // When
        Optional<DemandeInterventionDTO> result = service.getDemandeById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(repository, times(1)).findAllWithNullSafeDates();
    }

    @Test
    void testAssignTechnicianToIntervention_Success() {
        // Given
        Long interventionId = 1L;
        Long technicienId = 2L;
        
        List<Map<String, Object>> mockDataBefore = Arrays.asList(mockInterventionData);
        Map<String, Object> mockDataAfter = new HashMap<>(mockInterventionData);
        mockDataAfter.put("technicien_id", technicienId);
        mockDataAfter.put("statut", "EN_COURS");
        List<Map<String, Object>> mockDataAfterList = Arrays.asList(mockDataAfter);

        when(repository.findAllWithNullSafeDates())
            .thenReturn(mockDataBefore)
            .thenReturn(mockDataAfterList);
        when(userRepository.existsById(technicienId)).thenReturn(true);
        when(repository.assignTechnicianNative(interventionId, technicienId)).thenReturn(1);

        // When
        DemandeInterventionDTO result = service.assignTechnicianToIntervention(interventionId, technicienId);

        // Then
        assertNotNull(result);
        assertEquals(interventionId, result.getId());
        assertEquals(technicienId, result.getTechnicienAssigneId());
        assertEquals(StatutDemande.EN_COURS, result.getStatut());

        verify(repository, times(2)).findAllWithNullSafeDates();
        verify(userRepository, times(1)).existsById(technicienId);
        verify(repository, times(1)).assignTechnicianNative(interventionId, technicienId);
    }

    @Test
    void testAssignTechnicianToIntervention_InterventionNotFound() {
        // Given
        Long interventionId = 999L;
        Long technicienId = 2L;
        
        when(repository.findAllWithNullSafeDates()).thenReturn(new ArrayList<>());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> service.assignTechnicianToIntervention(interventionId, technicienId));
        
        assertEquals("Intervention non trouvée avec l'ID: " + interventionId, exception.getMessage());
        verify(repository, times(1)).findAllWithNullSafeDates();
        verify(userRepository, never()).existsById(any());
    }

    @Test
    void testAssignTechnicianToIntervention_TechnicienNotFound() {
        // Given
        Long interventionId = 1L;
        Long technicienId = 999L;
        
        List<Map<String, Object>> mockData = Arrays.asList(mockInterventionData);
        when(repository.findAllWithNullSafeDates()).thenReturn(mockData);
        when(userRepository.existsById(technicienId)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> service.assignTechnicianToIntervention(interventionId, technicienId));
        
        assertEquals("Technicien non trouvé avec l'ID: " + technicienId, exception.getMessage());
        verify(repository, times(1)).findAllWithNullSafeDates();
        verify(userRepository, times(1)).existsById(technicienId);
    }

    @Test
    void testAssignTesteurToIntervention_Success() {
        // Given
        Long interventionId = 1L;
        String testeurCodeGMAO = "TEST001";
        
        List<Map<String, Object>> mockDataBefore = Arrays.asList(mockInterventionData);
        Map<String, Object> mockDataAfter = new HashMap<>(mockInterventionData);
        mockDataAfter.put("testeur_code_gmao", testeurCodeGMAO);
        List<Map<String, Object>> mockDataAfterList = Arrays.asList(mockDataAfter);

        when(repository.findAllWithNullSafeDates())
            .thenReturn(mockDataBefore)
            .thenReturn(mockDataAfterList);
        when(testeurRepository.existsById(testeurCodeGMAO)).thenReturn(true);
        when(repository.assignTesteurNative(interventionId, testeurCodeGMAO)).thenReturn(1);

        // When
        DemandeInterventionDTO result = service.assignTesteurToIntervention(interventionId, testeurCodeGMAO);

        // Then
        assertNotNull(result);
        assertEquals(interventionId, result.getId());
        assertEquals(testeurCodeGMAO, result.getTesteurCodeGMAO());

        verify(repository, times(2)).findAllWithNullSafeDates();
        verify(testeurRepository, times(1)).existsById(testeurCodeGMAO);
        verify(repository, times(1)).assignTesteurNative(interventionId, testeurCodeGMAO);
    }

    @Test
    void testConfirmerIntervention_Success() {
        // Given
        Long interventionId = 1L;
        
        List<Map<String, Object>> mockDataBefore = Arrays.asList(mockInterventionData);
        Map<String, Object> mockDataAfter = new HashMap<>(mockInterventionData);
        mockDataAfter.put("confirmation", 1);
        mockDataAfter.put("statut", "TERMINEE");
        mockDataAfter.put("date_validation", new Date());
        List<Map<String, Object>> mockDataAfterList = Arrays.asList(mockDataAfter);

        when(repository.findAllWithNullSafeDates())
            .thenReturn(mockDataBefore)
            .thenReturn(mockDataAfterList);
        when(repository.confirmerInterventionNative(interventionId)).thenReturn(1);

        // When
        DemandeInterventionDTO result = service.confirmerIntervention(interventionId);

        // Then
        assertNotNull(result);
        assertEquals(interventionId, result.getId());
        assertEquals(1, result.getConfirmation());
        assertEquals(StatutDemande.TERMINEE, result.getStatut());

        verify(repository, times(2)).findAllWithNullSafeDates();
        verify(repository, times(1)).confirmerInterventionNative(interventionId);
    }

    @Test
    void testUpdateDemande_Success() {
        // Given
        Long interventionId = 1L;
        DemandeInterventionDTO updateDto = new DemandeInterventionDTO();
        updateDto.setDescription("Updated description");
        updateDto.setStatut(StatutDemande.EN_COURS);
        updateDto.setPriorite("MOYENNE");
        updateDto.setTechnicienAssigneId(3L);

        Map<String, Object> mockDataAfter = new HashMap<>(mockInterventionData);
        mockDataAfter.put("description", "Updated description");
        mockDataAfter.put("statut", "EN_COURS");
        mockDataAfter.put("priorite", "MOYENNE");
        mockDataAfter.put("technicien_id", 3L);
        List<Map<String, Object>> mockDataAfterList = Arrays.asList(mockDataAfter);

        when(repository.existsById(interventionId)).thenReturn(true);
        when(repository.updateDemandeBasicFields(interventionId, "Updated description", "EN_COURS", "MOYENNE", 3L))
            .thenReturn(1);
        when(repository.findAllWithNullSafeDates()).thenReturn(mockDataAfterList);

        // When
        DemandeInterventionDTO result = service.updateDemande(interventionId, updateDto);

        // Then
        assertNotNull(result);
        assertEquals(interventionId, result.getId());
        assertEquals("Updated description", result.getDescription());
        assertEquals(StatutDemande.EN_COURS, result.getStatut());
        assertEquals("MOYENNE", result.getPriorite());
        assertEquals(3L, result.getTechnicienAssigneId());

        verify(repository, times(1)).existsById(interventionId);
        verify(repository, times(1)).updateDemandeBasicFields(interventionId, "Updated description", "EN_COURS", "MOYENNE", 3L);
        verify(repository, times(1)).findAllWithNullSafeDates();
    }

    @Test
    void testUpdateDemande_NotFound() {
        // Given
        Long interventionId = 999L;
        DemandeInterventionDTO updateDto = new DemandeInterventionDTO();
        
        when(repository.existsById(interventionId)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> service.updateDemande(interventionId, updateDto));
        
        assertEquals("Demande d'intervention non trouvée avec l'ID: " + interventionId, exception.getMessage());
        verify(repository, times(1)).existsById(interventionId);
        verify(repository, never()).updateDemandeBasicFields(any(), any(), any(), any(), any());
    }

    @Test
    void testDeleteDemande_Success() {
        // Given
        Long interventionId = 1L;

        // When
        service.deleteDemande(interventionId);

        // Then
        verify(repository, times(1)).deleteById(interventionId);
    }
}
