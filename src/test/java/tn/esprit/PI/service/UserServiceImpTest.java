package tn.esprit.PI.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.esprit.PI.config.EmailService;
import tn.esprit.PI.config.JwtService;
import tn.esprit.PI.entity.User;
import tn.esprit.PI.entity.UserRole;
import tn.esprit.PI.model.UserDTO;
import tn.esprit.PI.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImpTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImp userService;

    private User mockUser;
    private UserDTO mockUserDTO;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFirstname("John");
        mockUser.setLastname("Doe");
        mockUser.setEmail("john.doe@example.com");
        mockUser.setPhoneNumber("123456789");
        mockUser.setAdress("123 Main St");
        mockUser.setRole(UserRole.TECHNICIEN_CURATIF);
        mockUser.setConfirmation(1);
        mockUser.setPassword("encodedPassword");

        mockUserDTO = new UserDTO();
        mockUserDTO.setId(1L);
        mockUserDTO.setFirstName("John");
        mockUserDTO.setLastName("Doe");
        mockUserDTO.setEmail("john.doe@example.com");
        mockUserDTO.setPhoneNumber("123456789");
        mockUserDTO.setAdress("123 Main St");
        mockUserDTO.setRole(UserRole.TECHNICIEN_CURATIF);
        mockUserDTO.setConfirmation(1);
    }

    @Test
    void testGetUserById_Success() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // When
        Optional<UserDTO> result = userService.getUserById(userId);

        // Then
        assertTrue(result.isPresent());
        UserDTO userDTO = result.get();
        assertEquals(userId, userDTO.getId());
        assertEquals("John", userDTO.getFirstName());
        assertEquals("Doe", userDTO.getLastName());
        assertEquals("john.doe@example.com", userDTO.getEmail());
        assertEquals("123456789", userDTO.getPhoneNumber());
        assertEquals("123 Main St", userDTO.getAdress());
        assertEquals(UserRole.TECHNICIEN_CURATIF, userDTO.getRole());
        assertEquals(1, userDTO.getConfirmation());
        assertNull(userDTO.getPassword()); // Password should not be set in DTO

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserById_NotFound() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        Optional<UserDTO> result = userService.getUserById(userId);

        // Then
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testFindAll_Success() {
        // Given
        User mockUser2 = new User();
        mockUser2.setId(2L);
        mockUser2.setFirstname("Jane");
        mockUser2.setLastname("Smith");
        mockUser2.setEmail("jane.smith@example.com");
        mockUser2.setRole(UserRole.ADMIN);
        mockUser2.setConfirmation(1);

        List<User> mockUsers = Arrays.asList(mockUser, mockUser2);
        when(userRepository.findAll(Sort.by("id"))).thenReturn(mockUsers);

        // When
        List<UserDTO> result = userService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        
        UserDTO dto1 = result.get(0);
        assertEquals(1L, dto1.getId());
        assertEquals("John", dto1.getFirstName());
        assertEquals("Doe", dto1.getLastName());
        
        UserDTO dto2 = result.get(1);
        assertEquals(2L, dto2.getId());
        assertEquals("Jane", dto2.getFirstName());
        assertEquals("Smith", dto2.getLastName());

        verify(userRepository, times(1)).findAll(Sort.by("id"));
    }

    @Test
    void testFindAll_EmptyList() {
        // Given
        when(userRepository.findAll(Sort.by("id"))).thenReturn(Arrays.asList());

        // When
        List<UserDTO> result = userService.findAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAll(Sort.by("id"));
    }

    @Test
    void testGet_Success() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // When
        UserDTO result = userService.get(userId);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGet_NotFound() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        UserDTO result = userService.get(userId);

        // Then
        assertNull(result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGet_NullId() {
        // When
        UserDTO result = userService.get(null);

        // Then
        assertNull(result);
        verify(userRepository, never()).findById(any());
    }

    @Test
    void testCreate_Success() {
        // Given
        UserDTO newUserDTO = new UserDTO();
        newUserDTO.setFirstName("Alice");
        newUserDTO.setLastName("Johnson");
        newUserDTO.setEmail("alice.johnson@example.com");
        newUserDTO.setRole(UserRole.MAGASINIER);

        User savedUser = new User();
        savedUser.setId(3L);
        savedUser.setFirstname("Alice");
        savedUser.setLastname("Johnson");
        savedUser.setEmail("alice.johnson@example.com");
        savedUser.setRole(UserRole.MAGASINIER);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        Long result = userService.create(newUserDTO);

        // Then
        assertNotNull(result);
        assertEquals(3L, result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testConvertToDTO_Success() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setFirstname("Test");
        user.setLastname("User");
        user.setEmail("test@example.com");
        user.setPhoneNumber("987654321");
        user.setAdress("456 Test St");
        user.setRole(UserRole.CHEF_PROJET);
        user.setConfirmation(0);
        user.setPassword("secretPassword");

        // When
        UserDTO result = userService.getUserById(1L).orElse(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        result = userService.getUserById(1L).orElse(null);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test", result.getFirstName());
        assertEquals("User", result.getLastName());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("987654321", result.getPhoneNumber());
        assertEquals("456 Test St", result.getAdress());
        assertEquals(UserRole.CHEF_PROJET, result.getRole());
        assertEquals(0, result.getConfirmation());
        assertNull(result.getPassword()); // Password should not be included in DTO
    }

    @Test
    void testUserRoles_AllRolesSupported() {
        // Test that all user roles are properly handled
        UserRole[] roles = {
            UserRole.ADMIN,
            UserRole.MAGASINIER,
            UserRole.CHEF_PROJET,
            UserRole.TECHNICIEN_CURATIF,
            UserRole.TECHNICIEN_PREVENTIF,
            UserRole.CHEF_SECTEUR,
            UserRole.SUPERVISEUR_PRODUCTION
        };

        for (UserRole role : roles) {
            // Given
            User user = new User();
            user.setId(1L);
            user.setFirstname("Test");
            user.setLastname("User");
            user.setEmail("test@example.com");
            user.setRole(role);
            user.setConfirmation(1);

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            // When
            Optional<UserDTO> result = userService.getUserById(1L);

            // Then
            assertTrue(result.isPresent());
            assertEquals(role, result.get().getRole());
        }
    }

    @Test
    void testUserConfirmationStates() {
        // Test confirmation = 0 (not confirmed)
        User unconfirmedUser = new User();
        unconfirmedUser.setId(1L);
        unconfirmedUser.setFirstname("Unconfirmed");
        unconfirmedUser.setLastname("User");
        unconfirmedUser.setEmail("unconfirmed@example.com");
        unconfirmedUser.setRole(UserRole.TECHNICIEN_CURATIF);
        unconfirmedUser.setConfirmation(0);

        when(userRepository.findById(1L)).thenReturn(Optional.of(unconfirmedUser));

        Optional<UserDTO> result = userService.getUserById(1L);
        assertTrue(result.isPresent());
        assertEquals(0, result.get().getConfirmation());

        // Test confirmation = 1 (confirmed)
        User confirmedUser = new User();
        confirmedUser.setId(2L);
        confirmedUser.setFirstname("Confirmed");
        confirmedUser.setLastname("User");
        confirmedUser.setEmail("confirmed@example.com");
        confirmedUser.setRole(UserRole.ADMIN);
        confirmedUser.setConfirmation(1);

        when(userRepository.findById(2L)).thenReturn(Optional.of(confirmedUser));

        result = userService.getUserById(2L);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getConfirmation());
    }
}
