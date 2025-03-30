package footoff.api.domain.user.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import footoff.api.domain.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {}
