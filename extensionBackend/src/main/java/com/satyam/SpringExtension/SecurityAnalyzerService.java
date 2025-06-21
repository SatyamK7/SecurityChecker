package com.satyam.SpringExtension;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class SecurityAnalyzerService {

    public SecurityReport analyze(String url) {
        SecurityReport report = new SecurityReport();
        List<String> warnings = new ArrayList<>();
        int score = 100;

        // === 🔐 CONNECTION SECURITY ===
        boolean isHttps = url.startsWith("https");
        report.setHttps(isHttps);
        if (!isHttps) {
            score -= 30;
            warnings.add("❌ Connection is not secure (missing HTTPS)");
        }

        try {
            Connection.Response response = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .timeout(7000)
                    .ignoreHttpErrors(true)
                    .followRedirects(true)
                    .execute();

            Map<String, String> headers = response.headers();

            // Normalize header keys for easier comparison
            Map<String, String> normalizedHeaders = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            normalizedHeaders.putAll(headers);

            // === 🧱 SECURITY HEADERS CHECK ===
            if (!normalizedHeaders.containsKey("Content-Security-Policy")) {
                score -= 15;
                warnings.add("⚠️ Missing Content-Security-Policy header");
            } else {
                String csp = normalizedHeaders.get("Content-Security-Policy");
                if (!csp.contains("default-src")) {
                    score -= 5;
                    warnings.add("⚠️ CSP present but missing default-src directive");
                }
            }

            if (isHttps) {
                if (!normalizedHeaders.containsKey("Strict-Transport-Security")) {
                    score -= 10;
                    warnings.add("⚠️ Missing HSTS (Strict-Transport-Security) header");
                }
            }

            // === 🔐 PRIVACY & PERMISSIONS HEADERS ===
            if (!normalizedHeaders.containsKey("Permissions-Policy") && !normalizedHeaders.containsKey("Feature-Policy")) {
                score -= 3;
                warnings.add("ℹ️ Permissions-Policy header is missing");
            }

            if (!normalizedHeaders.containsKey("Referrer-Policy")) {
                score -= 3;
                warnings.add("ℹ️ Referrer-Policy header is missing");
            }

            // === 📦 CONTENT-TYPE SAFETY ===
            if (!normalizedHeaders.containsKey("X-Content-Type-Options")) {
                score -= 2;
                warnings.add("ℹ️ Missing X-Content-Type-Options header (prevents MIME sniffing)");
            }

            // === 🪟 UI PROTECTION ===
            if (!normalizedHeaders.containsKey("X-Frame-Options")) {
                score -= 2;
                warnings.add("ℹ️ Missing X-Frame-Options header (clickjacking protection)");
            }

            // === 🧪 LEGACY HEADERS (Low Weight) ===
            if (!normalizedHeaders.containsKey("X-XSS-Protection")) {
                // This is outdated, no points deducted
                warnings.add("✅ X-XSS-Protection not present (OK - deprecated in modern browsers)");
            }

        } catch (IOException e) {
            score -= 40;
            warnings.add("❌ Failed to connect to site: " + e.getMessage());
            report.setHasCsp(false); // fallback
        }

        report.setScore(Math.max(score, 0)); // Ensure no negative scores
        report.setWarnings(warnings);
        return report;
    }
}

////////////////////////////////////// AFTER AI INTEGRATION/////////////////////////////////


//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import reactor.core.publisher.Mono;
//import java.util.Map;
//
//@Service
//public class SecurityAnalyzerService {
//
//    @Value("${mistral.api.key}")
//    private String openAiApiKey;
//
//    @Value("${mistral.api.url}")
//    private String openAiApiUrl;
//
//    private final WebClient webClient;
//
//    public SecurityAnalyzerService(WebClient.Builder webClientBuilder) {
//        this.webClient = webClientBuilder.baseUrl("https://api.mistral.ai").build();
//    }
//
//    public Mono<String> analyzeSecurity(String inputCodeOrText) {
//        String prompt = "Analyze the following input for security vulnerabilities and return a score from 0 (very insecure) to 10 (very secure), along with a brief justification:\n\n" + inputCodeOrText;
//
//        Map<String, Object> requestBody = Map.of(
//                "model", "gpt-4",
//                "messages", new Object[]{
//                        Map.of("role", "user", "content", prompt)
//                },
//                "temperature", 0.5
//        );
//
//        return webClient.post()
//                .uri("/v1/chat/completions")
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + openAiApiKey)
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(requestBody)
//                .retrieve()
//                .bodyToMono(String.class); // You can replace this with a proper DTO if you want to extract just the score
//    }
//}

