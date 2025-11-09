package tn.esprit.PI.Services;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class KPIServiceTest {

    @Test
    void testKPIServiceClassExists() {
        // Test basique pour vérifier l'existence de la classe
        assertNotNull(KPIService.class);
    }
    
    @Test
    void testKPIServiceHasCorrectPackage() {
        // Test pour vérifier le package
        assertEquals("tn.esprit.PI.Services", KPIService.class.getPackageName());
    }
}
