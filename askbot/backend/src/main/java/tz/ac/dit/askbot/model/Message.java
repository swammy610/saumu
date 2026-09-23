package tz.ac.dit.askbot.model;

import jakarta.persistence.*;

/**
 * OOP - INHERITANCE + POLYMORPHISM: another BaseEntity child with its own describe().
 */
@Entity
@Table(name = "messages")
public class Message extends BaseEntity {

    public enum Role { USER, BOT }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Role role;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    protected Message() { }

    public Message(Role role, String content) {
        this.role = role;
        this.content = content;
    }

    @Override
    public String describe() {
        String preview = content.length() > 40 ? content.substring(0, 40) + "..." : content;
        return role + ": " + preview;
    }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Conversation getConversation() { return conversation; }
    public void setConversation(Conversation conversation) { this.conversation = conversation; }
}
