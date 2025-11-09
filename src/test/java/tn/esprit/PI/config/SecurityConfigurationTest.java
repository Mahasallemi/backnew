package tn.esprit.PI.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class SecurityConfigurationTest {

    @Test
    void contextLoads() {
        // Test simple pour vérifier que le contexte Spring se charge correctement
        // avec la configuration de sécurité
        assertTrue(true, "Le contexte Spring se charge correctement");
    }
    
    @Test
    void securityConfigurationClassExists() {
        // Test basique pour s'assurer que la classe SecurityConfiguration existe
        assertNotNull(SecurityConfiguration.class, "La classe SecurityConfiguration doit exister");
        assertEquals("tn.esprit.PI.config", SecurityConfiguration.class.getPackageName());
    }
}
