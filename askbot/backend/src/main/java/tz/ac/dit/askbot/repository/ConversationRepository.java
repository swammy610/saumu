package tz.ac.dit.askbot.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import tz.ac.dit.askbot.model.Conversation;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    List<Conversation> findAllByOrderByIdDesc();

    /** JOIN FETCH avoids LazyInitializationException when we need the messages. */
    @Query("SELECT c FROM Conversation c LEFT JOIN FETCH c.messages WHERE c.id = :id")
    Optional<Conversation> findByIdWithMessages(@Param("id") Long id);
}
