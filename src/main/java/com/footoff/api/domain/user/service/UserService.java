package com.footoff.api.domain.user.service;

import com.footoff.api.domain.user.domainObject.User;
import com.footoff.api.domain.user.entity.UserEntity;
import com.footoff.api.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public List<User> getUsers() {
		return userRepository.findAll().stream().map(UserEntity::toDomainObject).collect(Collectors.toList());
	}
}
