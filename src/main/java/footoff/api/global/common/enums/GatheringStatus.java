package footoff.api.global.common.enums;

/**
 * 모임의 상태를 나타내는 열거형
 */
public enum GatheringStatus {
    /**
     * 모집중: 모임 참가자를 모집하는 상태
     */
    RECRUITMENT, 
    
    /**
     * 만료: 모임 날짜가 지나 더 이상 활성화되지 않은 상태
     */
    EXPIRATION,  
    
    /**
     * 삭제: 모임이 주최자에 의해 삭제된 상태
     * 모임원이 있는 경우도 해당될 수 있음
     */
    DELETED,     
    
    /**
     * 취소: 모임이 주최자에 의해 취소된 상태
     * 모임은 여전히 기록으로 남아있지만 더 이상 참가할 수 없음
     */
    CANCELLED    
}
