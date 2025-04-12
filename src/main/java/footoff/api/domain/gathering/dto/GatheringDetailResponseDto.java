package footoff.api.domain.gathering.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import footoff.api.domain.gathering.entity.Gathering;
import footoff.api.global.common.enums.GatheringUserStatus;
import lombok.Builder;
import lombok.Getter;

/**
 * 모임 상세 정보를 담는 응답 DTO 클래스
 * 모임의 기본 정보, 참가자 정보, 장소 정보 등을 포함한다
 */
@Getter
public class GatheringDetailResponseDto {
    private final Long id;
    private final String title;
    private final String description;
    private final LocalDateTime gatheringDate;
    private final Integer minUsers;
    private final Integer maxUsers;
    private final Integer fee;
    private final String organizerId;
    private final String organizerEmail;
    private final String organizerNickname;
    private final String organizerProfileImage;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final int userCount;
    private final int pendingUserCount;
    private final int approvedUserCount;
    private final GatheringLocationDto location;
    private final List<ParticipantDto> participants;
    private final boolean isFull;
    private final boolean isJoined;
    private final String joinStatus;

    /**
     * GatheringDetailResponseDto 생성을 위한 빌더 메서드
     * 
     * @param id 모임 고유 식별자
     * @param title 모임 제목
     * @param description 모임 설명
     * @param gatheringDate 모임 진행 일시
     * @param minUsers 모임 최소 인원
     * @param maxUsers 모임 최대 인원
     * @param fee 모임 참가비
     * @param organizerId 주최자 ID
     * @param organizerEmail 주최자 이메일
     * @param organizerNickname 주최자 닉네임
     * @param organizerProfileImage 주최자 프로필 이미지
     * @param createdAt 모임 생성 시간
     * @param updatedAt 모임 정보 업데이트 시간
     * @param userCount 전체 참가자 수
     * @param pendingUserCount 대기 중인 사용자 수
     * @param approvedUserCount 승인된 사용자 수
     * @param location 모임 장소 정보
     * @param participants 참가자 목록
     * @param isFull 모임 가득참 여부
     * @param isJoined 현재 사용자의 참가 여부
     * @param joinStatus 현재 사용자의 참가 상태
     */
    @Builder
    public GatheringDetailResponseDto(Long id, String title, String description,
                     LocalDateTime gatheringDate, Integer minUsers, Integer maxUsers, 
                     Integer fee, String organizerId, String organizerEmail, String organizerNickname,
                     String organizerProfileImage, LocalDateTime createdAt, LocalDateTime updatedAt, 
                     int userCount, int pendingUserCount, int approvedUserCount, GatheringLocationDto location, 
                     List<ParticipantDto> participants, boolean isFull, boolean isJoined, String joinStatus) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.gatheringDate = gatheringDate;
        this.minUsers = minUsers;
        this.maxUsers = maxUsers;
        this.fee = fee;
        this.organizerId = organizerId;
        this.organizerEmail = organizerEmail;
        this.organizerNickname = organizerNickname;
        this.organizerProfileImage = organizerProfileImage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userCount = userCount;
        this.pendingUserCount = pendingUserCount;
        this.approvedUserCount = approvedUserCount;
        this.location = location;
        this.participants = participants;
        this.isFull = isFull;
        this.isJoined = isJoined;
        this.joinStatus = joinStatus;
    }
    
    /**
     * Gathering 엔티티로부터 GatheringDetailResponseDto를 생성하는 정적 팩토리 메서드
     * 
     * @param gathering 모임 엔티티
     * @param currentUserId 현재 요청한 사용자의 ID
     * @return 생성된 GatheringDetailResponseDto 객체
     */
    public static GatheringDetailResponseDto fromEntity(Gathering gathering, String currentUserId) {
        GatheringLocationDto locationDto = GatheringLocationDto.fromEntity(gathering.getLocation());
        
        List<ParticipantDto> participants = gathering.getUsers().stream()
                .map(ParticipantDto::fromGatheringUser)
                .collect(Collectors.toList());
        
        // 승인된 사용자 수 계산
        long approvedCount = gathering.getUsers().stream()
                .filter(gu -> GatheringUserStatus.APPROVED.equals(gu.getStatus()))
                .count();
        
        // 대기 중인 사용자 수 계산
        long pendingCount = gathering.getUsers().stream()
                .filter(gu -> GatheringUserStatus.PENDING.equals(gu.getStatus()))
                .count();
        
        // 현재 사용자의 참가 상태 확인
        boolean isJoined = false;
        String joinStatus = null;
        
        if (currentUserId != null) {
            var userParticipation = gathering.getUsers().stream()
                    .filter(gu -> gu.getUser().getId().toString().equals(currentUserId))
                    .findFirst();
            
            isJoined = userParticipation.isPresent();
            if (isJoined) {
                joinStatus = userParticipation.get().getStatus().name();
            }
        }
        
        boolean isFull = approvedCount >= gathering.getMaxUsers();
        
        return GatheringDetailResponseDto.builder()
                .id(gathering.getId())
                .title(gathering.getTitle())
                .description(gathering.getDescription())
                .gatheringDate(gathering.getGatheringDate())
                .minUsers(gathering.getMinUsers())
                .maxUsers(gathering.getMaxUsers())
                .fee(gathering.getFee())
                .organizerId(gathering.getOrganizer().getId().toString())
                .organizerEmail(gathering.getOrganizer().getEmail())
                .organizerNickname(gathering.getOrganizer().getProfile().getNickname())
                .organizerProfileImage(gathering.getOrganizer().getProfileImageUrl())
                .createdAt(gathering.getCreatedAt())
                .updatedAt(gathering.getUpdatedAt())
                .userCount(gathering.getUsers().size())
                .pendingUserCount((int) pendingCount)
                .approvedUserCount((int) approvedCount)
                .location(locationDto)
                .participants(participants)
                .isFull(isFull)
                .isJoined(isJoined)
                .joinStatus(joinStatus)
                .build();
    }
    
    /**
     * 모임 참가자 정보를 담는 내부 DTO 클래스
     */
    @Getter
    public static class ParticipantDto {
        private final String userId;
        private final String nickname;
        private final String profileImage;
        private final String status;
        
        /**
         * ParticipantDto 생성을 위한 빌더 메서드
         * 
         * @param userId 참가자 ID
         * @param nickname 참가자 닉네임
         * @param profileImage 참가자 프로필 이미지
         * @param status 참가 상태
         */
        @Builder
        public ParticipantDto(String userId, String nickname, String profileImage, String status) {
            this.userId = userId;
            this.nickname = nickname;
            this.profileImage = profileImage;
            this.status = status;
        }
        
        /**
         * GatheringUser 엔티티로부터 ParticipantDto를 생성하는 정적 팩토리 메서드
         * 
         * @param gatheringUser 모임 참가자 엔티티
         * @return 생성된 ParticipantDto 객체
         */
        public static ParticipantDto fromGatheringUser(footoff.api.domain.gathering.entity.GatheringUser gatheringUser) {
            return ParticipantDto.builder()
                    .userId(gatheringUser.getUser().getId().toString())
                    .nickname(gatheringUser.getUser().getProfile().getNickname())
                    .profileImage(gatheringUser.getUser().getProfileImageUrl())
                    .status(gatheringUser.getStatus().name())
                    .build();
        }
    }
} 