package com.poly.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poly.config.MoMoConfig;
import com.poly.dto.MoMoPaymentRequest;
import com.poly.dto.MoMoPaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MoMoService {

    private final MoMoConfig moMoConfig;
    private final ObjectMapper objectMapper = new ObjectMapper(); // ✅ THAY ĐỔI

    /**
     * Tạo yêu cầu thanh toán MoMo
     */
    public MoMoPaymentResponse createPayment(String orderId, long amount, String orderInfo) {
        try {
            log.info("=== TẠO THANH TOÁN MOMO ===");
            log.info("Order ID: {}", orderId);
            log.info("Amount: {}", amount);

            String requestId = UUID.randomUUID().toString();
            String extraData = "";
            String requestType = "captureWallet";
            String lang = "vi";

            // Tạo raw signature
            String rawSignature = String.format(
                    "accessKey=%s&amount=%d&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s",
                    moMoConfig.getAccessKey(),
                    amount,
                    extraData,
                    moMoConfig.getIpnUrl(),
                    orderId,
                    orderInfo,
                    moMoConfig.getPartnerCode(),
                    moMoConfig.getRedirectUrl(),
                    requestId,
                    requestType
            );

            log.info("Raw signature: {}", rawSignature);

            // Tạo signature
            String signature = hmacSHA256(rawSignature, moMoConfig.getSecretKey());
            log.info("Signature: {}", signature);

            // Tạo request body
            MoMoPaymentRequest request = new MoMoPaymentRequest(
                    moMoConfig.getPartnerCode(),
                    moMoConfig.getAccessKey(),
                    requestId,
                    String.valueOf(amount),
                    orderId,
                    orderInfo,
                    moMoConfig.getRedirectUrl(),
                    moMoConfig.getIpnUrl(),
                    requestType,
                    extraData,
                    lang,
                    signature
            );

            // ✅ THAY ĐỔI: Dùng ObjectMapper thay vì Gson
            String jsonRequest = objectMapper.writeValueAsString(request);
            log.info("Request JSON: {}", jsonRequest);

            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost post = new HttpPost(moMoConfig.getEndpoint());
                post.setHeader("Content-Type", "application/json");
                post.setEntity(new StringEntity(jsonRequest, StandardCharsets.UTF_8));

                try (CloseableHttpResponse response = client.execute(post)) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    log.info("MoMo Response: {}", responseBody);

                    // ✅ THAY ĐỔI: Dùng ObjectMapper thay vì Gson
                    MoMoPaymentResponse momoResponse = objectMapper.readValue(responseBody, MoMoPaymentResponse.class);

                    if (momoResponse.getResultCode() == 0) {
                        log.info("✅ Tạo thanh toán MoMo thành công!");
                        log.info("Pay URL: {}", momoResponse.getPayUrl());
                    } else {
                        log.error("❌ Lỗi MoMo: {} - {}", momoResponse.getResultCode(), momoResponse.getMessage());
                    }

                    return momoResponse;
                }
            }

        } catch (Exception e) {
            log.error("❌ Lỗi khi tạo thanh toán MoMo", e);
            throw new RuntimeException("Không thể tạo thanh toán MoMo: " + e.getMessage());
        }
    }

    /**
     * Xác thực signature từ MoMo callback
     */
    public boolean verifySignature(String rawData, String signature) {
        try {
            String calculatedSignature = hmacSHA256(rawData, moMoConfig.getSecretKey());
            return calculatedSignature.equals(signature);
        } catch (Exception e) {
            log.error("Lỗi khi verify signature", e);
            return false;
        }
    }

    /**
     * Tạo HMAC SHA256 signature
     */
    private String hmacSHA256(String data, String key) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmac.init(secretKey);
        byte[] hash = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}