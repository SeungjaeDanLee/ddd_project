package footoff.api.domain.user.entity;

import java.util.UUID;

import footoff.api.domain.user.dto.UserDto;
import footoff.api.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
	
	@Id
	@Column(columnDefinition = "BINARY(16)")
	private UUID id; 
	
	@Column(nullable = false)
	private String name;
	
	private int age;
	
	@Builder
	public User(UUID id, String name, int age) {
		this.id = id;
		this.name = name;
		this.age = age;
	}

	public UserDto toDto() {
		return UserDto.builder()
            .id(id)
            .name(name)
            .age(age)
            .createDate(getCreatedAt())
            .updateDate(getUpdatedAt())
            .build();
	}
} 