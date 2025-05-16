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

import footoff.api.domain.auth.dto.AppleDto;
import footoff.api.domain.auth.exception.AuthHandler;
import footoff.api.domain.auth.exception.ErrorStatus;

import lombok.extern.slf4j.Slf4j;
import java.util.Arrays;

@Component
@Slf4j
public class AppleUtil {

    @Value("${apple.auth.client-id}")
    private String clientId;
    
    @Value("${apple.auth.team-id}")
    private String teamId;
    
    @Value("${apple.auth.key-id}")
    private String keyId;
    
    @Value("${apple.auth.redirect-uri}")
    private String redirectUri;
    
    @Value("${apple.auth.private-key}")
    private String privateKey;

    public AppleDto.OAuthToken requestToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);
        params.add("client_secret", generateClientSecret());

        HttpEntity<MultiValueMap<String, String>> appleTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://appleid.apple.com/auth/token",
                HttpMethod.POST,
                appleTokenRequest,
                String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        AppleDto.OAuthToken oAuthToken = null;

        try {
            oAuthToken = objectMapper.readValue(response.getBody(), AppleDto.OAuthToken.class);
            log.info("oAuthToken : " + oAuthToken.getAccess_token());
        } catch (JsonProcessingException e) {
            log.error("Failed to parse Apple token response: {}", response.getBody(), e);
            throw new AuthHandler(ErrorStatus._PARSING_ERROR);
        }
        return oAuthToken;
    }

    public AppleDto.AppleProfile requestProfile(String idToken) {
        try {
            // ID 토큰을 디코딩하여 사용자 정보 추출
            String[] parts = idToken.split("\\.");
            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            
            AppleDto.AppleProfile appleProfile = objectMapper.readValue(payload, AppleDto.AppleProfile.class);
            
            if (appleProfile != null) {
                log.info("Successfully parsed Apple profile, sub: {}", appleProfile.getSub());
            } else {
                log.error("Failed to parse Apple profile");
                throw new AuthHandler(ErrorStatus._PARSING_ERROR);
            }
            
            return appleProfile;
        } catch (Exception e) {
            log.error("Error parsing Apple profile: {}", e.getMessage());
            log.error("Stack trace: {}", Arrays.toString(e.getStackTrace()));
            throw new AuthHandler(ErrorStatus._PARSING_ERROR);
        }
    }

    private String generateClientSecret() {
        // TODO: JWT 토큰 생성 로직 구현
        // 1. 현재 시간과 만료 시간 설정
        // 2. JWT 헤더 설정 (alg: ES256, kid: keyId)
        // 3. JWT 클레임 설정 (iss: teamId, iat: 현재시간, exp: 만료시간, aud: https://appleid.apple.com, sub: clientId)
        // 4. ES256 알고리즘으로 서명
        return "dummy_client_secret"; // 임시 반환값
    }
} 