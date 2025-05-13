package footoff.api.domain.user.service;

import footoff.api.domain.user.entity.Block;
import footoff.api.domain.user.entity.User;
import footoff.api.domain.user.repository.BlockRepository;
import footoff.api.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlockService {
	private final BlockRepository blockRepository;
	private final UserRepository userRepository;

	@Transactional
	public Block createBlock(UUID userId, UUID blockedId, String reason) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("User not found"));
		User blocked = userRepository.findById(blockedId)
			.orElseThrow(() -> new IllegalArgumentException("Blocked user not found"));

		Block block = Block.builder()
			.user(user)
			.blocked(blocked)
			.reason(reason)
			.build();
		blockRepository.save(block);
		return block;
	}

	public Block getBlock(UUID userId, UUID blockedId) {
		return blockRepository.findByUserIdAndBlockedId(userId, blockedId)
			.orElse(null);
	}

	@Transactional
	public void disableBlock(UUID userId, UUID blockedId) {
		Block block = blockRepository.findByUserIdAndBlockedId(userId, blockedId)
			.orElseThrow(() -> new IllegalArgumentException("Block not found"));
		block.updateIsBlock(false);
		blockRepository.save(block);
	}

	@Transactional
	public Block enableBlock(UUID userId, UUID blockedId, String reason) {
		Block block = blockRepository.findByUserIdAndBlockedId(userId, blockedId)
			.orElseGet(() -> createBlock(userId, blockedId, reason));
		block.updateIsBlock(true);
		block.updateReason(reason);
		blockRepository.save(block);

		return block;
	}
}
