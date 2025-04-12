package footoff.api.domain.gathering.entity;

import footoff.api.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

/**
 * 모임 장소 정보를 담는 엔티티 클래스
 * 모임의 위치 정보(위도, 경도, 주소 등)를 관리한다
 */
@Entity
@Table(name = "gathering_location")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GatheringLocation extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_id", nullable = false)
    private Gathering gathering;
    
    @Column
    private Double latitude;
    
    @Column
    private Double longitude;
    
    @Column
    private String address;
    
    @Column(name = "place_name")
    private String placeName;
    
    /**
     * GatheringLocation 엔티티를 생성하는 빌더 메소드
     * 
     * @param id 장소 ID
     * @param gathering 연결된 모임
     * @param latitude 위도 좌표
     * @param longitude 경도 좌표
     * @param address 상세 주소
     * @param placeName 장소명
     */
    @Builder
    public GatheringLocation(Long id, Gathering gathering, Double latitude, 
                           Double longitude, String address, String placeName) {
        this.id = id;
        this.gathering = gathering;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.placeName = placeName;
    }
    
    /**
     * 모임 장소 정보를 업데이트하는 메소드
     * 
     * @param latitude 새로운 위도 좌표
     * @param longitude 새로운 경도 좌표
     * @param address 새로운 상세 주소
     * @param placeName 새로운 장소명
     */
    public void updateLocation(Double latitude, Double longitude, String address, String placeName) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.placeName = placeName;
    }
} 