package com.placementpro.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.placementpro.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;
import java.util.Map;

@Service
public class Judge0Service {
    private final RestTemplate restTemplate;

    @Value("${app.code-execution.enabled:false}") private boolean enabled;
    @Value("${judge0.api-url:}") private String apiUrl;
    @Value("${judge0.api-key:}") private String apiKey;
    @Value("${judge0.poll-interval-ms:750}") private long pollIntervalMs;
    @Value("${judge0.max-poll-attempts:20}") private int maxPollAttempts;
    @Value("${judge0.cpu-time-limit:2}") private double cpuTimeLimit;
    @Value("${judge0.wall-time-limit:5}") private double wallTimeLimit;
    @Value("${judge0.memory-limit:128000}") private int memoryLimit;

    private static final Map<String, Integer> LANGUAGE_IDS = Map.of(
            "javascript", 63, "python", 71, "java", 62, "cpp", 54, "c", 50);

    public Judge0Service(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ExecutionResult execute(String language, String sourceCode, String stdin, String expectedOutput) {
        String normalizedLanguage = language == null ? "javascript" : language.toLowerCase(Locale.ROOT);
        Integer languageId = LANGUAGE_IDS.get(normalizedLanguage);
        if (languageId == null) throw new BadRequestException("Unsupported execution language: " + normalizedLanguage);
        if (sourceCode == null || sourceCode.isBlank()) throw new BadRequestException("Source code is required");
        if (sourceCode.length() > 50_000) throw new BadRequestException("Source code exceeds the 50 KB limit");
        if (!enabled || apiUrl == null || apiUrl.isBlank()) {
            throw new BadRequestException("Code execution is not configured. Set CODE_EXECUTION_ENABLED=true and the Judge0 environment variables.");
        }

        String base = apiUrl.replaceAll("/+$", "");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (apiKey != null && !apiKey.isBlank()) headers.set("X-Auth-Token", apiKey);
        Map<String, Object> payload = new java.util.LinkedHashMap<>();
        payload.put("source_code", sourceCode);
        payload.put("language_id", languageId);
        payload.put("stdin", stdin == null ? "" : stdin);
        if (expectedOutput != null) payload.put("expected_output", expectedOutput);
        payload.put("cpu_time_limit", cpuTimeLimit);
        payload.put("wall_time_limit", wallTimeLimit);
        payload.put("memory_limit", memoryLimit);

        try {
            ResponseEntity<JsonNode> created = restTemplate.exchange(
                    base + "/submissions?base64_encoded=false&wait=false", HttpMethod.POST,
                    new HttpEntity<>(payload, headers), JsonNode.class);
            JsonNode tokenNode = created.getBody() == null ? null : created.getBody().get("token");
            if (tokenNode == null || tokenNode.asText().isBlank()) throw new BadRequestException("Judge0 did not return a submission token");
            String token = tokenNode.asText();
            for (int attempt = 0; attempt < Math.max(1, maxPollAttempts); attempt++) {
                if (attempt > 0) Thread.sleep(Math.max(100, Math.min(pollIntervalMs, 5000)));
                ResponseEntity<JsonNode> polled = restTemplate.exchange(
                        base + "/submissions/" + token + "?base64_encoded=false", HttpMethod.GET,
                        new HttpEntity<>(headers), JsonNode.class);
                JsonNode body = polled.getBody();
                int statusId = body != null && body.has("status") ? body.get("status").path("id").asInt(0) : 0;
                if (statusId > 2) return ExecutionResult.from(body);
            }
            return new ExecutionResult("Time Limit Exceeded", 5, null, null, "Judge0 polling timed out", null, null);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new BadRequestException("Code execution was interrupted");
        } catch (RestClientException ex) {
            throw new BadRequestException("Judge0 is unavailable: " + ex.getMessage());
        }
    }

    public record ExecutionResult(String status, int statusId, String stdout, String stderr,
                                  String compileOutput, String time, Long memory) {
        static ExecutionResult from(JsonNode node) {
            int id = node.path("status").path("id").asInt(0);
            String status = node.path("status").path("description").asText("Unknown");
            return new ExecutionResult(status, id, text(node, "stdout"), text(node, "stderr"),
                    text(node, "compile_output"), text(node, "time"), node.has("memory") ? node.get("memory").asLong() : null);
        }
        private static String text(JsonNode node, String name) { return node.hasNonNull(name) ? node.get(name).asText() : null; }
    }
}
