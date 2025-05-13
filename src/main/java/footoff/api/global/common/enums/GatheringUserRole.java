package footoff.api.global.common.enums;

/**
 * 모임에서 사용자의 역할을 나타내는 열거형
 */
public enum GatheringUserRole {
    /**
     * 모임 주최자: 모임을 생성하고 관리하는 권한을 가진 사용자
     * 참가 신청 승인/거부, 모임 정보 수정, 모임 취소/삭제 등의 권한을 갖음
     */
    ORGANIZER,
    
    /**
     * 참가자: 모임에 참가하는 일반 사용자
     * 모임 기본 정보 조회 및 본인의 참가 상태 변경(취소/탈퇴) 등의 권한을 갖음
     */
    PARTICIPANT
} 