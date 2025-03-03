package com.footoff.api.domain.user.entity;

import com.footoff.api.domain.user.domainObject.User;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "user")
public class UserEntity {
	@Id
	Long id;

	public User toDomainObject() {
		return new User(id);
	}
}
