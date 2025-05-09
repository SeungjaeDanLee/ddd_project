package footoff.api.domain.gathering.dto;

import java.time.LocalDateTime;

import footoff.api.domain.gathering.entity.GatheringLocation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 모임 장소 정보를 담는 DTO 클래스
 */
@Getter
@Schema(description = "모임 장소 정보")
public class GatheringLocationDto {
    @Schema(description = "장소 정보 고유 식별자", example = "1")
    private final Long id;
    
    @Schema(description = "연결된 모임 ID", example = "10")
    private final Long gatheringId;
    
    @Schema(description = "위도", example = "37.5665", required = true)
    private final Double latitude;
    
    @Schema(description = "경도", example = "126.9780", required = true)
    private final Double longitude;
    
    @Schema(description = "주소", example = "서울특별시 강남구 테헤란로 152", required = true)
    private final String address;
    
    @Schema(description = "장소명", example = "강남역 11번 출구")
    private final String placeName;
    
    @Schema(description = "장소 정보 생성 시간")
    private final LocalDateTime createdAt;
    
    @Schema(description = "장소 정보 마지막 수정 시간")
    private final LocalDateTime updatedAt;
    
    /**
     * GatheringLocationDto 생성자
     * 
     * @param id 장소 정보 ID
     * @param gatheringId 모임 ID
     * @param latitude 위도
     * @param longitude 경도
     * @param address 주소
     * @param placeName 장소명
     * @param createdAt 생성 시간
     * @param updatedAt 수정 시간
     */
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
    
    /**
     * GatheringLocation 엔티티를 GatheringLocationDto로 변환하는 메서드
     * 
     * @param location 변환할 GatheringLocation 엔티티
     * @return 변환된 GatheringLocationDto 객체
     */
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