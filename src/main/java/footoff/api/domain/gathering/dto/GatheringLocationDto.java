package footoff.api.domain.gathering.dto;

import java.time.LocalDateTime;

import footoff.api.domain.gathering.entity.GatheringLocation;
import lombok.Builder;
import lombok.Getter;

@Getter
public class GatheringLocationDto {
    private final Long id;
    private final Long gatheringId;
    private final Double latitude;
    private final Double longitude;
    private final String address;
    private final String placeName;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    
    @Builder
    public GatheringLocationDto(Long id, Long gatheringId, Double latitude, Double longitude, 
                               String address, String placeName,
                               LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.gatheringId = gatheringId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.placeName = placeName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    public static GatheringLocationDto fromEntity(GatheringLocation location) {
        if (location == null) {
            return null;
        }
        
        return GatheringLocationDto.builder()
                .id(location.getId())
                .gatheringId(location.getGathering().getId())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .address(location.getAddress())
                .placeName(location.getPlaceName())
                .createdAt(location.getCreatedAt())
                .updatedAt(location.getUpdatedAt())
                .build();
    }
} 