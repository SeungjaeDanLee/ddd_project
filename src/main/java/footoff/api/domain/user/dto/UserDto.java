package footoff.api.domain.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import footoff.api.global.common.enums.Language;
import footoff.api.global.common.enums.UserActivityStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserDto {
	private final UUID id;
	private final String phoneNumber;
	private final String email;
	private final UserActivityStatus status;
	private final Language language;
	private final boolean isVerified;
	private final LocalDateTime lastLoginAt;
	private final LocalDateTime createDate;
	private final LocalDateTime updateDate;

	@Builder
	public UserDto(UUID id, String phoneNumber, String email, UserActivityStatus status, 
	               Language language, boolean isVerified, LocalDateTime lastLoginAt, 
	               LocalDateTime createDate, LocalDateTime updateDate) {
		this.id = id;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.status = status;
		this.language = language;
		this.isVerified = isVerified;
		this.lastLoginAt = lastLoginAt;
		this.createDate = createDate;
		this.updateDate = updateDate;
	}
}
