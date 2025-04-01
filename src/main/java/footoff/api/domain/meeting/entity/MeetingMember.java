package footoff.api.domain.meeting.entity;

import java.time.LocalDateTime;

import footoff.api.domain.user.entity.User;
import footoff.api.global.common.enums.MemberStatus;
import footoff.api.global.common.enums.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "meeting_member",
       uniqueConstraints = @UniqueConstraint(columnNames = {"meeting_id", "user_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MeetingMember {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;
    
    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * MeetingMembership 엔티티를 생성하는 빌더 메소드
     * 
     * @param meeting 참가할 모임
     * @param user 참가하는 사용자
     * @param status 멤버십 상태 (기본값: PENDING)
     * @param role 멤버십 역할 (기본값: MEMBER)
     * @param joinedAt 참가 시간 (기본값: 현재 시간)
     * @param updatedAt 수정 시간 (기본값: 현재 시간)
     */
    @Builder
    public MeetingMember(Meeting meeting, User user, MemberStatus status,
                         UserRole role, LocalDateTime joinedAt, LocalDateTime updatedAt) {
        this.meeting = meeting;
        this.user = user;
        this.status = status != null ? status : MemberStatus.PENDING;
        this.role = role != null ? role : UserRole.MEMBER;
        this.joinedAt = joinedAt != null ? joinedAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }
    
    /**
     * 멤버십 상태를 승인으로 변경하는 메소드
     */
    public void approve() {
        this.status = MemberStatus.APPROVED;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 멤버십 상태를 거부로 변경하는 메소드
     */
    public void reject() {
        this.status = MemberStatus.REJECTED;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 멤버십 역할을 변경하는 메소드
     * 
     * @param role 새로운 역할
     */
    public void changeRole(UserRole role) {
        this.role = role;
        this.updatedAt = LocalDateTime.now();
    }
} 