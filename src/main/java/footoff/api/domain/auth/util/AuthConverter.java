package footoff.api.domain.auth.util;

import footoff.api.domain.user.entity.UserEntity;

public class AuthConverter {
	public static UserEntity toUserEntity(String name) {
		return UserEntity.builder()
				.role("ROLE_USER")
				.nickname(name)
				.build();
	}
}
