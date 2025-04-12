package footoff.api.domain.auth.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import footoff.api.domain.auth.dto.KakaoDTO;
import footoff.api.domain.auth.exception.AuthHandler;
import footoff.api.domain.auth.exception.ErrorStatus;

import lombok.extern.slf4j.Slf4j;
import java.util.Arrays;

@Component
@Slf4j
public class KakaoUtil {

	@Value("${kakao.auth.client}")
	private String client;
	@Value("${kakao.auth.redirect}")
	private String redirect;

	public KakaoDTO.OAuthToken requestToken(String accessCode) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", client);
		params.add("redirect_uri", redirect);
		params.add("code", accessCode);

		HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

		ResponseEntity<String> response = restTemplate.exchange(
				"https://kauth.kakao.com/oauth/token",
				HttpMethod.POST,
				kakaoTokenRequest,
				String.class);

		ObjectMapper objectMapper = new ObjectMapper();

		KakaoDTO.OAuthToken oAuthToken = null;

		try {
			oAuthToken = objectMapper.readValue(response.getBody(), KakaoDTO.OAuthToken.class);
			log.info("oAuthToken : " + oAuthToken.getAccess_token());
		} catch (JsonProcessingException e) {
			log.error("Failed to parse Kakao token response: {}", response.getBody(), e);
			throw new AuthHandler(ErrorStatus._PARSING_ERROR);
		}
		return oAuthToken;
	}

	public KakaoDTO.KakaoProfile requestProfile(KakaoDTO.OAuthToken oAuthToken){
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();

		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		headers.add("Authorization","Bearer "+ oAuthToken.getAccess_token());

		HttpEntity<MultiValueMap<String,String>> kakaoProfileRequest = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange(
				"https://kapi.kakao.com/v2/user/me",
				HttpMethod.GET,
				kakaoProfileRequest,
				String.class);

		ObjectMapper objectMapper = new ObjectMapper();
		// Configure ObjectMapper to ignore unknown properties
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		KakaoDTO.KakaoProfile kakaoProfile = null;

		try {
			String responseBody = response.getBody();
			log.info("Kakao API Response: {}", responseBody);
			
			// First check the response structure
			JsonNode rootNode = objectMapper.readTree(responseBody);
			if (rootNode == null) {
				log.error("Kakao API returned null or invalid JSON");
				throw new AuthHandler(ErrorStatus._PARSING_ERROR);
			}
			
			// Try to parse directly to our DTO
			kakaoProfile = objectMapper.readValue(responseBody, KakaoDTO.KakaoProfile.class);
			
			// Log important info for debugging
			if (kakaoProfile != null) {
				log.info("Successfully parsed Kakao profile, id: {}", kakaoProfile.getId());
				if (kakaoProfile.getKakao_account() != null) {
					log.info("Kakao account exists in profile");
				} else {
					log.warn("No kakao_account field in the Kakao profile");
				}
			} else {
				log.error("Failed to parse Kakao profile, but no exception was thrown");
			}
		} catch (JsonProcessingException e) {
			log.error("Error parsing Kakao profile: {}", e.getMessage());
			log.error("Stack trace: {}", Arrays.toString(e.getStackTrace()));
			throw new AuthHandler(ErrorStatus._PARSING_ERROR);
		} catch (Exception e) {
			log.error("Unexpected error when processing Kakao profile: {}", e.getMessage());
			log.error("Stack trace: {}", Arrays.toString(e.getStackTrace()));
			throw new AuthHandler(ErrorStatus._PARSING_ERROR);
		}

		return kakaoProfile;
	}
}