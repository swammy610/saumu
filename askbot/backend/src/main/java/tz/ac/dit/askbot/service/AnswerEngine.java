package tz.ac.dit.askbot.service;

import tz.ac.dit.askbot.model.Language;
import tz.ac.dit.askbot.model.Subject;

/**
 * OOP - ABSTRACTION.
 * The contract: "give me a question, I give you an answer."
 * The controller depends on THIS, never on a concrete class.
 * Swap the implementation and nothing above this line changes.
 */
public interface AnswerEngine {

    String answer(String question, Subject subject, Language language);

    /** Name of this engine, for logging / health checks. */
    String engineName();
}
