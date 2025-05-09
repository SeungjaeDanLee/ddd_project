package footoff.api.domain.user.service;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import footoff.api.domain.user.dto.UserProfileDto;
import footoff.api.domain.user.entity.User;
import footoff.api.domain.user.entity.UserProfile;
import footoff.api.domain.user.entity.UserInterest;
import footoff.api.domain.user.repository.UserProfileRepository;
import footoff.api.domain.user.repository.UserRepository;
import footoff.api.global.exception.EntityNotFoundException;

/**
 * 사용자 서비스 구현체
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final UserProfileRepository userProfileRepository;
	/**
	 * 모든 사용자 목록을 조회하는 메서드
	 * 
	 * @return 전체 사용자 목록
	 */
	@Override
	public List<User> getUsers() {
		return userRepository.findAll();
	}

	@Override
	public UserProfileDto createUserProfile(UserProfileDto userProfileDto) {
		UserProfile userProfile = UserProfile.builder()
			.user(userRepository.findById(userProfileDto.getUserId()).orElseThrow(() -> new EntityNotFoundException("User not found")))
			.profileImage(userProfileDto.getProfileImage())
			.nickname(userProfileDto.getNickname())
			.age(userProfileDto.getAge())
			.gender(userProfileDto.getGender())
			.introduction(userProfileDto.getIntroduction())
			.mbti(userProfileDto.getMbti())
			.location(userProfileDto.getLocation())
			.job(userProfileDto.getJob())
			.hobby(userProfileDto.getHobby())
			.build();

		for (String interestName : userProfileDto.getInterests()) {
			UserInterest interest = UserInterest.builder()
				.profile(userProfile)
				.interestName(interestName)
				.build();
			userProfile.addInterest(interest);
		}

		userProfileRepository.save(userProfile);
		
		return userProfile.toDto();
	}

	@Override
	public UserProfileDto getUserProfile(UUID userId) {
		return userProfileRepository.findByUserId(userId)
			.map(UserProfile::toDto)
			.orElse(null);
	}

	@Override
	public UserProfileDto updateUserProfile(UUID userId, UserProfileDto userProfileDto) {
		UserProfile userProfile = userProfileRepository.findByUserId(userId).orElseThrow(() -> new EntityNotFoundException("User profile not found"));
		
		// 기본 프로필 정보 업데이트
		userProfile.updateProfile(
			userProfileDto.getProfileImage(),
			userProfileDto.getNickname(),
			userProfileDto.getAge(),
			userProfileDto.getGender(),
			userProfileDto.getIntroduction(),
			userProfileDto.getMbti(),
			userProfileDto.getLocation(),
			userProfileDto.getJob(),
			userProfileDto.getHobby()
		);
		
		// 기존 관심사 제거 후 새 관심사 추가
		if (userProfileDto.getInterests() != null) {
			// 기존 관심사 모두 제거
			userProfile.getInterests().clear();
			
			// 새 관심사 추가
			userProfileDto.getInterests().forEach(interestName -> {
				UserInterest interest = UserInterest.builder()
					.profile(userProfile)
					.interestName(interestName)
					.build();
				userProfile.addInterest(interest);
			});
		}
		
		userProfileRepository.save(userProfile);
		return userProfile.toDto();
	}

	@Override
	public void deleteUserProfile(UUID userId) {
		userProfileRepository.deleteByUserId(userId);
	}
	
	
} 