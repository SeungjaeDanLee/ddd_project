package footoff.api.domain.user.service;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;

import footoff.api.domain.user.entity.User;
import footoff.api.domain.user.repository.UserRepository;

/**
 * 사용자 서비스 구현체
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Override
	public List<User> getUsers() {
		return userRepository.findAll();
	}
} 