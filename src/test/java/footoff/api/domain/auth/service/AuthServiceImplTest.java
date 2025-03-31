package footoff.api.domain.auth.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import footoff.api.domain.auth.dto.KaKaoLoginResponseDTO;
import footoff.api.domain.auth.dto.KakaoDTO;
import footoff.api.domain.auth.entity.KakaoAccount;
import footoff.api.domain.auth.repository.KakaoAccountRepository;
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
    private KakaoAccountRepository kakaoAccountRepository;

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
        
        KakaoAccount savedKakaoAccount = KakaoAccount.builder()
                .id(kakaoId)
                .user(savedUser)
                .build();
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(kakaoAccountRepository.save(any(KakaoAccount.class))).thenReturn(savedKakaoAccount);
        
        // When
        KakaoAccount result = authService.createKakaoAccount(kakaoId, name, age);
        
        // Then
        assertNotNull(result);
        assertEquals(kakaoId, result.getId());
        assertEquals(savedUser, result.getUser());
        assertEquals(name, result.getUser().getName());
        assertEquals(age, result.getUser().getAge());
        
        verify(userRepository, times(1)).save(any(User.class));
        verify(kakaoAccountRepository, times(1)).save(any(KakaoAccount.class));
    }
} 