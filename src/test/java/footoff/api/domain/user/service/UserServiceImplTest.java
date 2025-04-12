package footoff.api.domain.user.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import footoff.api.domain.user.entity.User;
import footoff.api.domain.user.repository.UserRepository;
import footoff.api.global.common.enums.Language;
import footoff.api.global.common.enums.UserActivityStatus;

public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;
    
    @Mock
    private UserRepository userRepository;
    
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    public void getUsers_ShouldReturnAllUsers() {
        // Given
        User user1 = User.builder()
                .id(UUID.randomUUID())
                .email("user1@example.com")
                .phoneNumber("010-1234-5678")
                .status(UserActivityStatus.ACTIVE)
                .language(Language.KO)
                .isVerified(false)
                .lastLoginAt(LocalDateTime.now())
                .build();
        
        User user2 = User.builder()
                .id(UUID.randomUUID())
                .email("user2@example.com")
                .lastLoginAt(LocalDateTime.now())
                .build();
        
        List<User> expectedUsers = Arrays.asList(user1, user2);
        
        
        // When
        List<User> actualUsers = userService.getUsers();
        
        // Then
        assertNotNull(actualUsers);
        assertEquals(2, actualUsers.size());
        assertEquals(expectedUsers, actualUsers);
        
        verify(userRepository, times(1)).findAll();
    }
} 