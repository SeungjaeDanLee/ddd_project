package footoff.api.domain.user.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import footoff.api.domain.user.dto.UserDto;
import footoff.api.global.common.entity.BaseEntity;
import footoff.api.global.common.enums.Language;
import footoff.api.global.common.enums.UserActivityStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Entity
@Table(name = "User")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
	
	@Id
	@Column(columnDefinition = "BINARY(16)")
	private UUID id; 
	
	@Column(name = "phone_number")
	private String phoneNumber;
	
	@Column
	private String email;
	
	@Enumerated(EnumType.STRING)
	@Column
	private UserActivityStatus status = UserActivityStatus.ACTIVE;
	
	@Enumerated(EnumType.STRING)
	@Column
	private Language language = Language.KO;
	
	@Column(name = "is_verified")
	private boolean isVerified = false;
	
	@Column(name = "last_login_at")
	private LocalDateTime lastLoginAt;
	
	@Builder
	public User(UUID id, String phoneNumber, String email, UserActivityStatus status, 
	            Language language, boolean isVerified, LocalDateTime lastLoginAt) {
		this.id = id;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.status = status != null ? status : UserActivityStatus.ACTIVE;
		this.language = language != null ? language : Language.KO;
		this.isVerified = isVerified;
		this.lastLoginAt = lastLoginAt != null ? lastLoginAt : LocalDateTime.now();
	}

	public UserDto toDto() {
		return UserDto.builder()
            .id(id)
            .phoneNumber(phoneNumber)
            .email(email)
            .status(status)
            .language(language)
            .isVerified(isVerified)
            .lastLoginAt(lastLoginAt)
            .createDate(getCreatedAt())
            .updateDate(getUpdatedAt())
            .build();
	}
	
	public void updateLastLoginAt() {
		this.lastLoginAt = LocalDateTime.now();
	}
	
	public void updateStatus(UserActivityStatus status) {
		this.status = status;
	}
	
	public void updateLanguage(Language language) {
		this.language = language;
	}
	
	public void verify() {
		this.isVerified = true;
	}
} 