package tz.ac.dit.askbot.dto;

import tz.ac.dit.askbot.model.Subject;

public record SubjectDto(String code, String label, String swahiliLabel) {
    public static SubjectDto from(Subject s) {
        return new SubjectDto(s.name(), s.getLabel(), s.getSwahiliLabel());
    }
}
