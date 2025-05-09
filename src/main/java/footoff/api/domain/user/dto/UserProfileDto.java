package footoff.api.domain.user.dto;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import footoff.api.domain.user.entity.UserProfile;
import footoff.api.domain.user.entity.UserInterest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 사용자 프로필 정보를 담는 DTO 클래스
 * 사용자의 프로필 이미지, 닉네임, 나이, 성별, MBTI, 위치, 직업, 취미, 관심사 등의 상세 정보를 포함합니다.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "사용자 프로필 정보")
public class UserProfileDto {
	// @Schema(description = "프로필 고유 식별자")
	// private Long id;

	@Schema(description = "사용자 고유 식별자", required = true)
	private UUID userId;

	@Schema(description = "프로필 이미지 URL")
	private String profileImage;

	@Schema(description = "사용자 닉네임", required = true, example = "닉네임")
	private String nickname;

	@Schema(description = "사용자 나이", example = "25")
	private Integer age;

	@Schema(description = "사용자 성별", example = "남성/여성")
	private String gender;

	@Schema(description = "자기소개", example = "안녕하세요. 반갑습니다.")
	private String introduction;

	@Schema(description = "MBTI 성격 유형", example = "ENFP")
	private String mbti;

	@Schema(description = "사용자 위치/지역", example = "서울시 강남구")
	private String location;

	@Schema(description = "사용자 직업", example = "개발자")
	private String job;

	@Schema(description = "사용자 취미", example = "독서, 요리")
	private String hobby;

	@Schema(description = "사용자 관심사 목록", example = "[\"여행\", \"음악\", \"영화\"]")
	private Set<String> interests;

	@Schema(description = "사용자 환불 계좌", example = "950002-00-553328")
	private String account;

	@Schema(description = "사용자 환불 은행", example = "국민은행")
	private String bank;

	@Schema(description = "사용자 환불 예금주명", example = "허세진")
	private String depositorName;

	@Schema(description = "프로필 생성 시간")
	private LocalDateTime createDate;

	@Schema(description = "프로필 수정 시간")
	private LocalDateTime updateDate;
	
	/**
	 * UserProfile 엔티티를 UserProfileDto로 변환하는 메서드
	 * 
	 * @param profile 변환할 UserProfile 엔티티
	 * @return 변환된 UserProfileDto 객체
	 */
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
			.account(profile.getAccount())
			.bank(profile.getBank())
			.depositorName(profile.getDepositorName())
			.createDate(profile.getCreatedAt())
			.updateDate(profile.getUpdatedAt())
			.build();
	}
} 