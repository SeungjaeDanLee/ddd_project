package footoff.api.domain.user.service;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import footoff.api.domain.user.entity.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
	Optional<UserProfile> findByUserId(UUID userId);
	void deleteByUserId(UUID userId);

}
