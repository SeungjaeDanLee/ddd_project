package footoff.api.domain.auth.dto;

import lombok.Getter;

/**
 * 카카오 API 통신에 사용되는 DTO 클래스
 * 카카오 로그인 과정에서 토큰 및 사용자 프로필 정보를 매핑하는데 사용됩니다.
 */
public class KakaoDTO {
	/**
	 * 카카오 OAuth 토큰 정보 DTO
	 * 카카오 인증 서버에서 받은 토큰 정보를 저장합니다.
	 */
	@Getter
	public static class OAuthToken {
		private String access_token;      // 액세스 토큰
		private String token_type;        // 토큰 타입 (bearer)
		private String refresh_token;     // 리프레시 토큰
		private int expires_in;           // 액세스 토큰 만료 시간(초)
		private String scope;             // 토큰 범위
		private int refresh_token_expires_in;  // 리프레시 토큰 만료 시간(초)
	}

	/**
	 * 카카오 사용자 프로필 정보 DTO
	 * 카카오 API에서 받은 사용자 프로필 정보를 저장합니다.
	 */
	@Getter
	public static class KakaoProfile {
		private Long id;                  // 카카오 사용자 고유 ID
		private String connected_at;      // 앱과 연결된 시간
		private Properties properties;    // 사용자 기본 정보
		private KakaoAccount kakao_account; // 카카오 계정 정보

		/**
		 * 카카오 사용자 속성 정보 DTO
		 * 사용자의 닉네임, 프로필 이미지 등의 기본 정보를 포함합니다.
		 */
		@Getter
		public static class Properties {
			private String nickname;          // 사용자 닉네임
			private String profile_image;     // 프로필 이미지 URL
			private String thumbnail_image;   // 썸네일 이미지 URL
		}

		/**
		 * 카카오 계정 정보 DTO
		 * 카카오 계정과 관련된 다양한 정보(이메일, 프로필, 연령대, 성별 등)를 포함합니다.
		 */
		@Getter
		public static class KakaoAccount {
			// Email fields
			private Boolean has_email;            // 이메일 정보 보유 여부
			private Boolean email_needs_agreement; // 이메일 정보 제공 동의 필요 여부
			private Boolean is_email_valid;       // 이메일 유효성 여부
			private Boolean is_email_verified;    // 이메일 인증 여부
			private String email;                 // 이메일 주소
			
			// Profile fields
			private Boolean profile_nickname_needs_agreement; // 닉네임 정보 제공 동의 필요 여부
			private Boolean profile_image_needs_agreement;    // 프로필 이미지 정보 제공 동의 필요 여부
			private Profile profile;                          // 상세 프로필 정보

			/**
			 * 카카오 프로필 상세 정보 DTO
			 * 프로필 이미지 URL, 닉네임 등의 상세 정보를 포함합니다.
			 */
			@Getter
			public static class Profile {
				private String nickname;                // 닉네임
				private String thumbnail_image_url;     // 썸네일 이미지 URL
				private String profile_image_url;       // 프로필 이미지 URL
				private Boolean is_default_image;       // 기본 이미지 사용 여부
				private Boolean is_default_nickname;    // 기본 닉네임 사용 여부
			}
			
			// Age range fields
			private Boolean has_age_range;             // 연령대 정보 보유 여부
			private Boolean age_range_needs_agreement; // 연령대 정보 제공 동의 필요 여부
			private String age_range;                  // 연령대 정보 (예: "20~29")
			
			// Gender fields
			private Boolean has_gender;                // 성별 정보 보유 여부
			private Boolean gender_needs_agreement;    // 성별 정보 제공 동의 필요 여부
			private String gender;                     // 성별 정보 (female/male)
		}
	}
}