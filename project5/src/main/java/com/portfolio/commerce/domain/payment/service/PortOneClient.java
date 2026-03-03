package com.portfolio.commerce.domain.payment.service;

import com.portfolio.commerce.domain.payment.dto.PortOnePaymentResponse;
import com.portfolio.commerce.global.exception.CustomException;
import com.portfolio.commerce.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PortOneClient {

    private final WebClient webClient;

    @Value("${portone.api-url}")
    private String apiUrl;

    @Value("${portone.imp-key}")
    private String impKey;

    @Value("${portone.imp-secret}")
    private String impSecret;

    // 포트원 액세스 토큰 발급
    private String getAccessToken() {
        try {
            Map<?, ?> response = webClient.post()
                    .uri(apiUrl + "/users/getToken")
                    .bodyValue(Map.of("imp_key", impKey, "imp_secret", impSecret))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null) throw new CustomException(ErrorCode.PORTONE_API_ERROR);

            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) response.get("response");
            return (String) data.get("access_token");
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("포트원 토큰 발급 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.PORTONE_API_ERROR);
        }
    }

    // 결제 정보 단건 조회
    // 클라이언트에서 받은 impUid로 포트원에 실제 결제 정보 조회
    // → 위변조 방지: 클라이언트가 amount를 조작할 수 없음
    public PortOnePaymentResponse.PaymentData getPayment(String impUid) {
        try {
            String token = getAccessToken();
            PortOnePaymentResponse response = webClient.get()
                    .uri(apiUrl + "/payments/" + impUid)
                    .header("Authorization", token)
                    .retrieve()
                    .bodyToMono(PortOnePaymentResponse.class)
                    .block();

            if (response == null || response.getResponse() == null)
                throw new CustomException(ErrorCode.PAYMENT_NOT_FOUND);

            return response.getResponse();
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("포트원 결제 조회 실패 impUid={}: {}", impUid, e.getMessage());
            throw new CustomException(ErrorCode.PORTONE_API_ERROR);
        }
    }
}
