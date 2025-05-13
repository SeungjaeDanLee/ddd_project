package footoff.api.domain.user.dto;

import java.util.UUID;
import lombok.Data;
import footoff.api.domain.user.entity.Block;

/**
 * 사용자 차단 정보를 위한 DTO 클래스
 */
@Data
public class BlockResponseDto {
    private Long id;
    private UUID userId;
    private UUID blockedId;
    private String reason;
    private Boolean isBlock;

    public static BlockResponseDto fromEntity(Block block) {
        BlockResponseDto dto = new BlockResponseDto();
        dto.id = block.getId();
        dto.userId = block.getUser().getId();
        dto.blockedId = block.getBlocked().getId();
        dto.reason = block.getReason();
        dto.isBlock = block.getIsBlock();
        return dto;
    }
} 