package footoff.api.global.validator;

import java.time.LocalDateTime;

import footoff.api.domain.gathering.entity.Gathering;
import footoff.api.domain.gathering.entity.GatheringUser;
import footoff.api.domain.user.entity.User;
import footoff.api.global.common.enums.GatheringUserRole;
import footoff.api.global.common.enums.GatheringUserStatus;
import footoff.api.global.exception.InvalidOperationException;

/**
 * 모임 관련 유효성 검증을 위한 유틸리티 클래스
 */
public class GatheringValidator {
    
    /**
     * 모임 참가 가능 여부를 검증
     * 
     * @param gathering 모임
     * @throws InvalidOperationException 참가가 불가능한 경우
     */
    public static void validateJoinGathering(Gathering gathering) {
        // 모임 날짜가 지난 경우
        if (isGatheringDatePassed(gathering)) {
            throw new InvalidOperationException("이미 지난 모임에는 참가할 수 없습니다.");
        }
        
        // 모임 최대 인원에 도달한 경우
        if (isGatheringFull(gathering)) {
            throw new InvalidOperationException("모임 최대 인원에 도달했습니다.");
        }
    }
    
    /**
     * 모임 참가 취소 가능 여부를 검증
     * 
     * @param gathering 모임
     * @param gatheringUser 모임 참가자
     * @throws InvalidOperationException 취소가 불가능한 경우
     */
    public static void validateCancelUser(Gathering gathering, GatheringUser gatheringUser) {
        // 모임 주최자인 경우
        if (GatheringUserRole.ORGANIZER.equals(gatheringUser.getRole())) {
            throw new InvalidOperationException("모임 주최자는 참가를 취소할 수 없습니다.");
        }
        
        // 참가 신청 상태가 PENDING이 아닌 경우
        if (!GatheringUserStatus.PENDING.equals(gatheringUser.getStatus())) {
            throw new InvalidOperationException("대기 중인 참가 신청만 취소할 수 있습니다.");
        }
    }
    
    /**
     * 모임 탈퇴 가능 여부를 검증
     * 
     * @param gathering 모임
     * @param gatheringUser 모임 참가자
     * @throws InvalidOperationException 탈퇴가 불가능한 경우
     */
    public static void validateLeaveGathering(Gathering gathering, GatheringUser gatheringUser) {
        // 모임 주최자인 경우
        if (GatheringUserRole.ORGANIZER.equals(gatheringUser.getRole())) {
            throw new InvalidOperationException("모임 주최자는 모임을 나갈 수 없습니다.");
        }
        
        // 참가 신청 상태가 APPROVED가 아닌 경우
        if (!GatheringUserStatus.APPROVED.equals(gatheringUser.getStatus())) {
            throw new InvalidOperationException("승인된 참가자만 모임에서 나갈 수 있습니다.");
        }
        
        // 모임 날짜가 지난 경우
        if (isGatheringDatePassed(gathering)) {
            throw new InvalidOperationException("이미 진행된 모임에서는 나갈 수 없습니다.");
        }
    }
    
    /**
     * 모임 수정 가능 여부를 검증
     * 
     * @param gathering 모임
     * @param user 사용자
     * @param minUsers 새로운 최소 인원
     * @param maxUsers 새로운 최대 인원
     * @throws InvalidOperationException 수정이 불가능한 경우
     */
    public static void validateUpdateGathering(Gathering gathering, User user, int minUsers, int maxUsers) {
        // 모임 주최자가 아닌 경우
        if (!gathering.getOrganizer().getId().equals(user.getId())) {
            throw new InvalidOperationException("모임 주최자만 모임을 수정할 수 있습니다.");
        }
        
        // 모임 날짜가 지난 경우
        if (isGatheringDatePassed(gathering)) {
            throw new InvalidOperationException("이미 진행된 모임은 수정할 수 없습니다.");
        }
        
        // 승인된 참가자 수 계산
        long approvedCount = getApprovedMemberCount(gathering);
        
        // 승인된 참가자 수가 새로운 최소 인원보다 적은 경우
        if (approvedCount < minUsers) {
            throw new InvalidOperationException("현재 승인된 참가자 수보다 큰 최소 인원으로 설정할 수 없습니다.");
        }
        
        // 승인된 참가자 수가 새로운 최대 인원보다 많은 경우
        if (approvedCount > maxUsers) {
            throw new InvalidOperationException("현재 승인된 참가자 수보다 작은 최대 인원으로 설정할 수 없습니다.");
        }
    }
    
    /**
     * 모임 삭제 가능 여부를 검증
     * 
     * @param gathering 모임
     * @param user 사용자
     * @throws InvalidOperationException 삭제가 불가능한 경우
     */
    public static void validateDeleteGathering(Gathering gathering, User user) {
        // 모임 주최자가 아닌 경우
        if (!gathering.getOrganizer().getId().equals(user.getId())) {
            throw new InvalidOperationException("모임 주최자만 모임을 삭제할 수 있습니다.");
        }
        
        // 모임 날짜가 지난 경우
        if (isGatheringDatePassed(gathering)) {
            throw new InvalidOperationException("이미 진행된 모임은 삭제할 수 없습니다.");
        }
    }
    
    /**
     * 모임 승인 가능 여부를 검증
     * 
     * @param gathering 모임
     * @param gatheringUser 모임 참가자
     * @throws InvalidOperationException 승인이 불가능한 경우
     */
    public static void validateApproveUser(Gathering gathering, GatheringUser gatheringUser) {
        // 이미 승인된 경우
        if (GatheringUserStatus.APPROVED.equals(gatheringUser.getStatus())) {
            throw new InvalidOperationException("이미 승인된 참가 신청입니다.");
        }
        
        // 모임 최대 인원에 도달한 경우
        if (isGatheringFull(gathering)) {
            throw new InvalidOperationException("모임 최대 인원에 도달했습니다.");
        }
    }
    
    /**
     * 모임 거부 가능 여부를 검증
     * 
     * @param gatheringUser 모임 참가자
     * @throws InvalidOperationException 거부가 불가능한 경우
     */
    public static void validateRejectUser(GatheringUser gatheringUser) {
        // 이미 거부된 경우
        if (GatheringUserStatus.REJECTED.equals(gatheringUser.getStatus())) {
            throw new InvalidOperationException("이미 거부된 참가 신청입니다.");
        }
    }
    
    /**
     * 모임 날짜가 지났는지 확인
     * 
     * @param gathering 모임
     * @return 날짜가 지났으면 true, 아니면 false
     */
    public static boolean isGatheringDatePassed(Gathering gathering) {
        return gathering.getGatheringDate().isBefore(LocalDateTime.now());
    }
    
    /**
     * 모임 정원이 찼는지 확인
     * 
     * @param gathering 모임
     * @return 정원이 찼으면 true, 아니면 false
     */
    public static boolean isGatheringFull(Gathering gathering) {
        return getApprovedMemberCount(gathering) >= gathering.getMaxUsers();
    }
    
    /**
     * 모임의 승인된 참가자 수를 반환
     * 
     * @param gathering 모임
     * @return 승인된 참가자 수
     */
    public static long getApprovedMemberCount(Gathering gathering) {
        return gathering.getUsers().stream()
                .filter(gu -> GatheringUserStatus.APPROVED.equals(gu.getStatus()))
                .count();
    }
} 