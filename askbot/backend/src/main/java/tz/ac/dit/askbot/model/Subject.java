package tz.ac.dit.askbot.model;

/**
 * JAVA BASICS: an enum is a fixed set of constants.
 * Each constant carries its own data (label, swahili label)
 * AND its own behaviour (promptHint) - encapsulation at enum level.
 */
public enum Subject {

    GENERAL("General Knowledge", "Maarifa ya Jumla"),
    MATHEMATICS("Mathematics", "Hisabati"),
    PHYSICS("Physics", "Fizikia"),
    CHEMISTRY("Chemistry", "Kemia"),
    BIOLOGY("Biology", "Biolojia"),
    COMPUTER_SCIENCE("Computer Science", "Sayansi ya Kompyuta"),
    ENGINEERING("Engineering", "Uhandisi"),
    HISTORY("History", "Historia"),
    GEOGRAPHY("Geography", "Jiografia"),
    ECONOMICS("Economics", "Uchumi"),
    LANGUAGE("Language & Literature", "Lugha na Fasihi"),
    BUSINESS("Business & Accounting", "Biashara na Uhasibu");

    private final String label;
    private final String swahiliLabel;

    Subject(String label, String swahiliLabel) {
        this.label = label;
        this.swahiliLabel = swahiliLabel;
    }

    public String getLabel() { return label; }

    public String getSwahiliLabel() { return swahiliLabel; }

    /** Behaviour attached to the constant itself. */
    public String promptHint() {
        return switch (this) {
            case MATHEMATICS -> "Show every step of the calculation. Use clear notation.";
            case PHYSICS, CHEMISTRY -> "State the formula, list given values with units, then solve step by step.";
            case COMPUTER_SCIENCE, ENGINEERING -> "Give concrete code or worked examples where useful.";
            case HISTORY, GEOGRAPHY -> "Give dates, places and context. Prefer Tanzanian / East African examples where relevant.";
            case LANGUAGE -> "Explain grammar or meaning plainly, with example sentences.";
            case BUSINESS, ECONOMICS -> "Use TZS for money examples where relevant.";
            case BIOLOGY -> "Explain the process or structure clearly, naming the parts involved.";
            default -> "Answer clearly and completely.";
        };
    }

    public static Subject fromString(String raw) {
        if (raw == null || raw.isBlank()) return GENERAL;
        try {
            return Subject.valueOf(raw.trim().toUpperCase().replace(' ', '_'));
        } catch (IllegalArgumentException ex) {
            return GENERAL;
        }
    }
}
