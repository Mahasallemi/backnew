package tn.esprit.PI.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TesteurTest {

    @Test
    void testTesteurCreation() {
        Testeur testeur = new Testeur();
        assertNotNull(testeur, "Testeur should not be null");
    }
    
    @Test
    void testTesteurCodeGMAO() {
        Testeur testeur = new Testeur();
        testeur.setCodeGMAO("TEST001");
        assertEquals("TEST001", testeur.getCodeGMAO());
    }
    
    @Test
    void testTesteurClass() {
        assertEquals("tn.esprit.PI.entity", Testeur.class.getPackageName());
        assertNotNull(Testeur.class.getSimpleName());
    }
}
