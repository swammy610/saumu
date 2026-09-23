package tz.ac.dit.askbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tz.ac.dit.askbot.model.Message;

public interface MessageRepository extends JpaRepository<Message, Long> { }
