package tz.ac.dit.askbot.dto;

import tz.ac.dit.askbot.model.Conversation;
import java.time.Instant;
import java.util.List;

public record ConversationDto(
        Long id, String title, String subject, String language,
        Instant createdAt, List<MessageDto> messages
) {
    public static ConversationDto summary(Conversation c) {
        return new ConversationDto(c.getId(), c.getTitle(), c.getSubject().getLabel(),
                c.getLanguage().name(), c.getCreatedAt(), List.of());
    }

    public static ConversationDto full(Conversation c) {
        return new ConversationDto(c.getId(), c.getTitle(), c.getSubject().getLabel(),
                c.getLanguage().name(), c.getCreatedAt(),
                c.getMessages().stream().map(MessageDto::from).toList());
    }
}
