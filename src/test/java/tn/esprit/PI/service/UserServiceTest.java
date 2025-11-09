package tn.esprit.PI.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class UserServiceTest {

    @Test
    void testUserServiceExists() {
        // Test basique pour vérifier l'existence du service
        assertNotNull(UserServiceImp.class);
    }
    
    @Test
    void testRetrieveveUserIdByEmailMethodExists() {
        // Test pour vérifier que la méthode existe
        try {
            UserServiceImp.class.getMethod("RetrieveveUserIdByEmail", String.class);
        } catch (NoSuchMethodException e) {
            fail("La méthode RetrieveveUserIdByEmail devrait exister");
        }
    }
}
