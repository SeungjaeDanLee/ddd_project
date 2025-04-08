package footoff.api.domain.user.entity;

import footoff.api.domain.user.dto.UserProfileDto;
import footoff.api.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "UserProfile")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfile extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @Column(name = "profile_image")
    private String profileImage;
    
    @Column
    private String nickname;
    
    @Column
    private Integer age;
    
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
    
    @Builder
    public UserProfile(Long id, User user, String profileImage, String nickname, 
                      Integer age, String gender, String introduction, String mbti, 
                      String location, String job, String hobby) {
        this.id = id;
        this.user = user;
        this.profileImage = profileImage;
        this.nickname = nickname;
        this.age = age;
        this.gender = gender;
        this.introduction = introduction;
        this.mbti = mbti;
        this.location = location;
        this.job = job;
        this.hobby = hobby;
    }
    
    public void updateProfile(String profileImage, String nickname, Integer age, 
                            String gender, String introduction, String mbti, 
                            String location, String job, String hobby) {
        this.profileImage = profileImage;
        this.nickname = nickname;
        this.age = age;
        this.gender = gender;
        this.introduction = introduction;
        this.mbti = mbti;
        this.location = location;
        this.job = job;
        this.hobby = hobby;
    }
    
    public void addInterest(UserInterest interest) {
        this.interests.add(interest);
    }
    
    public void removeInterest(UserInterest interest) {
        this.interests.remove(interest);
    }
    
    public UserProfileDto toDto() {
        Set<String> interestNames = this.interests.stream()
            .map(UserInterest::getInterestName)
            .collect(Collectors.toSet());
            
        return UserProfileDto.builder()
            .id(id)
            .userId(user.getId())
            .profileImage(profileImage)
            .nickname(nickname)
            .age(age)
            .gender(gender)
            .introduction(introduction)
            .mbti(mbti)
            .location(location)
            .job(job)
            .hobby(hobby)
            .interests(interestNames)
            .createDate(getCreatedAt())
            .updateDate(getUpdatedAt())
            .build();
    }
} 