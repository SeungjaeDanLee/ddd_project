package footoff.api.domain.user.entity;

import footoff.api.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Entity
@Table(name = "block")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Block extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_id", nullable = false)
    private User blocked;
    
    @Column(columnDefinition = "TEXT")
    private String reason;
    
    @Builder
    public Block(Long id, User user, User blocked, String reason) {
        this.id = id;
        this.user = user;
        this.blocked = blocked;
        this.reason = reason;
    }
    
    public void updateReason(String reason) {
        this.reason = reason;
    }
} 