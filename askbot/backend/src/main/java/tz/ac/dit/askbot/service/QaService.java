package tz.ac.dit.askbot.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.ac.dit.askbot.dto.*;
import tz.ac.dit.askbot.model.*;
import tz.ac.dit.askbot.repository.ConversationRepository;

import java.util.List;

/**
 * OOP - ABSTRACTION / DEPENDENCY INVERSION.
 * This class holds an AnswerEngine (the INTERFACE), not ClaudeAnswerEngine.
 * It has no idea whether the answer came from Claude or from the offline stub.
 */
@Service
public class QaService {

    private final AnswerEngine answerEngine;
    private final ConversationRepository conversationRepository;

    // Constructor injection - no field @Autowired, no Lombok.
    public QaService(AnswerEngine answerEngine,
                     ConversationRepository conversationRepository) {
        this.answerEngine = answerEngine;
        this.conversationRepository = conversationRepository;
    }

    @Transactional
    public AskResponse ask(AskRequest request) {
        Subject subject = Subject.fromString(request.subject());
        Language language = Language.fromString(request.language());
        String question = request.question().trim();

        Conversation conversation = resolveConversation(request.conversationId(), question, subject, language);

        // POLYMORPHISM in action: we call the interface method.
        String answer = answerEngine.answer(question, subject, language);

        conversation.addMessage(new Message(Message.Role.USER, question));
        conversation.addMessage(new Message(Message.Role.BOT, answer));
        conversationRepository.save(conversation);

        return new AskResponse(conversation.getId(), question, answer,
                subject.getLabel(), language.name());
    }

    private Conversation resolveConversation(Long id, String question, Subject subject, Language language) {
        if (id != null) {
            return conversationRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Conversation " + id + " not found"));
        }
        return new Conversation(buildTitle(question), subject, language);
    }

    /** Java basics: simple string handling for a readable title. */
    private String buildTitle(String question) {
        String cleaned = question.replaceAll("\\s+", " ").trim();
        return cleaned.length() <= 60 ? cleaned : cleaned.substring(0, 57) + "...";
    }

    @Transactional(readOnly = true)
    public List<ConversationDto> listConversations() {
        return conversationRepository.findAllByOrderByIdDesc()
                .stream().map(ConversationDto::summary).toList();
    }

    @Transactional(readOnly = true)
    public ConversationDto getConversation(Long id) {
        Conversation c = conversationRepository.findByIdWithMessages(id)
                .orElseThrow(() -> new IllegalArgumentException("Conversation " + id + " not found"));
        return ConversationDto.full(c);
    }

    @Transactional
    public void deleteConversation(Long id) {
        if (!conversationRepository.existsById(id)) {
            throw new IllegalArgumentException("Conversation " + id + " not found");
        }
        conversationRepository.deleteById(id);
    }

    public List<SubjectDto> listSubjects() {
        return java.util.Arrays.stream(Subject.values()).map(SubjectDto::from).toList();
    }

    public String engineName() { return answerEngine.engineName(); }
}
