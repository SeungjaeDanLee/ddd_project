package footoff.api.domain.user.entity;

import footoff.api.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

/**
 * 사용자 관심사 정보를 담는 엔티티 클래스
 * 사용자 프로필과 연결되어 사용자의 관심사를 관리한다
 */
@Entity
@Table(name = "user_interest")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInterest extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private UserProfile profile;
    
    @Column(name = "interest_name", nullable = false)
    private String interestName;
    
    /**
     * UserInterest 엔티티 생성을 위한 빌더 메서드
     * 
     * @param id 관심사 정보 고유 식별자
     * @param profile 연결된 사용자 프로필
     * @param interestName 관심사 이름
     */
    @Builder
    public UserInterest(Long id, UserProfile profile, String interestName) {
        this.id = id;
        this.profile = profile;
        this.interestName = interestName;
    }
    
    /**
     * 관심사 이름을 업데이트하는 메서드
     * 
     * @param interestName 업데이트할 관심사 이름
     */
    public void updateInterestName(String interestName) {
        this.interestName = interestName;
    }
} 