package footoff.api.domain.user.entity;

import footoff.api.domain.user.domainObject.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "user")
public class UserEntity {
	@Id
	Long id;

	public User toDomainObject() {
		return new User(id);
	}
}
