package footoff.api.domain.gathering.entity;

import footoff.api.domain.user.entity.User;
import footoff.api.global.common.entity.BaseEntity;
import footoff.api.global.common.enums.UserStatus;
import footoff.api.global.common.enums.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private UserStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;
    
    /**
     * GatheringUser 엔티티를 생성하는 빌더 메소드
     * 
     * @param gathering 참가할 모임
     * @param user 참가하는 사용자
     * @param status gathering 상태 (기본값: PENDING)
     * @param role gathering 역할 (기본값: MEMBER)
     */
    @Builder
    public GatheringUser(Gathering gathering, User user, UserStatus status,
                         UserRole role) {
        this.gathering = gathering;
        this.user = user;
        this.status = status != null ? status : UserStatus.PENDING;
        this.role = role != null ? role : UserRole.MEMBER;
    }
    
    /**
     * gathering 상태를 승인으로 변경하는 메소드
     */
    public void approve() {
        this.status = UserStatus.APPROVED;
    }
    
    /**
     * gathering 상태를 거부로 변경하는 메소드
     */
    public void reject() {
        this.status = UserStatus.REJECTED;
    }
    
    /**
     * gathering 역할을 변경하는 메소드
     * 
     * @param role 새로운 역할
     */
    public void changeRole(UserRole role) {
        this.role = role;
    }
} 