package footoff.api.domain.user.entity;

import java.util.Date;
import java.util.UUID;

import footoff.api.domain.user.dto.UserDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
	
	@Id
	@Column(columnDefinition = "BINARY(16)")
	private UUID id; 
	
	@Column(nullable = false)
	private String name;
	
	private int age;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;
	
	@Builder
	public User(UUID id, String name, int age, Date createDate, Date updateDate) {
		this.id = id;
		this.name = name;
		this.age = age;
		this.createDate = createDate;
		this.updateDate = updateDate;
	}

	public UserDto toDto() {
		return new UserDto(id, name, age, createDate, updateDate);
	}
	
} 