package tz.ac.dit.askbot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * JAVA BASICS: a record is an immutable data carrier.
 * The compiler generates the constructor, getters, equals, hashCode, toString.
 */
public record AskRequest(
        @NotBlank(message = "Question cannot be empty")
        @Size(max = 4000, message = "Question is too long (max 4000 characters)")
        String question,

        String subject,
        String language,
        Long conversationId
) { }
