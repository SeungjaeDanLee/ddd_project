package footoff.api.domain.auth.util;

import footoff.api.domain.user.entity.UserEntity;

public class AuthConverter {
	public static UserEntity toUserEntity(String nickname) {
		return UserEntity.builder()
				.kakaoId(null)
				.nickname(nickname)
				.role("ROLE_USER")
				.profileImage(null)
				.build();
	}
}
