package footoff.api.domain.user.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import footoff.api.domain.user.entity.User;
import footoff.api.domain.user.repository.UserRepository;

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
                .name("사용자1")
                .age(25)
                .createDate(new Date())
                .updateDate(new Date())
                .build();
        
        User user2 = User.builder()
                .id(UUID.randomUUID())
                .name("사용자2")
                .age(30)
                .createDate(new Date())
                .updateDate(new Date())
                .build();
        
        List<User> expectedUsers = Arrays.asList(user1, user2);
        
        when(userRepository.findAll()).thenReturn(expectedUsers);
        
        // When
        List<User> actualUsers = userService.getUsers();
        
        // Then
        assertNotNull(actualUsers);
        assertEquals(2, actualUsers.size());
        assertEquals(expectedUsers, actualUsers);
        
        verify(userRepository, times(1)).findAll();
    }
} 