package tz.ac.dit.askbot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import tz.ac.dit.askbot.model.Language;
import tz.ac.dit.askbot.model.Subject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * OOP - POLYMORPHISM + INHERITANCE (implements AnswerEngine).
 * Talks to the Anthropic Messages API over plain java.net.http - no extra SDK.
 * Marked @Primary so Spring injects THIS one by default.
 */
@Service
@Primary
public class ClaudeAnswerEngine implements AnswerEngine {

    private static final Logger log = LoggerFactory.getLogger(ClaudeAnswerEngine.class);
    private static final String ENDPOINT = "https://api.anthropic.com/v1/messages";

    private final String apiKey;
    private final String model;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    public ClaudeAnswerEngine(@Value("${askbot.anthropic.api-key:}") String apiKey,
                              @Value("${askbot.anthropic.model:claude-sonnet-4-6}") String model) {
        this.apiKey = apiKey;
        this.model = model;
    }

    @Override
    public String engineName() { return "Claude (" + model + ")"; }

    @Override
    public String answer(String question, Subject subject, Language language) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                "ANTHROPIC_API_KEY is not set. Set the environment variable and restart.");
        }
        try {
            String body = mapper.writeValueAsString(buildPayload(question, subject, language));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ENDPOINT))
                    .timeout(Duration.ofSeconds(90))
                    .header("Content-Type", "application/json")
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.error("Anthropic API returned {}: {}", response.statusCode(), response.body());
                throw new IllegalStateException("AI service returned HTTP " + response.statusCode());
            }
            return extractText(response.body());

        } catch (com.fasterxml.jackson.core.JsonProcessingException ex) {
            log.error("Failed to build the request payload", ex);
            throw new IllegalStateException("Could not build the AI request.", ex);

        } catch (java.io.IOException ex) {
            log.error("Call to Anthropic API failed", ex);
            throw new IllegalStateException("Could not reach the AI service: " + ex.getMessage(), ex);

        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt(); // restore the flag - never swallow it
            log.error("Call to Anthropic API was interrupted", ex);
            throw new IllegalStateException("The AI request was interrupted.", ex);
        }
    }

    private ObjectNode buildPayload(String question, Subject subject, Language language) {
        ObjectNode payload = mapper.createObjectNode();
        payload.put("model", model);
        payload.put("max_tokens", 2000);
        payload.put("system", buildSystemPrompt(subject, language));

        ObjectNode userMsg = mapper.createObjectNode();
        userMsg.put("role", "user");
        userMsg.put("content", question);

        ArrayNode messages = mapper.createArrayNode();
        messages.add(userMsg);
        payload.set("messages", messages);
        return payload;
    }

    /**
     * The subject enum supplies its own hint, the language enum supplies its own
     * instruction. Adding a new subject needs ZERO changes to this method.
     */
    private String buildSystemPrompt(Subject subject, Language language) {
        return """
               You are AskBot, a knowledgeable tutor built by a Computer Engineering \
               student at Dar es Salaam Institute of Technology (DIT), Tanzania.

               Current subject area: %s (%s).
               Subject guidance: %s
               Language: %s

               Rules:
               - Answer any question from any subject, accurately and completely.
               - Teach, don't just state: give the reasoning so the student learns.
               - Use headings and short paragraphs. Use bullet points only when they help.
               - Use worked examples. Prefer Tanzanian context (TZS, local places) where natural.
               - If you are not certain, say so plainly rather than guessing.
               """.formatted(
                subject.getLabel(),
                subject.getSwahiliLabel(),
                subject.promptHint(),
                language.getInstruction()
        );
    }

    /** The API returns content as an array of blocks; concatenate the text ones. */
    private String extractText(String rawJson) {
        try {
            JsonNode root = mapper.readTree(rawJson);
            JsonNode content = root.path("content");
            StringBuilder sb = new StringBuilder();
            for (JsonNode block : content) {
                if ("text".equals(block.path("type").asText())) {
                    sb.append(block.path("text").asText());
                }
            }
            String result = sb.toString().trim();
            return result.isEmpty() ? "The AI returned an empty answer. Please try rephrasing." : result;
        } catch (Exception ex) {
            log.error("Failed to parse Anthropic response: {}", rawJson, ex);
            throw new IllegalStateException("Could not read the AI response.", ex);
        }
    }
}
