package footoff.api.domain.user.entity;

import footoff.api.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

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
    
    @Builder
    public UserInterest(Long id, UserProfile profile, String interestName) {
        this.id = id;
        this.profile = profile;
        this.interestName = interestName;
    }
    
    public void updateInterestName(String interestName) {
        this.interestName = interestName;
    }
} 