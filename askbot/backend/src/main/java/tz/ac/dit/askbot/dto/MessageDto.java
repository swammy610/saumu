package tz.ac.dit.askbot.dto;

import tz.ac.dit.askbot.model.Message;
import java.time.Instant;

public record MessageDto(Long id, String role, String content, Instant createdAt) {
    public static MessageDto from(Message m) {
        return new MessageDto(m.getId(), m.getRole().name(), m.getContent(), m.getCreatedAt());
    }
}
