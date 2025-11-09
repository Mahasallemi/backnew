package tn.esprit.PI.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserCreation() {
        User user = new User();
        assertNotNull(user, "User should not be null");
    }
    
    @Test
    void testUserEmail() {
        User user = new User();
        user.setEmail("test@example.com");
        assertEquals("test@example.com", user.getEmail());
    }
    
    @Test
    void testUserClass() {
        assertEquals("tn.esprit.PI.entity", User.class.getPackageName());
        assertNotNull(User.class.getSimpleName());
    }
}
