package footoff.api.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class UserResponseDTO {

    @Getter
    @AllArgsConstructor
    public static class JoinResultDTO {
        private Long userId;
        private String nickname;
        private String role;
    }
} 