package footoff.api.domain.user.service;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import footoff.api.domain.user.domainObject.User;
import footoff.api.domain.user.entity.UserEntity;
import footoff.api.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public List<User> getUsers() {
		return userRepository.findAll().stream().map(UserEntity::toDomainObject).collect(Collectors.toList());
	}
}
