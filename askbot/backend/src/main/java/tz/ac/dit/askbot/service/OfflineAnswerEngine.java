package tz.ac.dit.askbot.service;

import org.springframework.stereotype.Service;
import tz.ac.dit.askbot.model.Language;
import tz.ac.dit.askbot.model.Subject;

/**
 * OOP - POLYMORPHISM: same AnswerEngine contract, completely different guts.
 * Useful for demos with no API key and no internet.
 * Inject it with @Qualifier("offlineAnswerEngine") to swap engines.
 */
@Service("offlineAnswerEngine")
public class OfflineAnswerEngine implements AnswerEngine {

    @Override
    public String engineName() { return "Offline (demo)"; }

    @Override
    public String answer(String question, Subject subject, Language language) {
        String heading = language == Language.SWAHILI
                ? "Hali ya nje ya mtandao"
                : "Offline mode";
        return """
               ## %s

               **Subject:** %s (%s)
               **Your question:** %s

               This is the offline demo engine. It proves the architecture works \
               end to end without calling any external API. Set `ANTHROPIC_API_KEY` \
               and restart to get real AI answers from Claude.
               """.formatted(heading, subject.getLabel(), subject.getSwahiliLabel(), question);
    }
}
