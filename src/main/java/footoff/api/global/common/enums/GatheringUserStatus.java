package footoff.api.global.common.enums;

/**
 * 모임에 참가하는 사용자의 상태를 나타내는 열거형
 */
public enum GatheringUserStatus {
    /**
     * 대기 중: 모임 참가 신청이 제출되어 주최자의 승인을 기다리는 상태
     */
    PENDING,
    
    /**
     * 승인됨: 모임 주최자가 참가 신청을 승인한 상태
     * 모임에 정식으로 참가할 수 있음
     */
    APPROVED,
    
    /**
     * 거부됨: 모임 주최자가 참가 신청을 거부한 상태
     */
    REJECTED,
    
    /**
     * 참가 취소: 사용자가 직접 참가 신청을 취소한 상태
     */
    CANCELLED,
} 