package io.coala.jetbrains.utils;

import java.util.List;

public class CodeAnalysisIssue {
    private final String origin;
    private final String message;
    private final List<SourceRange> affectedCodeRange;
    private final IssueSeverity severity;
    private final String debugMessage;
    private final String additionalInformation;

    private enum IssueSeverity {INFO, WARNING, ERROR};

    public CodeAnalysisIssue(String origin, String message, List<SourceRange> affectedCodeRange, IssueSeverity severity, String debugMessage, String additionalInformation) {
        this.origin = origin;
        this.message = message;
        this.affectedCodeRange = affectedCodeRange;
        this.severity = severity;
        this.debugMessage = debugMessage;
        this.additionalInformation = additionalInformation;
    }

}
