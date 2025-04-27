package footoff.api.domain.user.dto;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import footoff.api.domain.user.entity.UserProfile;
import footoff.api.domain.user.entity.UserInterest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileDto {
	private Long id;
	private UUID userId;
	private String profileImage;
	private String nickname;
	private Integer age;
	private String gender;
	private String introduction;
	private String mbti;
	private String location;
	private String job;
	private String hobby;
	private Set<String> interests;
	private LocalDateTime createDate;
	private LocalDateTime updateDate;
	
	public static UserProfileDto fromEntity(UserProfile profile) {
		Set<String> interestNames = profile.getInterests().stream()
			.map(UserInterest::getInterestName)
			.collect(Collectors.toSet());
			
		return UserProfileDto.builder()
			.userId(profile.getUser().getId())
			.profileImage(profile.getProfileImage())
			.nickname(profile.getNickname())
			.age(profile.getAge())
			.gender(profile.getGender())
			.introduction(profile.getIntroduction())
			.mbti(profile.getMbti())
			.location(profile.getLocation())
			.job(profile.getJob())
			.hobby(profile.getHobby())
			.interests(interestNames)
			.createDate(profile.getCreatedAt())
			.updateDate(profile.getUpdatedAt())
			.build();
	}
} 