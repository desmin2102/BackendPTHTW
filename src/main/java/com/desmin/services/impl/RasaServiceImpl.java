package com.desmin.services.impl;

import com.desmin.services.RasaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RasaServiceImpl implements RasaService {

    private static final Logger logger = LoggerFactory.getLogger(RasaServiceImpl.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String rasaUrl;

    @Autowired
    public RasaServiceImpl(
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            @Value("${rasa.webhook.url:http://localhost:5005/webhooks/rest/webhook}") String rasaUrl) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.rasaUrl = rasaUrl;
    }

    @Override
    public String getRasaResponse(String userMessage, String senderId) {
        try {
            // Tạo payload
            String payload = String.format("{\"sender\": \"%s\", \"message\": \"%s\"}", senderId, userMessage);

            // Tạo headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Tạo request entity
            HttpEntity<String> request = new HttpEntity<>(payload, headers);

            // Gửi request
            String response = restTemplate.postForObject(rasaUrl, request, String.class);
            if (response == null) {
                logger.error("Rasa response is null");
                return "Xin lỗi, không nhận được phản hồi từ bot.";
            }

            // Parse JSON response
            JsonNode jsonResponse = objectMapper.readTree(response);
            if (!jsonResponse.isArray() || jsonResponse.size() == 0) {
                logger.warn("Rasa response is empty or not an array: {}", response);
                return "Không có hoạt động nào được gợi ý.";
            }

            // Nối tất cả các text từ response
            StringBuilder result = new StringBuilder();
            for (JsonNode node : jsonResponse) {
                if (node.has("text")) {
                    result.append(node.get("text").asText()).append("\n");
                }
            }

            if (result.length() == 0) {
                logger.warn("No text field found in Rasa response: {}", response);
                return "Không có hoạt động nào được gợi ý.";
            }

            return result.toString().trim();
        } catch (Exception e) {
            logger.error("Error processing Rasa request for sender {}: {}", senderId, e.getMessage(), e);
            return "Xin lỗi, tôi gặp lỗi khi xử lý yêu cầu của bạn: " + e.getMessage();
        }
    }
}