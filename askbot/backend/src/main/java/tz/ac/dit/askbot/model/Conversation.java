package tz.ac.dit.askbot.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * OOP - INHERITANCE: extends BaseEntity, inheriting id + createdAt.
 * OOP - ENCAPSULATION: messages list is private; callers must use addMessage().
 * OOP - POLYMORPHISM: overrides describe().
 */
@Entity
@Table(name = "conversations")
public class Conversation extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private Subject subject = Subject.GENERAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Language language = Language.AUTO;

    @OneToMany(mappedBy = "conversation",
               cascade = CascadeType.ALL,
               orphanRemoval = true,
               fetch = FetchType.LAZY)
    @OrderBy("id ASC")
    private List<Message> messages = new ArrayList<>();

    protected Conversation() { }

    public Conversation(String title, Subject subject, Language language) {
        this.title = title;
        this.subject = subject;
        this.language = language;
    }

    /**
     * ENCAPSULATION: the only sanctioned way to attach a message.
     * Keeps BOTH sides of the relationship in sync - a classic JPA bug otherwise.
     */
    public void addMessage(Message message) {
        this.messages.add(message);
        message.setConversation(this);
    }

    @Override
    public String describe() {
        return "Conversation #" + getId() + " [" + subject.getLabel() + "] - " + title;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public Language getLanguage() { return language; }
    public void setLanguage(Language language) { this.language = language; }

    /** Read-only view so nobody bypasses addMessage(). */
    public List<Message> getMessages() { return Collections.unmodifiableList(messages); }
}
