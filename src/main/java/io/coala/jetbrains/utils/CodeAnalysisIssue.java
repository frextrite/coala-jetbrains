package io.coala.jetbrains.utils;

import java.util.List;

public class CodeAnalysisIssue {
    private final String origin;
    private final String message;
    private final List<SourceRange> affectedCodeRange;
    private final IssueSeverity severity;

    private enum IssueSeverity {INFO, WARNING, ERROR}

    public CodeAnalysisIssue(String origin, String message, List<SourceRange> affectedCodeRange, int severity) {
        this.origin = origin;
        this.message = message;
        this.affectedCodeRange = affectedCodeRange;
        this.severity = getSeverityFromInteger(severity);
    }

    private IssueSeverity getSeverityFromInteger(int severity) {
        return IssueSeverity.values()[severity];
    }

    public String getOrigin() { return origin; }

    public String getMessage() { return message; }

    public List<SourceRange> getAffectedCodeRange() { return affectedCodeRange; }

    public IssueSeverity getSeverity() { return severity; }

}
