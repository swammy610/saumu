package tz.ac.dit.askbot.dto;

public record AskResponse(
        Long conversationId,
        String question,
        String answer,
        String subject,
        String language
) { }
