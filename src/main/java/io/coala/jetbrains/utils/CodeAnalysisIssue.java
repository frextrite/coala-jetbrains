package io.coala.jetbrains.utils;

import java.util.List;

public class CodeAnalysisIssue {
    private final String origin;
    private final String message;
    private final List<AffectedCode> affectedCodeList;
    private final IssueSeverity severity;

    private enum IssueSeverity {INFO, WARNING, ERROR}

    public CodeAnalysisIssue(String origin, String message, List<AffectedCode> affectedCodeList, int severity) {
        this.origin = origin;
        this.message = message;
        this.affectedCodeList = affectedCodeList;
        this.severity = getSeverityFromInteger(severity);
    }

    private IssueSeverity getSeverityFromInteger(int severity) {
        return IssueSeverity.values()[severity];
    }

    public String getOrigin() { return origin; }

    public String getMessage() { return message; }

    public List<AffectedCode> getAffectedCodeList() { return affectedCodeList; }

    public IssueSeverity getSeverity() { return severity; }

}
