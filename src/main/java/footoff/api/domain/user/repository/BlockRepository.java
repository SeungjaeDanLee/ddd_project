package footoff.api.domain.user.repository;

import footoff.api.domain.user.entity.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
    List<Block> findByUserId(UUID userId);
    List<Block> findByBlockedId(UUID blockedId);
    boolean existsByUserIdAndBlockedId(UUID userId, UUID blockedId);
    Optional<Block> findByUserIdAndBlockedId(UUID userId, UUID blockedId);
}
