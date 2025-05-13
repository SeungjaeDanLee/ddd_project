package footoff.api.domain.user.dto;

import java.util.UUID;
import lombok.Data;

/**
 * 사용자 차단 요청을 위한 DTO 클래스
 */
@Data
public class BlockRequestDto {
	private UUID userId;
    private UUID blockedId;
    private String reason;
} 