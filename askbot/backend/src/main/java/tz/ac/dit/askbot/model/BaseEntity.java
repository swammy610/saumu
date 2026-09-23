package tz.ac.dit.askbot.model;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * OOP - ABSTRACTION + INHERITANCE.
 * Abstract superclass holding fields every table needs.
 * You can never write "new BaseEntity()" - it exists only to be extended.
 * @MappedSuperclass tells JPA: copy these columns into every child table.
 */
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    /** ABSTRACT METHOD: every child MUST implement this. */
    public abstract String describe();
}
