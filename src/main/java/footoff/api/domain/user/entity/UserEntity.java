package footoff.api.domain.user.entity;

import footoff.api.domain.user.domainObject.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true)
	private Long kakaoId;
	
	@Column(nullable = false)
	private String nickname;
	
	private String role;
	
	private String profileImage;
	
	@Builder
	public UserEntity(Long kakaoId, String nickname, String role, String profileImage) {
		this.kakaoId = kakaoId;
		this.nickname = nickname;
		this.role = role;
		this.profileImage = profileImage;
	}
	
	public void update(String nickname, String profileImage) {
		this.nickname = nickname;
		this.profileImage = profileImage;
	}

	public User toDomainObject() {
		return new User(id, kakaoId);
	}
} 