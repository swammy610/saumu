package tz.ac.dit.askbot.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.ac.dit.askbot.dto.*;
import tz.ac.dit.askbot.service.QaService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class QaController {

    private final QaService qaService;

    public QaController(QaService qaService) {
        this.qaService = qaService;
    }

    @PostMapping("/ask")
    public AskResponse ask(@Valid @RequestBody AskRequest request) {
        return qaService.ask(request);
    }

    @GetMapping("/subjects")
    public List<SubjectDto> subjects() {
        return qaService.listSubjects();
    }

    @GetMapping("/conversations")
    public List<ConversationDto> conversations() {
        return qaService.listConversations();
    }

    @GetMapping("/conversations/{id}")
    public ConversationDto conversation(@PathVariable Long id) {
        return qaService.getConversation(id);
    }

    @DeleteMapping("/conversations/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        qaService.deleteConversation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "engine", qaService.engineName());
    }
}
