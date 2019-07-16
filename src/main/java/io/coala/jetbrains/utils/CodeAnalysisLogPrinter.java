package io.coala.jetbrains.utils;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import io.coala.jetbrains.ui.CodeAnalysisConsoleView;
import java.util.List;

public class CodeAnalysisLogPrinter implements ProjectComponent {

  private static final Logger LOGGER = Logger.getInstance(CodeAnalysisLogPrinter.class);

  private final Project project;
  private final CodeAnalysisConsoleView codeAnalysisConsoleView;

  public CodeAnalysisLogPrinter(Project project, CodeAnalysisConsoleView codeAnalysisConsoleView) {
    this.project = project;
    this.codeAnalysisConsoleView = codeAnalysisConsoleView;
  }

  public void submit(List<CodeAnalysisLog> codeAnalysisLogList) {
    for (CodeAnalysisLog log : codeAnalysisLogList) {
      String message = log.getMessage();
      CodeInspectionSeverity severity = log.getSeverity();
      String timestamp = log.getPrintableTimeStamp();
      
      codeAnalysisConsoleView
          .print(CodeAnalysisLog.getSeverityTagFromSeverity(severity), severity);
      codeAnalysisConsoleView.print(timestamp, CodeInspectionSeverity.INFO);
      codeAnalysisConsoleView.print(message + "\n", CodeInspectionSeverity.INFO);
    }
  }
}
