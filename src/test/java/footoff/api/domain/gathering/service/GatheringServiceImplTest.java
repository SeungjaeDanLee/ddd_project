package footoff.api.domain.gathering.service;

import footoff.api.domain.gathering.dto.GatheringDto;
import footoff.api.domain.gathering.dto.GatheringRequestDto;
import footoff.api.domain.gathering.dto.GatheringWithApprovedUsersDto;
import footoff.api.domain.gathering.entity.Gathering;
import footoff.api.domain.gathering.entity.GatheringLocation;
import footoff.api.domain.gathering.entity.GatheringUser;
import footoff.api.domain.gathering.repository.GatheringRepository;
import footoff.api.domain.gathering.repository.GatheringUserRepository;
import footoff.api.domain.user.entity.User;
import footoff.api.domain.user.repository.UserRepository;
import footoff.api.global.common.enums.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    private GatheringLocation testLocation;
    private GatheringRequestDto requestDto;
    private UUID testUserId;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        testUserId = UUID.randomUUID();
        testUser = User.builder()
                .id(testUserId)
                .email("test@example.com")
                .phoneNumber("010-1234-5678")
                .status(UserActivityStatus.ACTIVE)
                .language(Language.KO)
                .isVerified(false)
                .lastLoginAt(LocalDateTime.now())
                .build();

        testGathering = Gathering.builder()
                .id(1L)
                .title("테스트 모임")
                .description("테스트 모임 설명")
                .gatheringDate(LocalDateTime.now().plusDays(14))
                .minUsers(1)
                .maxUsers(10)
                .fee(0)
                .organizer(testUser)
                .build();

        testLocation = GatheringLocation.builder()
                .id(1L)
                .gathering(testGathering)
                .latitude(37.5665)
                .longitude(126.9780)
                .address("서울시 강남구")
                .placeName("테스트 장소")
                .build();

        requestDto = new GatheringRequestDto();
        // 리플렉션으로 private 필드 설정 - 실제 구현시 setter 추가 필요
        try {
            java.lang.reflect.Field titleField = requestDto.getClass().getDeclaredField("title");
            titleField.setAccessible(true);
            titleField.set(requestDto, "테스트 모임");

            java.lang.reflect.Field descField = requestDto.getClass().getDeclaredField("description");
            descField.setAccessible(true);
            descField.set(requestDto, "테스트 모임 설명");

            java.lang.reflect.Field addressField = requestDto.getClass().getDeclaredField("address");
            addressField.setAccessible(true);
            addressField.set(requestDto, "서울시 강남구");

            java.lang.reflect.Field dateField = requestDto.getClass().getDeclaredField("gatheringDate");
            dateField.setAccessible(true);
            dateField.set(requestDto, LocalDateTime.now().plusDays(14));
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
                        .status(GatheringUserStatus.APPROVED)
                        .role(GatheringUserRole.ORGANIZER)
                        .build()
        );

        // When
        GatheringDto result = gatheringService.createGathering(requestDto, testUserId);

        // Then
        assertNotNull(result);
        assertEquals("테스트 모임", result.getTitle());
        assertEquals("테스트 모임 설명", result.getDescription());
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
            gatheringService.createGathering(requestDto, testUserId);
        });

        verify(systemUserRepository, times(1)).findById(testUserId);
        verify(gatheringRepository, never()).save(any(Gathering.class));
    }

    @Test
    public void getGathering_ExistingId_ReturnsGatheringDto() {
        // Given
        Long gatheringId = 1L;
        // Set up the testGathering to have a location
        testGathering.setLocation(testLocation);
        when(gatheringRepository.findById(gatheringId)).thenReturn(Optional.of(testGathering));

        // When
        GatheringDto result = gatheringService.getGathering(gatheringId);

        // Then
        assertNotNull(result);
        assertEquals(gatheringId, result.getId());
        assertEquals("테스트 모임", result.getTitle());
        assertEquals("서울시 강남구", result.getAddress()); // This should come from the location

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
        Gathering testGathering2 = Gathering.builder()
                .id(2L)
                .title("테스트 모임 2")
                .description("테스트 모임 설명 2")
                .gatheringDate(LocalDateTime.now().plusDays(20))
                .minUsers(1)
                .maxUsers(20)
                .fee(5000)
                .organizer(testUser)
                .build();

        GatheringLocation testLocation2 = GatheringLocation.builder()
                .id(2L)
                .gathering(testGathering2)
                .latitude(37.5665)
                .longitude(126.9780)
                .address("서울시 서초구")
                .placeName("테스트 장소 2")
                .build();

        testGathering.setLocation(testLocation);
        testGathering2.setLocation(testLocation2);

        List<Gathering> gatherings = new ArrayList<>();
        gatherings.add(testGathering);
        gatherings.add(testGathering2);

        when(gatheringRepository.findAllByStatus(GatheringStatus.RECRUITMENT)).thenReturn(gatherings);

        // When
        List<GatheringWithApprovedUsersDto> result = gatheringService.getAllGatherings(null);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("테스트 모임", result.get(0).getTitle());
        assertEquals("테스트 모임 2", result.get(1).getTitle());
        assertEquals("서울시 강남구", result.get(0).getAddress());
        assertEquals("서울시 서초구", result.get(1).getAddress());

        verify(gatheringRepository, times(1)).findAllByStatus(GatheringStatus.RECRUITMENT);
    }
} 