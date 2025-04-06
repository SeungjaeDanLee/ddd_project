package footoff.api.domain.user.dto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserDto {
	private final UUID id;
	private final String name;
	private final int age;
	private final Date createDate;
	private final Date updateDate;

	@Builder
	public UserDto(UUID id, String name, int age, LocalDateTime createDate, LocalDateTime updateDate) {
		this.id = id;
		this.name = name;
		this.age = age;
		this.createDate = createDate != null ? Date.from(createDate.atZone(ZoneId.systemDefault()).toInstant()) : null;
		this.updateDate = updateDate != null ? Date.from(updateDate.atZone(ZoneId.systemDefault()).toInstant()) : null;
	}
	
	// Date 타입으로 직접 생성할 수 있는 생성자도 유지
	public UserDto(UUID id, String name, int age, Date createDate, Date updateDate) {
		this.id = id;
		this.name = name;
		this.age = age;
		this.createDate = createDate;
		this.updateDate = updateDate;
	}
}
