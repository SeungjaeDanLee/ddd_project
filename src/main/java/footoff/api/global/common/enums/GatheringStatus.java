package footoff.api.global.common.enums;

/**
 * 모임의 상태
 */
public enum GatheringStatus {
    RECRUITMENT, // 모집중
    EXPIRATION,  // 만료
    /**
     * 모임 삭제 전 모임원이 있을 경우
     */
    CANCELLED    // 삭제
}
