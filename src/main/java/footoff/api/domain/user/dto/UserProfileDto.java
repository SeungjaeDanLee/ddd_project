package footoff.api.domain.user.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import footoff.api.domain.user.entity.UserProfile;
import footoff.api.domain.user.entity.UserInterest;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserProfileDto {
    private final Long id;
    private final UUID userId;
    private final String profileImage;
    private final String nickname;
    private final Integer age;
    private final String gender;
    private final String introduction;
    private final String mbti;
    private final String location;
    private final String job;
    private final String hobby;
    private final Set<String> interests;
    private final LocalDateTime createDate;
    private final LocalDateTime updateDate;

    @Builder
    public UserProfileDto(Long id, UUID userId, String profileImage, String nickname, 
                      Integer age, String gender, String introduction, String mbti, 
                      String location, String job, String hobby, Set<String> interests,
                      LocalDateTime createDate, LocalDateTime updateDate) {
        this.id = id;
        this.userId = userId;
        this.profileImage = profileImage;
        this.nickname = nickname;
        this.age = age;
        this.gender = gender;
        this.introduction = introduction;
        this.mbti = mbti;
        this.location = location;
        this.job = job;
        this.hobby = hobby;
        this.interests = interests;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }
    
    public static UserProfileDto fromEntity(UserProfile profile) {
        Set<String> interestNames = profile.getInterests().stream()
            .map(UserInterest::getInterestName)
            .collect(Collectors.toSet());
            
        return UserProfileDto.builder()
            .id(profile.getId())
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