package footoff.api.domain.user.domainObject;

import footoff.api.domain.user.dto.UserDto;

public class User {
	long id;

	public User(long id) {
		this.id = id;
	}

	public UserDto toDto() {
		return new UserDto(id);
	}
}
