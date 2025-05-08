package footoff.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 카카오 API 통신에 사용되는 DTO 클래스
 * 카카오 로그인 과정에서 토큰 및 사용자 프로필 정보를 매핑하는데 사용됩니다.
 */
@Schema(description = "카카오 API 통신 관련 DTO")
public class KakaoDto {
	/**
	 * 카카오 OAuth 토큰 정보 DTO
	 * 카카오 인증 서버에서 받은 토큰 정보를 저장합니다.
	 */
	@Getter
	@Schema(description = "카카오 OAuth 토큰 정보")
	public static class OAuthToken {
		@Schema(description = "액세스 토큰", example = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")
		private String access_token;
		
		@Schema(description = "토큰 타입", example = "bearer")
		private String token_type;
		
		@Schema(description = "리프레시 토큰", example = "yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy")
		private String refresh_token;
		
		@Schema(description = "액세스 토큰 만료 시간(초)", example = "21599")
		private int expires_in;
		
		@Schema(description = "토큰 범위", example = "profile_nickname profile_image")
		private String scope;
		
		@Schema(description = "리프레시 토큰 만료 시간(초)", example = "5183999")
		private int refresh_token_expires_in;
	}

	/**
	 * 카카오 사용자 프로필 정보 DTO
	 * 카카오 API에서 받은 사용자 프로필 정보를 저장합니다.
	 */
	@Getter
	@Schema(description = "카카오 사용자 프로필 정보")
	public static class KakaoProfile {
		@Schema(description = "카카오 사용자 고유 ID", example = "123456789")
		private Long id;
		
		@Schema(description = "앱과 연결된 시간", example = "2023-01-01T12:00:00Z")
		private String connected_at;
		
		@Schema(description = "사용자 기본 정보")
		private Properties properties;
		
		@Schema(description = "카카오 계정 정보")
		private KakaoAccount kakao_account;

		/**
		 * 카카오 사용자 속성 정보 DTO
		 * 사용자의 닉네임, 프로필 이미지 등의 기본 정보를 포함합니다.
		 */
		@Getter
		@Schema(description = "카카오 사용자 속성 정보")
		public static class Properties {
			@Schema(description = "사용자 닉네임", example = "홍길동")
			private String nickname;
			
			@Schema(description = "프로필 이미지 URL", example = "http://example.com/profile.jpg")
			private String profile_image;
			
			@Schema(description = "썸네일 이미지 URL", example = "http://example.com/thumbnail.jpg")
			private String thumbnail_image;
		}

		/**
		 * 카카오 계정 정보 DTO
		 * 카카오 계정과 관련된 다양한 정보(이메일, 프로필, 연령대, 성별 등)를 포함합니다.
		 */
		@Getter
		@Schema(description = "카카오 계정 정보")
		public static class KakaoAccount {
			// Email fields
			@Schema(description = "이메일 정보 보유 여부")
			private Boolean has_email;
			
			@Schema(description = "이메일 정보 제공 동의 필요 여부")
			private Boolean email_needs_agreement;
			
			@Schema(description = "이메일 유효성 여부")
			private Boolean is_email_valid;
			
			@Schema(description = "이메일 인증 여부")
			private Boolean is_email_verified;
			
			@Schema(description = "이메일 주소", example = "user@example.com")
			private String email;
			
			// Profile fields
			@Schema(description = "닉네임 정보 제공 동의 필요 여부")
			private Boolean profile_nickname_needs_agreement;
			
			@Schema(description = "프로필 이미지 정보 제공 동의 필요 여부")
			private Boolean profile_image_needs_agreement;
			
			@Schema(description = "상세 프로필 정보")
			private Profile profile;

			/**
			 * 카카오 프로필 상세 정보 DTO
			 * 프로필 이미지 URL, 닉네임 등의 상세 정보를 포함합니다.
			 */
			@Getter
			@Schema(description = "카카오 프로필 상세 정보")
			public static class Profile {
				@Schema(description = "닉네임", example = "홍길동")
				private String nickname;
				
				@Schema(description = "썸네일 이미지 URL", example = "http://example.com/thumbnail.jpg")
				private String thumbnail_image_url;
				
				@Schema(description = "프로필 이미지 URL", example = "http://example.com/profile.jpg")
				private String profile_image_url;
				
				@Schema(description = "기본 이미지 사용 여부")
				private Boolean is_default_image;
				
				@Schema(description = "기본 닉네임 사용 여부")
				private Boolean is_default_nickname;
			}
			
			// Age range fields
			@Schema(description = "연령대 정보 보유 여부")
			private Boolean has_age_range;
			
			@Schema(description = "연령대 정보 제공 동의 필요 여부")
			private Boolean age_range_needs_agreement;
			
			@Schema(description = "연령대 정보", example = "20~29")
			private String age_range;
			
			// Gender fields
			@Schema(description = "성별 정보 보유 여부")
			private Boolean has_gender;
			
			@Schema(description = "성별 정보 제공 동의 필요 여부")
			private Boolean gender_needs_agreement;
			
			@Schema(description = "성별 정보", example = "female/male")
			private String gender;
		}
	}
}