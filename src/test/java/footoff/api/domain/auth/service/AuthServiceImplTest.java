package footoff.api.domain.auth.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import footoff.api.domain.auth.entity.UserSocialAccount;
import footoff.api.domain.auth.repository.UserSocialAccountRepository;
import footoff.api.domain.auth.util.KakaoUtil;
import footoff.api.domain.user.entity.User;
import footoff.api.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;

public class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private KakaoUtil kakaoUtil;

    @Mock
    private UserSocialAccountRepository userSocialAccountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletResponse httpServletResponse;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createKakaoAccount_ShouldCreateAndSaveUser() {
        // Given
        Long kakaoId = 123456L;
        String name = "테스트사용자";
        int age = 25;
        
        User savedUser = User.builder()
                .id(UUID.randomUUID())
                .name(name)
                .age(age)
                .build();
        
        UserSocialAccount savedKakaoAccount = UserSocialAccount.builder()
                .id(kakaoId)
                .user(savedUser)
                .build();
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userSocialAccountRepository.save(any(UserSocialAccount.class))).thenReturn(savedKakaoAccount);
        
        // When
        UserSocialAccount result = authService.createKakaoAccount(kakaoId, name, age);
        
        // Then
        assertNotNull(result);
        assertEquals(kakaoId, result.getId());
        assertEquals(savedUser, result.getUser());
        assertEquals(name, result.getUser().getName());
        assertEquals(age, result.getUser().getAge());
        
        verify(userRepository, times(1)).save(any(User.class));
        verify(userSocialAccountRepository, times(1)).save(any(UserSocialAccount.class));
    }
} 