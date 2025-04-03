package footoff.api.domain.gathering.service;

import footoff.api.domain.gathering.dto.GatheringCreateRequestDto;
import footoff.api.domain.gathering.dto.GatheringDto;
import footoff.api.domain.gathering.entity.Gathering;
import footoff.api.domain.gathering.entity.GatheringUser;
import footoff.api.domain.gathering.repository.GatheringUserRepository;
import footoff.api.domain.gathering.repository.GatheringRepository;
import footoff.api.domain.user.entity.User;
import footoff.api.domain.user.repository.UserRepository;
import footoff.api.global.common.enums.UserStatus;
import footoff.api.global.common.enums.UserRole;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GatheringServiceImplTest {

    @InjectMocks
    private GatheringServiceImpl gatheringService;

    @Mock
    private GatheringRepository gatheringRepository;

    @Mock
    private GatheringUserRepository userRepository;

    @Mock
    private UserRepository systemUserRepository;

    private User testUser;
    private Gathering testGathering;
    private GatheringCreateRequestDto createRequestDto;
    private UUID testUserId;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        testUserId = UUID.randomUUID();
        testUser = User.builder()
                .id(testUserId)
                .name("테스트유저")
                .age(30)
                .build();

        testGathering = Gathering.builder()
                .id(1L)
                .title("테스트 모임")
                .description("테스트 모임 설명")
                .address("서울시 강남구")
                .applicationDeadline(LocalDateTime.now().plusDays(7))
                .gatheringDate(LocalDateTime.now().plusDays(14))
                .organizer(testUser)
                .build();

        createRequestDto = new GatheringCreateRequestDto();
        // 리플렉션으로 private 필드 설정 - 실제 구현시 setter 추가 필요
        try {
            java.lang.reflect.Field titleField = createRequestDto.getClass().getDeclaredField("title");
            titleField.setAccessible(true);
            titleField.set(createRequestDto, "테스트 모임");

            java.lang.reflect.Field descField = createRequestDto.getClass().getDeclaredField("description");
            descField.setAccessible(true);
            descField.set(createRequestDto, "테스트 모임 설명");

            java.lang.reflect.Field addressField = createRequestDto.getClass().getDeclaredField("address");
            addressField.setAccessible(true);
            addressField.set(createRequestDto, "서울시 강남구");

            java.lang.reflect.Field deadlineField = createRequestDto.getClass().getDeclaredField("applicationDeadline");
            deadlineField.setAccessible(true);
            deadlineField.set(createRequestDto, LocalDateTime.now().plusDays(7));

            java.lang.reflect.Field dateField = createRequestDto.getClass().getDeclaredField("gatheringDate");
            dateField.setAccessible(true);
            dateField.set(createRequestDto, LocalDateTime.now().plusDays(14));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createGathering_ValidRequest_ReturnsGatheringDto() {
        // Given
        when(systemUserRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(gatheringRepository.save(any(Gathering.class))).thenReturn(testGathering);
        when(userRepository.save(any(GatheringUser.class))).thenReturn(
                GatheringUser.builder()
                        .gathering(testGathering)
                        .user(testUser)
                        .status(UserStatus.APPROVED)
                        .role(UserRole.ORGANIZER)
                        .build()
        );

        // When
        GatheringDto result = gatheringService.createGathering(createRequestDto, testUserId);

        // Then
        assertNotNull(result);
        assertEquals("테스트 모임", result.getTitle());
        assertEquals("테스트 모임 설명", result.getDescription());
        assertEquals("서울시 강남구", result.getAddress());
        assertEquals(testUserId.toString(), result.getOrganizerId());

        verify(systemUserRepository, times(1)).findById(testUserId);
        verify(gatheringRepository, times(1)).save(any(Gathering.class));
        verify(userRepository, times(1)).save(any(GatheringUser.class));
    }

    @Test
    public void createGathering_UserNotFound_ThrowsException() {
        // Given
        when(systemUserRepository.findById(testUserId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            gatheringService.createGathering(createRequestDto, testUserId);
        });

        verify(systemUserRepository, times(1)).findById(testUserId);
        verify(gatheringRepository, never()).save(any(Gathering.class));
    }

    @Test
    public void getGathering_ExistingId_ReturnsGatheringDto() {
        // Given
        Long gatheringId = 1L;
        when(gatheringRepository.findById(gatheringId)).thenReturn(Optional.of(testGathering));

        // When
        GatheringDto result = gatheringService.getGathering(gatheringId);

        // Then
        assertNotNull(result);
        assertEquals(gatheringId, result.getId());
        assertEquals("테스트 모임", result.getTitle());

        verify(gatheringRepository, times(1)).findById(gatheringId);
    }

    @Test
    public void getGathering_NonExistingId_ThrowsException() {
        // Given
        Long gatheringId = 99L;
        when(gatheringRepository.findById(gatheringId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            gatheringService.getGathering(gatheringId);
        });

        verify(gatheringRepository, times(1)).findById(gatheringId);
    }

    @Test
    public void getAllGatherings_ReturnsListOfGatheringDto() {
        // Given
        List<Gathering> gatherings = new ArrayList<>();
        gatherings.add(testGathering);
        gatherings.add(Gathering.builder()
                .id(2L)
                .title("테스트 모임 2")
                .description("테스트 모임 설명 2")
                .address("서울시 서초구")
                .applicationDeadline(LocalDateTime.now().plusDays(10))
                .gatheringDate(LocalDateTime.now().plusDays(20))
                .organizer(testUser)
                .build());

        when(gatheringRepository.findAll()).thenReturn(gatherings);

        // When
        List<GatheringDto> result = gatheringService.getAllGatherings();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("테스트 모임", result.get(0).getTitle());
        assertEquals("테스트 모임 2", result.get(1).getTitle());

        verify(gatheringRepository, times(1)).findAll();
    }
} 