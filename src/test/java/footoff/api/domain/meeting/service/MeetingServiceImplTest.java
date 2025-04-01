package footoff.api.domain.meeting.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import footoff.api.domain.meeting.dto.MeetingCreateRequestDto;
import footoff.api.domain.meeting.dto.MeetingDto;
import footoff.api.domain.meeting.entity.Meeting;
import footoff.api.domain.meeting.entity.MeetingMember;
import footoff.api.domain.meeting.repository.MeetingMembershipRepository;
import footoff.api.domain.meeting.repository.MeetingRepository;
import footoff.api.domain.user.entity.User;
import footoff.api.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

public class MeetingServiceImplTest {

    @InjectMocks
    private MeetingServiceImpl meetingService;
    
    @Mock
    private MeetingRepository meetingRepository;
    
    @Mock
    private MeetingMembershipRepository membershipRepository;
    
    @Mock
    private UserRepository userRepository;
    
    private User testUser;
    private Meeting testMeeting;
    private MeetingCreateRequestDto createRequestDto;
    private UUID testUserId;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        testUserId = UUID.randomUUID();
        testUser = User.builder()
                .id(testUserId)
                .name("테스트유저")
                .age(30)
                .createDate(new Date())
                .updateDate(new Date())
                .build();
        
        testMeeting = Meeting.builder()
                .id(1L)
                .title("테스트 모임")
                .description("테스트 모임 설명")
                .address("서울시 강남구")
                .applicationDeadline(LocalDateTime.now().plusDays(7))
                .meetingDate(LocalDateTime.now().plusDays(14))
                .organizer(testUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        createRequestDto = new MeetingCreateRequestDto();
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
            
            java.lang.reflect.Field dateField = createRequestDto.getClass().getDeclaredField("meetingDate");
            dateField.setAccessible(true);
            dateField.set(createRequestDto, LocalDateTime.now().plusDays(14));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void createMeeting_ValidRequest_ReturnsMeetingDto() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(meetingRepository.save(any(Meeting.class))).thenReturn(testMeeting);
        when(membershipRepository.save(any(MeetingMember.class))).thenReturn(
                MeetingMember.builder()
                    .meeting(testMeeting)
                    .user(testUser)
                    .status(MembershipStatus.APPROVED)
                    .role(MemberRole.ORGANIZER)
                    .build()
        );
        
        // When
        MeetingDto result = meetingService.createMeeting(createRequestDto, testUserId);
        
        // Then
        assertNotNull(result);
        assertEquals("테스트 모임", result.getTitle());
        assertEquals("테스트 모임 설명", result.getDescription());
        assertEquals("서울시 강남구", result.getAddress());
        assertEquals(testUserId.toString(), result.getOrganizerId());
        
        verify(userRepository, times(1)).findById(testUserId);
        verify(meetingRepository, times(1)).save(any(Meeting.class));
        verify(membershipRepository, times(1)).save(any(MeetingMember.class));
    }
    
    @Test
    public void createMeeting_UserNotFound_ThrowsException() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            meetingService.createMeeting(createRequestDto, testUserId);
        });
        
        verify(userRepository, times(1)).findById(testUserId);
        verify(meetingRepository, never()).save(any(Meeting.class));
    }
    
    @Test
    public void getMeeting_ExistingId_ReturnsMeetingDto() {
        // Given
        Long meetingId = 1L;
        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(testMeeting));
        
        // When
        MeetingDto result = meetingService.getMeeting(meetingId);
        
        // Then
        assertNotNull(result);
        assertEquals(meetingId, result.getId());
        assertEquals("테스트 모임", result.getTitle());
        
        verify(meetingRepository, times(1)).findById(meetingId);
    }
    
    @Test
    public void getMeeting_NonExistingId_ThrowsException() {
        // Given
        Long meetingId = 99L;
        when(meetingRepository.findById(meetingId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            meetingService.getMeeting(meetingId);
        });
        
        verify(meetingRepository, times(1)).findById(meetingId);
    }
    
    @Test
    public void getAllMeetings_ReturnsListOfMeetingDto() {
        // Given
        List<Meeting> meetings = new ArrayList<>();
        meetings.add(testMeeting);
        meetings.add(Meeting.builder()
                .id(2L)
                .title("테스트 모임 2")
                .description("테스트 모임 설명 2")
                .address("서울시 서초구")
                .applicationDeadline(LocalDateTime.now().plusDays(10))
                .meetingDate(LocalDateTime.now().plusDays(20))
                .organizer(testUser)
                .build());
        
        when(meetingRepository.findAll()).thenReturn(meetings);
        
        // When
        List<MeetingDto> result = meetingService.getAllMeetings();
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("테스트 모임", result.get(0).getTitle());
        assertEquals("테스트 모임 2", result.get(1).getTitle());
        
        verify(meetingRepository, times(1)).findAll();
    }
} 