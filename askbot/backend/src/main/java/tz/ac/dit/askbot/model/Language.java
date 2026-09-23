package tz.ac.dit.askbot.model;

public enum Language {
    ENGLISH("English", "Answer in clear English."),
    SWAHILI("Kiswahili", "Jibu kwa Kiswahili sanifu, kwa lugha rahisi na mifano."),
    AUTO("Auto", "Reply in the SAME language the question was asked in (English or Kiswahili).");

    private final String label;
    private final String instruction;

    Language(String label, String instruction) {
        this.label = label;
        this.instruction = instruction;
    }

    public String getLabel() { return label; }
    public String getInstruction() { return instruction; }

    public static Language fromString(String raw) {
        if (raw == null || raw.isBlank()) return AUTO;
        try {
            return Language.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return AUTO;
        }
    }
}
