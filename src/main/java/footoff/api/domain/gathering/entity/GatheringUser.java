package footoff.api.domain.gathering.entity;

import footoff.api.domain.user.entity.User;
import footoff.api.global.common.entity.BaseEntity;
import footoff.api.global.common.enums.GatheringUserStatus;
import footoff.api.global.common.enums.GatheringUserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 모임 참가자 정보를 담는 엔티티 클래스
 * 사용자의 모임 참가 상태와 역할을 관리한다
 */
@Entity
@Table(name = "gathering_user",
       uniqueConstraints = @UniqueConstraint(columnNames = {"gathering_id", "user_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GatheringUser extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_id", nullable = false)
    private Gathering gathering;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GatheringUserStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GatheringUserRole role;
    
    /**
     * GatheringUser 엔티티를 생성하는 빌더 메소드
     * 
     * @param gathering 참가할 모임
     * @param user 참가하는 사용자
     * @param status gathering 상태 (기본값: PENDING)
     * @param role gathering 역할 (기본값: PARTICIPANT)
     */
    @Builder
    public GatheringUser(Gathering gathering, User user, GatheringUserStatus status,
                         GatheringUserRole role) {
        this.gathering = gathering;
        this.user = user;
        this.status = status != null ? status : GatheringUserStatus.PENDING;
        this.role = role != null ? role : GatheringUserRole.PARTICIPANT;
    }
    
    /**
     * gathering 상태를 승인으로 변경하는 메소드
     */
    public void approve() {
        this.status = GatheringUserStatus.APPROVED;
    }
    
    /**
     * gathering 상태를 거부로 변경하는 메소드
     */
    public void reject() {
        this.status = GatheringUserStatus.REJECTED;
    }

    /**
     * gathering 상태를 취소로 변경하는 메소드
     */
    public void cancel() {
        this.status = GatheringUserStatus.CANCELLED;
    }
    
    /**
     * gathering 역할을 변경하는 메소드
     * 
     * @param role 새로운 역할
     */
    public void changeRole(GatheringUserRole role) {
        this.role = role;
    }
} 