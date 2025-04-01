package footoff.api.domain.meeting.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import footoff.api.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "meeting")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Meeting {
    
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
    
    @Column(name = "meeting_date", nullable = false)
    private LocalDateTime meetingDate;
    
    @ManyToOne
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MeetingMember> memberships = new HashSet<>();
    
    /**
     * Meeting 엔티티를 생성하는 빌더 메소드
     * 
     * @param id 모임 ID
     * @param title 모임 제목
     * @param description 모임 설명
     * @param address 모임 주소
     * @param applicationDeadline 신청 마감 시간
     * @param meetingDate 모임 날짜
     * @param organizer 모임 주최자
     * @param createdAt 생성 시간
     * @param updatedAt 수정 시간
     */
    @Builder
    public Meeting(Long id, String title, String description, String address, 
                  LocalDateTime applicationDeadline, LocalDateTime meetingDate, 
                  User organizer, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.address = address;
        this.applicationDeadline = applicationDeadline;
        this.meetingDate = meetingDate;
        this.organizer = organizer;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }
    
    /**
     * 모임 정보를 업데이트하는 메소드
     * 
     * @param title 새로운 모임 제목
     * @param description 새로운 모임 설명
     * @param address 새로운 모임 주소
     * @param applicationDeadline 새로운 신청 마감 시간
     * @param meetingDate 새로운 모임 날짜
     */
    public void updateMeeting(String title, String description, String address, 
                            LocalDateTime applicationDeadline, LocalDateTime meetingDate) {
        this.title = title;
        this.description = description;
        this.address = address;
        this.applicationDeadline = applicationDeadline;
        this.meetingDate = meetingDate;
        this.updatedAt = LocalDateTime.now();
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
     * 모임에 새로운 멤버를 추가하는 메소드
     * 
     * @param membership 추가할 멤버십 정보
     */
    public void addMember(MeetingMember membership) {
        this.memberships.add(membership);
    }
    
    /**
     * 모임에서 멤버를 제거하는 메소드
     * 
     * @param membership 제거할 멤버십 정보
     */
    public void removeMember(MeetingMember membership) {
        this.memberships.remove(membership);
    }
} 