package footoff.api.domain.gathering.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import footoff.api.domain.user.entity.User;
import footoff.api.global.common.entity.BaseEntity;
import footoff.api.global.common.enums.GatheringStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 모임 정보를 담는 엔티티 클래스
 */
@Entity
@Table(name = "gathering")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Gathering extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "gathering_date", nullable = false)
    private LocalDateTime gatheringDate;
    
    @Column(name = "min_users", nullable = false)
    private Integer minUsers;
    
    @Column(name = "max_users", nullable = false)
    private Integer maxUsers;
    
    @Column(nullable = false)
    private Integer fee;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GatheringStatus status = GatheringStatus.RECRUITMENT;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;
    
    @OneToMany(mappedBy = "gathering", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GatheringUser> users = new HashSet<>();
    
    @OneToOne(mappedBy = "gathering", cascade = CascadeType.ALL, orphanRemoval = true)
    private GatheringLocation location;
    
    /**
     * Gathering 엔티티를 생성하는 빌더 메소드
     * 
     * @param id 모임 ID
     * @param title 모임 제목
     * @param description 모임 설명
     * @param gatheringDate 모임 날짜
     * @param minUsers 최소 참가자 수
     * @param maxUsers 최대 참가자 수
     * @param fee 참가비
     * @param organizer 모임 주최자
     * @param status 모임 상태 (기본값: RECRUITMENT)
     */
    @Builder
    public Gathering(Long id, String title, String description, LocalDateTime gatheringDate, 
                  Integer minUsers, Integer maxUsers, Integer fee, User organizer, GatheringStatus status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.gatheringDate = gatheringDate;
        this.minUsers = minUsers;
        this.maxUsers = maxUsers;
        this.fee = fee;
        this.organizer = organizer;
        this.status = status != null ? status : GatheringStatus.RECRUITMENT;
    }
    
    /**
     * 모임 정보를 업데이트하는 메소드
     * 
     * @param title 새로운 모임 제목
     * @param description 새로운 모임 설명
     * @param gatheringDate 새로운 모임 날짜
     * @param minUsers 새로운 최소 참가자 수
     * @param maxUsers 새로운 최대 참가자 수
     * @param fee 새로운 참가비
     */
    public void updateGathering(String title, String description, LocalDateTime gatheringDate, 
                            Integer minUsers, Integer maxUsers, Integer fee) {
        this.title = title;
        this.description = description;
        this.gatheringDate = gatheringDate;
        this.minUsers = minUsers;
        this.maxUsers = maxUsers;
        this.fee = fee;
    }
    
    /**
     * 모임 상태를 변경하는 메소드
     * 
     * @param status 새로운 모임 상태
     */
    public void updateStatus(GatheringStatus status) {
        this.status = status;
    }

    /**
     * 모임 상태를 취소로 변경하는 메소드
     */
    public void cancel() {
        this.status = GatheringStatus.CANCELLED;
    }

    /**
     * 모임 상태를 삭제로 변경하는 메소드
     */
    public void delete() {
        this.status = GatheringStatus.DELETED;
    }
    
    /**
     * 모임에 새로운 user를 추가하는 메소드 (양방향 관계 설정)
     * 
     * @param gatheringUser 추가할 gathering 정보
     */
    public void addUser(GatheringUser gatheringUser) {
        // 양방향 관계 처리
        if (gatheringUser.getGathering() != this) {
            gatheringUser = GatheringUser.builder()
                    .gathering(this)
                    .user(gatheringUser.getUser())
                    .status(gatheringUser.getStatus())
                    .role(gatheringUser.getRole())
                    .build();
        }
        this.users.add(gatheringUser);
    }
    
    /**
     * 모임에서 user를 제거하는 메소드 (양방향 관계 정리)
     * 
     * @param gatheringUser 제거할 gathering 정보
     */
    public void removeUser(GatheringUser gatheringUser) {
        this.users.remove(gatheringUser);
    }
    
    /**
     * 모임 장소를 설정하는 메소드 (양방향 관계 설정)
     * 
     * @param location 모임 장소
     */
    public void setLocation(GatheringLocation location) {
        // 기존 위치 제거
        if (this.location != null) {
            this.location = null;
        }
        
        // 양방향 관계 처리
        if (location != null && location.getGathering() != this) {
            location = GatheringLocation.builder()
                    .gathering(this)
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .address(location.getAddress())
                    .placeName(location.getPlaceName())
                    .build();
        }
        
        this.location = location;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Gathering gathering = (Gathering) o;
        return Objects.equals(id, gathering.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
} 