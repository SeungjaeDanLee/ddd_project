package footoff.api.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import footoff.api.domain.user.dto.UserProfileDto;
import footoff.api.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 사용자 프로필 정보를 담는 엔티티 클래스
 * 사용자의 개인 정보와 프로필 상세 정보를 관리한다
 */
@Entity
@Table(name = "user_profile")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfile extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	@JsonManagedReference
	private User user;

	@Column(name = "profile_image")
	private String profileImage;

	@Column
	private String nickname;

	@Column
	private Integer birthYear;

	@Column
	private String gender;

	@Column(columnDefinition = "TEXT")
	private String introduction;

	@Column
	private String mbti;

	@Column
	private String location;

	@Column
	private String job;

	@Column
	private String hobby;

	@OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<UserInterest> interests = new HashSet<>();

	@Column
	private String account;

	@Column
	private String bank;

	@Column(name = "depositor_name")
	private String depositorName;

	/**
	 * UserProfile 엔티티 생성을 위한 빌더 메서드
	 *
	 * @param id 프로필 고유 식별자
	 * @param user 연결된 사용자
	 * @param profileImage 프로필 이미지 URL
	 * @param nickname 사용자 닉네임
	 * @param birthYear 출생 연도
	 * @param gender 사용자 성별
	 * @param introduction 자기소개
	 * @param mbti 사용자 MBTI 유형
	 * @param location 사용자 위치/지역
	 * @param job 사용자 직업
	 * @param hobby 사용자 취미
	 * @param account 사용자 환불 계좌
	 * @param bank 사용자 환불 은행
	 * @param depositorName 사용자 환불 예금주명
	 */
	@Builder
	public UserProfile(Long id, User user, String profileImage, String nickname,
					  Integer birthYear, String gender, String introduction, String mbti,
					  String location, String job, String hobby, String account, String bank, String depositorName) {
		this.id = id;
		this.user = user;
		this.profileImage = profileImage;
		this.nickname = nickname;
		this.birthYear = birthYear;
		this.gender = gender;
		this.introduction = introduction;
		this.mbti = mbti;
		this.location = location;
		this.job = job;
		this.hobby = hobby;
		this.account = account;
		this.bank = bank;
		this.depositorName = depositorName;
	}

	/**
	 * 프로필 정보를 업데이트하는 메서드
	 *
	 * @param profileImage 업데이트할 프로필 이미지 URL
	 * @param nickname 업데이트할 닉네임
	 * @param birthYear 업데이트할 출생 연도
	 * @param gender 업데이트할 성별
	 * @param introduction 업데이트할 자기소개
	 * @param mbti 업데이트할 MBTI 유형
	 * @param location 업데이트할 위치/지역
	 * @param job 업데이트할 직업
	 * @param hobby 업데이트할 취미
	 * @param account 업데이트할 환불 계좌
	 * @param bank 업데이트할 환불 은행
	 * @param depositorName 업데이트할 환불 예금주명
	 */
	public void updateProfile(String profileImage, String nickname, Integer birthYear,
							String gender, String introduction, String mbti,
							String location, String job, String hobby, String account, String bank, String depositorName) {
		this.profileImage = profileImage;
		this.nickname = nickname;
		this.birthYear = birthYear;
		this.gender = gender;
		this.introduction = introduction;
		this.mbti = mbti;
		this.location = location;
		this.job = job;
		this.hobby = hobby;
		this.account = account;
		this.bank = bank;
		this.depositorName = depositorName;
	}

	/**
	 * 관심사를 추가하는 메서드
	 *
	 * @param interest 추가할 관심사 객체
	 */
	public void addInterest(UserInterest interest) {
		this.interests.add(interest);
	}

	/**
	 * 관심사를 제거하는 메서드
	 *
	 * @param interest 제거할 관심사 객체
	 */
	public void removeInterest(UserInterest interest) {
		this.interests.remove(interest);
	}

	/**
	 * UserProfile 엔티티를 UserProfileDto로 변환하는 메서드
	 * 관심사 이름들을 Set으로 수집하여 DTO에 포함시킨다
	 *
	 * @return 변환된 UserProfileDto 객체
	 */
	public UserProfileDto toDto() {
		Set<String> interestNames = this.interests.stream()
			.map(UserInterest::getInterestName)
			.collect(Collectors.toSet());

		return UserProfileDto.builder()
			.userId(user.getId())
			.profileImage(profileImage)
			.nickname(nickname)
			.birthYear(birthYear)
			.gender(gender)
			.introduction(introduction)
			.mbti(mbti)
			.location(location)
			.job(job)
			.hobby(hobby)
			.interests(interestNames)
			.account(account)
			.bank(bank)
			.depositorName(depositorName)
			.email(user.getEmail())
			.phoneNumber(user.getPhoneNumber())
			.createDate(getCreatedAt())
			.updateDate(getUpdatedAt())
			.build();
	}
} 