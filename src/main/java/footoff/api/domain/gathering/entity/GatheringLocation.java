package footoff.api.domain.gathering.entity;

import footoff.api.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Entity
@Table(name = "GatheringLocation")
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
    
    public void updateLocation(Double latitude, Double longitude, String address, String placeName) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.placeName = placeName;
    }
} 