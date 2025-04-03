package footoff.api.domain.gathering.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import footoff.api.domain.user.entity.User;
import footoff.api.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gathering")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Gathering extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private String address;
    
    @Column(name = "application_deadline")
    private LocalDateTime applicationDeadline;
    
    @Column(name = "gathering_date", nullable = false)
    private LocalDateTime gatheringDate;
    
    @ManyToOne
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;
    
    @OneToMany(mappedBy = "gathering", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GatheringUser> users = new HashSet<>();
    
    /**
     * Gathering 엔티티를 생성하는 빌더 메소드
     * 
     * @param id 모임 ID
     * @param title 모임 제목
     * @param description 모임 설명
     * @param address 모임 주소
     * @param applicationDeadline 신청 마감 시간
     * @param gatheringDate 모임 날짜
     * @param organizer 모임 주최자
     */
    @Builder
    public Gathering(Long id, String title, String description, String address, 
                  LocalDateTime applicationDeadline, LocalDateTime gatheringDate, 
                  User organizer) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.address = address;
        this.applicationDeadline = applicationDeadline;
        this.gatheringDate = gatheringDate;
        this.organizer = organizer;
    }
    
    /**
     * 모임 정보를 업데이트하는 메소드
     * 
     * @param title 새로운 모임 제목
     * @param description 새로운 모임 설명
     * @param address 새로운 모임 주소
     * @param applicationDeadline 새로운 신청 마감 시간
     * @param gatheringDate 새로운 모임 날짜
     */
    public void updateGathering(String title, String description, String address, 
                            LocalDateTime applicationDeadline, LocalDateTime gatheringDate) {
        this.title = title;
        this.description = description;
        this.address = address;
        this.applicationDeadline = applicationDeadline;
        this.gatheringDate = gatheringDate;
    }
    
    /**
     * 모임 신청이 마감되었는지 확인하는 메소드
     * 
     * @return 현재 시간이 신청 마감 시간을 지났으면 true, 아니면 false
     */
    public boolean isApplicationClosed() {
        return LocalDateTime.now().isAfter(this.applicationDeadline);
    }
    
    /**
     * 모임에 새로운 user를 추가하는 메소드
     * 
     * @param gatheringUser 추가할 gathering 정보
     */
    public void addUser(GatheringUser gatheringUser) {
        this.users.add(gatheringUser);
    }
    
    /**
     * 모임에서 user를 제거하는 메소드
     * 
     * @param gatheringUser 제거할 gathering 정보
     */
    public void removeUser(GatheringUser gatheringUser) {
        this.users.remove(gatheringUser);
    }
} 