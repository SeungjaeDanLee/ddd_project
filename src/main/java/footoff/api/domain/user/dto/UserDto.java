package footoff.api.domain.user.dto;

public class UserDto {
	long id;
	long kakaoId;

	public UserDto(long id, long kakaoId) {
		this.id = id;
		this.kakaoId = kakaoId;
	}
}
