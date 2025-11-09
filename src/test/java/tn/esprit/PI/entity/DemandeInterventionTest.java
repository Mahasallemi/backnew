package tn.esprit.PI.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DemandeInterventionTest {

    @Test
    void testDemandeInterventionCreation() {
        DemandeIntervention demande = new DemandeIntervention();
        assertNotNull(demande, "DemandeIntervention should not be null");
    }
    
    @Test
    void testDemandeInterventionDescription() {
        DemandeIntervention demande = new DemandeIntervention();
        demande.setDescription("Test intervention");
        assertEquals("Test intervention", demande.getDescription());
    }
    
    @Test
    void testDemandeInterventionClass() {
        assertEquals("tn.esprit.PI.entity", DemandeIntervention.class.getPackageName());
        assertNotNull(DemandeIntervention.class.getSimpleName());
    }
}
