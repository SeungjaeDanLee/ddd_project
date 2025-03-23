package footoff.api.domain.user.domainObject;

import footoff.api.domain.user.dto.UserDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
	long id;
	long kakaoId;
	

	public User(long id, long kakaoId) {
		this.id = id;
		this.kakaoId = kakaoId;
	}

	public UserDto toDto() {
		return new UserDto(id, kakaoId);
	}
}
