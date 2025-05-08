package footoff.api.domain.user.dto;

import java.util.Set;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserRequestDto {
	@Getter
    @AllArgsConstructor
    public static class createUserProfile {
		private final UUID userId;
		private final String profileImage;
		private final String nickname;
		private final Integer age;
		private final String gender;
		private final String introduction;
		private final String mbti;
		private final String location;
		private final String job;
		private final String hobby;
		private final Set<String> interests;
    }
}
