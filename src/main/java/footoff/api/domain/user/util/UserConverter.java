package footoff.api.domain.user.util;

import footoff.api.domain.user.dto.UserResponseDTO;
import footoff.api.domain.user.entity.UserEntity;

public class UserConverter {

    public static UserResponseDTO.JoinResultDTO toJoinResultDTO(UserEntity userEntity) {
        return new UserResponseDTO.JoinResultDTO(
            userEntity.getId(),
            userEntity.getNickname(),
            userEntity.getRole()
        );
    }
} 