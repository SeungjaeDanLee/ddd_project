package footoff.api.domain.user.service;

import java.util.List;

import footoff.api.domain.user.entity.User;

/**
 * 사용자 관련 서비스 인터페이스
 */
public interface UserService {
    
    /**
     * 모든 사용자 목록 조회
     * @return 사용자 목록
     */
    List<User> getUsers();
} 