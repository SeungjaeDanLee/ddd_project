package footoff.api.domain.user.entity;

import footoff.api.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

/**
 * 사용자 차단 정보를 담는 엔티티 클래스
 * 사용자가 다른 사용자를 차단한 정보를 관리한다
 */
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

	@Column(name = "is_block")
	private Boolean isBlock;
    
    /**
     * Block 엔티티 생성을 위한 빌더 메서드
     * 
     * @param id 차단 정보 고유 식별자
     * @param user 차단을 요청한 사용자
     * @param blocked 차단된 사용자
     * @param reason 차단 사유
	 * @param isBlock 차단 여부	
     */
    @Builder
    public Block(Long id, User user, User blocked, String reason, Boolean isBlock) {
        this.id = id;
        this.user = user;
        this.blocked = blocked;
        this.reason = reason;
        this.isBlock = isBlock;
    }
    
    /**
     * 차단 사유를 업데이트하는 메서드
     * 
     * @param reason 업데이트할 차단 사유
     */
    public void updateReason(String reason) {
        this.reason = reason;
    }

	/*
	 * 차단 여부를 업데이트하는 메서드
	 * 
	 * @param isBlock 업데이트할 차단 여부
	 */
	public void updateIsBlock(Boolean isBlock) {
		this.isBlock = isBlock;
	}
} 