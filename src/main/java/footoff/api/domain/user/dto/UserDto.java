package footoff.api.domain.user.dto;

import java.util.Date;
import java.util.UUID;

public class UserDto {
	UUID id;
	String name;
	int age;
	Date createDate;
	Date updateDate;

	public UserDto(UUID id, String name, int age, Date createDate, Date updateDate) {
		this.id = id;
		this.name = name;
		this.age = age;
		this.createDate = createDate;
		this.updateDate = updateDate;
	}
}
