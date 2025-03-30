package footoff.api.domain.user.service;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;

import footoff.api.domain.user.entity.User;
import footoff.api.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public List<User> getUsers() {
		return userRepository.findAll();
	}
}
