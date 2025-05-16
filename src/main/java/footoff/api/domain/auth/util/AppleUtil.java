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
import java.util.HashMap;
import java.util.Map;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

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
        log.info("애플 토큰 요청 시작: code={}", code);
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
        log.info("애플 토큰 요청 파라미터: {}", params);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://appleid.apple.com/auth/token",
                    HttpMethod.POST,
                    appleTokenRequest,
                    String.class);
            
            log.info("애플 토큰 응답 상태: {}", response.getStatusCode());
            log.info("애플 토큰 응답 본문: {}", response.getBody());

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            AppleDto.OAuthToken oAuthToken = objectMapper.readValue(response.getBody(), AppleDto.OAuthToken.class);
            log.info("애플 토큰 파싱 성공: access_token={}", oAuthToken.getAccess_token());
            return oAuthToken;
        } catch (Exception e) {
            log.error("애플 토큰 요청 실패: {}", e.getMessage(), e);
            throw new AuthHandler(ErrorStatus._PARSING_ERROR);
        }
    }

    public AppleDto.AppleProfile requestProfile(String idToken) {
        log.info("애플 프로필 요청 시작: idToken={}", idToken);
        try {
            String[] parts = idToken.split("\\.");
            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            log.info("애플 프로필 페이로드: {}", payload);
            
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            
            AppleDto.AppleProfile appleProfile = objectMapper.readValue(payload, AppleDto.AppleProfile.class);
            
            if (appleProfile != null) {
                log.info("애플 프로필 파싱 성공: sub={}", appleProfile.getSub());
            } else {
                log.error("애플 프로필 파싱 실패: null 반환");
                throw new AuthHandler(ErrorStatus._PARSING_ERROR);
            }
            
            return appleProfile;
        } catch (Exception e) {
            log.error("애플 프로필 파싱 실패: {}", e.getMessage(), e);
            throw new AuthHandler(ErrorStatus._PARSING_ERROR);
        }
    }

    private String generateClientSecret() {
        try {
            // 현재 시간과 만료 시간 설정
            long now = System.currentTimeMillis() / 1000;
            long exp = now + 3600; // 1시간 후 만료

            // JWT 헤더 생성
            Map<String, Object> header = new HashMap<>();
            header.put("kid", keyId);
            header.put("alg", "ES256");

            // JWT 클레임 생성
            Map<String, Object> claims = new HashMap<>();
            claims.put("iss", teamId);
            claims.put("iat", now);
            claims.put("exp", exp);
            claims.put("aud", "https://appleid.apple.com");
            claims.put("sub", clientId);

            // Private Key 파싱
            String privateKeyPEM = privateKey
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "")
                .replaceAll("\\n", "")
                .replaceAll("\\r", "");

            log.info("Private Key PEM: {}", privateKeyPEM);

            byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            PrivateKey key = keyFactory.generatePrivate(keySpec);

            // JWT 토큰 생성
            String token = Jwts.builder()
                    .setHeader(header)
                    .setClaims(claims)
                    .signWith(SignatureAlgorithm.ES256, key)
                    .compact();

            log.info("클라이언트 시크릿 생성 성공");
            return token;
        } catch (Exception e) {
            log.error("클라이언트 시크릿 생성 실패: {}", e.getMessage(), e);
            throw new AuthHandler(ErrorStatus._PARSING_ERROR);
        }
    }
} 