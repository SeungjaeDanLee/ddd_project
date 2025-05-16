package footoff.api.global.common.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class DiscordNotifier {

    @Value("${discord.money-webhook-url}")
    private String moneyWebhookUrl;

    @Value("${discord.server-webhook-url}")
    private String serverWebhookUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendDiscordMoneyMessage(Map<String, String> data) {
        // 닉네임, 모임명, 환불계좌를 순서대로 구성
        String message = String.format(
                """
                💸 환불 요청 도착
                👤 닉네임: %s
                📌 모임명: %s
                💳 환불 계좌: %s
                """,
                data.getOrDefault("nickname", "N/A"),
                data.getOrDefault("meetingName", "N/A"),
                data.getOrDefault("account", "N/A")
        );
        sendDiscordMessage(moneyWebhookUrl, message);
    }

    public void sendDiscordServerErrorMessage(Map<String, String> data) {
        // 향후 서버 에러 메시지 구성 방식 정의 가능
        String message = String.format(
                """
                🚨 서버 오류 발생
                📍 위치: %s
                🔍 메서드: %s
                📝 내용: %s
                """,
                data.getOrDefault("location", "알 수 없음"),
                data.getOrDefault("method", "알 수 없음"),
                data.getOrDefault("details", "내용 없음")
        );
        sendDiscordMessage(serverWebhookUrl, message);
    }

    /**
     * 보안 관련 알림을 디스코드로 전송
     * 의심스러운 IP 접근이나 차단된 IP 등에 대한 정보를 알립니다.
     *
     * @param data 보안 알림 데이터 (IP, 이벤트 수, URI, 상세 정보 등)
     */
    public void sendDiscordSecurityMessage(Map<String, String> data) {
        String message = String.format(
                """
                🛡️ 보안 알림
                🔒 IP 주소: %s
                🔢 의심 이벤트 수: %s
                🔗 마지막 요청 URI: %s
                ℹ️ 상세 정보: %s
                ⏰ 시간: %s
                """,
                data.getOrDefault("ip", "알 수 없음"),
                data.getOrDefault("eventCount", "0"),
                data.getOrDefault("lastUri", "알 수 없음"),
                data.getOrDefault("details", "내용 없음"),
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );

        sendDiscordMessage(serverWebhookUrl, message);
    }

    private void sendDiscordMessage(String webhookUrl, String content) {
        Map<String, String> body = new HashMap<>();
        body.put("content", content);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(webhookUrl, request, String.class);
    }
}
