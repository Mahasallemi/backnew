package tn.esprit.PI.Services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.PI.entity.Testeur;
import tn.esprit.PI.entity.TesteurDTO;
import tn.esprit.PI.repository.TesteurRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TesteurServiceTest {

    @Mock
    private TesteurRepository testeurRepository;

    @InjectMocks
    private TesteurService testeurService;

    private Testeur mockTesteur;
    private Testeur mockTesteur2;

    @BeforeEach
    void setUp() {
        mockTesteur = new Testeur();
        mockTesteur.setCodeGMAO("TEST001");
        mockTesteur.setAtelier("Atelier A");
        mockTesteur.setLigne("Ligne 1");
        mockTesteur.setBancTest("Banc Test 1");

        mockTesteur2 = new Testeur();
        mockTesteur2.setCodeGMAO("TEST002");
        mockTesteur2.setAtelier("Atelier B");
        mockTesteur2.setLigne("Ligne 2");
        mockTesteur2.setBancTest("Banc Test 2");
    }

    @Test
    void testCreateTesteur_Success() {
        // Given
        when(testeurRepository.save(any(Testeur.class))).thenReturn(mockTesteur);

        // When
        Testeur result = testeurService.createTesteur(mockTesteur);

        // Then
        assertNotNull(result);
        assertEquals("TEST001", result.getCodeGMAO());
        assertEquals("Atelier A", result.getAtelier());
        assertEquals("Ligne 1", result.getLigne());
        assertEquals("Banc Test 1", result.getBancTest());

        verify(testeurRepository, times(1)).save(mockTesteur);
    }

    @Test
    void testGetAllTesteurs_Success() {
        // Given
        List<Testeur> mockTesteurs = Arrays.asList(mockTesteur, mockTesteur2);
        when(testeurRepository.findAll()).thenReturn(mockTesteurs);

        // When
        List<Testeur> result = testeurService.getAllTesteurs();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("TEST001", result.get(0).getCodeGMAO());
        assertEquals("TEST002", result.get(1).getCodeGMAO());

        verify(testeurRepository, times(1)).findAll();
    }

    @Test
    void testGetAllTesteurs_EmptyList() {
        // Given
        when(testeurRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Testeur> result = testeurService.getAllTesteurs();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(testeurRepository, times(1)).findAll();
    }

    @Test
    void testGetAllTesteursDTO_Success() {
        // Given
        List<Testeur> mockTesteurs = Arrays.asList(mockTesteur, mockTesteur2);
        when(testeurRepository.findAll()).thenReturn(mockTesteurs);

        // When
        List<TesteurDTO> result = testeurService.getAllTesteursDTO();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        
        TesteurDTO dto1 = result.get(0);
        assertEquals("TEST001", dto1.getCodeGMAO());
        assertEquals("Atelier A", dto1.getAtelier());
        assertEquals("Ligne 1", dto1.getLigne());
        assertEquals("Banc Test 1", dto1.getBancTest());
        assertEquals(0, dto1.getNombreInterventions());
        assertTrue(dto1.getInterventionIds().isEmpty());

        TesteurDTO dto2 = result.get(1);
        assertEquals("TEST002", dto2.getCodeGMAO());
        assertEquals("Atelier B", dto2.getAtelier());

        verify(testeurRepository, times(1)).findAll();
    }

    @Test
    void testGetAllTesteursDTO_WithNullTesteur() {
        // Given
        List<Testeur> mockTesteurs = Arrays.asList(mockTesteur, null, mockTesteur2);
        when(testeurRepository.findAll()).thenReturn(mockTesteurs);

        // When
        List<TesteurDTO> result = testeurService.getAllTesteursDTO();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size()); // null testeur should be filtered out
        assertEquals("TEST001", result.get(0).getCodeGMAO());
        assertEquals("TEST002", result.get(1).getCodeGMAO());

        verify(testeurRepository, times(1)).findAll();
    }

    @Test
    void testGetAllTesteursDTO_ExceptionHandling() {
        // Given
        when(testeurRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // When
        List<TesteurDTO> result = testeurService.getAllTesteursDTO();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(testeurRepository, times(1)).findAll();
    }

    @Test
    void testGetTesteurByAtelierAndLigne_Success() {
        // Given
        String atelier = "Atelier A";
        String ligne = "Ligne 1";
        List<Testeur> mockTesteurs = Arrays.asList(mockTesteur);
        when(testeurRepository.findByAtelierAndLigne(atelier, ligne)).thenReturn(mockTesteurs);

        // When
        Optional<Testeur> result = testeurService.getTesteurByAtelierAndLigne(atelier, ligne);

        // Then
        assertTrue(result.isPresent());
        assertEquals("TEST001", result.get().getCodeGMAO());
        assertEquals(atelier, result.get().getAtelier());
        assertEquals(ligne, result.get().getLigne());

        verify(testeurRepository, times(1)).findByAtelierAndLigne(atelier, ligne);
    }

    @Test
    void testGetTesteurByAtelierAndLigne_NotFound() {
        // Given
        String atelier = "Atelier X";
        String ligne = "Ligne X";
        when(testeurRepository.findByAtelierAndLigne(atelier, ligne)).thenReturn(Arrays.asList());

        // When
        Optional<Testeur> result = testeurService.getTesteurByAtelierAndLigne(atelier, ligne);

        // Then
        assertFalse(result.isPresent());

        verify(testeurRepository, times(1)).findByAtelierAndLigne(atelier, ligne);
    }

    @Test
    void testUpdateTesteur_Success_SameCodeGMAO() {
        // Given
        String atelier = "Atelier A";
        String ligne = "Ligne 1";
        Testeur existingTesteur = new Testeur();
        existingTesteur.setCodeGMAO("TEST001");
        existingTesteur.setAtelier(atelier);
        existingTesteur.setLigne(ligne);
        existingTesteur.setBancTest("Old Banc Test");

        Testeur updatedDetails = new Testeur();
        updatedDetails.setCodeGMAO("TEST001"); // Same code GMAO
        updatedDetails.setBancTest("New Banc Test");

        List<Testeur> mockTesteurs = Arrays.asList(existingTesteur);
        when(testeurRepository.findByAtelierAndLigne(atelier, ligne)).thenReturn(mockTesteurs);
        when(testeurRepository.save(any(Testeur.class))).thenReturn(existingTesteur);

        // When
        Testeur result = testeurService.updateTesteur(atelier, ligne, updatedDetails);

        // Then
        assertNotNull(result);
        assertEquals("TEST001", result.getCodeGMAO());
        
        verify(testeurRepository, times(1)).findByAtelierAndLigne(atelier, ligne);
        verify(testeurRepository, times(1)).save(existingTesteur);
        verify(testeurRepository, never()).delete(any());
    }

    @Test
    void testUpdateTesteur_Success_DifferentCodeGMAO() {
        // Given
        String atelier = "Atelier A";
        String ligne = "Ligne 1";
        Testeur existingTesteur = new Testeur();
        existingTesteur.setCodeGMAO("TEST001");
        existingTesteur.setAtelier(atelier);
        existingTesteur.setLigne(ligne);
        existingTesteur.setBancTest("Old Banc Test");

        Testeur updatedDetails = new Testeur();
        updatedDetails.setCodeGMAO("TEST999"); // Different code GMAO
        updatedDetails.setBancTest("New Banc Test");

        Testeur newTesteur = new Testeur();
        newTesteur.setCodeGMAO("TEST999");
        newTesteur.setAtelier(atelier);
        newTesteur.setLigne(ligne);
        newTesteur.setBancTest("New Banc Test");

        List<Testeur> mockTesteurs = Arrays.asList(existingTesteur);
        when(testeurRepository.findByAtelierAndLigne(atelier, ligne)).thenReturn(mockTesteurs);
        when(testeurRepository.save(any(Testeur.class))).thenReturn(newTesteur);

        // When
        Testeur result = testeurService.updateTesteur(atelier, ligne, updatedDetails);

        // Then
        assertNotNull(result);
        assertEquals("TEST999", result.getCodeGMAO());
        assertEquals(atelier, result.getAtelier());
        assertEquals(ligne, result.getLigne());
        assertEquals("New Banc Test", result.getBancTest());
        
        verify(testeurRepository, times(1)).findByAtelierAndLigne(atelier, ligne);
        verify(testeurRepository, times(1)).delete(existingTesteur);
        verify(testeurRepository, times(1)).save(any(Testeur.class));
    }

    @Test
    void testUpdateTesteur_NotFound() {
        // Given
        String atelier = "Atelier X";
        String ligne = "Ligne X";
        Testeur updatedDetails = new Testeur();
        updatedDetails.setCodeGMAO("TEST999");

        when(testeurRepository.findByAtelierAndLigne(atelier, ligne)).thenReturn(Arrays.asList());

        // When
        Testeur result = testeurService.updateTesteur(atelier, ligne, updatedDetails);

        // Then
        assertNull(result);
        
        verify(testeurRepository, times(1)).findByAtelierAndLigne(atelier, ligne);
        verify(testeurRepository, never()).save(any());
        verify(testeurRepository, never()).delete(any());
    }

    @Test
    void testDeleteTesteur_Success() {
        // Given
        String atelier = "Atelier A";
        String ligne = "Ligne 1";
        List<Testeur> mockTesteurs = Arrays.asList(mockTesteur);
        when(testeurRepository.findByAtelierAndLigne(atelier, ligne)).thenReturn(mockTesteurs);

        // When
        testeurService.deleteTesteur(atelier, ligne);

        // Then
        verify(testeurRepository, times(1)).findByAtelierAndLigne(atelier, ligne);
        verify(testeurRepository, times(1)).delete(mockTesteur);
    }

    @Test
    void testDeleteTesteur_NotFound() {
        // Given
        String atelier = "Atelier X";
        String ligne = "Ligne X";
        when(testeurRepository.findByAtelierAndLigne(atelier, ligne)).thenReturn(Arrays.asList());

        // When
        testeurService.deleteTesteur(atelier, ligne);

        // Then
        verify(testeurRepository, times(1)).findByAtelierAndLigne(atelier, ligne);
        verify(testeurRepository, never()).delete(any());
    }
}
