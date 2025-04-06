package footoff.api.domain.auth.entity;

import footoff.api.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class UserSocialAccount {
	@Id
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Builder
	public UserSocialAccount(Long id, User user) {
		this.id = id;
		this.user = user;
	}
}