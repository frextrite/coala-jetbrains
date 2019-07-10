package io.coala.jetbrains.ui;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import io.coala.jetbrains.utils.CodeInspectionSeverity;
import java.awt.Color;

public class CodeAnalysisConsoleView implements ProjectComponent {

  private final Project project;
  private ConsoleView consoleView;

  public CodeAnalysisConsoleView(Project project) {
    this.project = project;
    this.consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
  }

  public ConsoleView getConsoleView() {
    return consoleView;
  }

  /**
   * This method prints the message to the console.
   *
   * <p>Check {@link CodeInspectionSeverity} for the available message severity types
   *
   * @param message the message to be printed to the console
   * @param severity the severity of the printed message
   */
  public void print(String message, CodeInspectionSeverity severity) {
    switch (severity) {
      case INFO:
        info(message);
        break;

      case WARNING:
        warn(message);
        break;

      case ERROR:
        error(message);
        break;

      case DEBUG:
        debug(message);
        break;

      case VERBOSE:
        verbose(message);
        break;

      default:
        error("Internal Plugin Error. Unidentified severity specified.");
        break;
    }
  }

  public void clear() {
    this.consoleView.clear();
  }

  private void info(String message) {
    this.consoleView.print(message, ConsoleViewContentType.NORMAL_OUTPUT);
  }

  private void warn(String message) {
    this.consoleView.print(message, ConsoleViewContentType.LOG_WARNING_OUTPUT);
  }

  private void error(String message) {
    this.consoleView.print(message, ConsoleViewContentType.ERROR_OUTPUT);
  }

  private void debug(String message) {
    this.consoleView.print(message, getDebugCosoleViewContentType());
  }

  private void verbose(String message) {
    this.consoleView.print(message, ConsoleViewContentType.LOG_VERBOSE_OUTPUT);
  }

  private ConsoleViewContentType getDebugCosoleViewContentType() {
    final ConsoleViewContentType errorOutput = ConsoleViewContentType.LOG_DEBUG_OUTPUT;
    final Color color = new Color(34, 183, 52);

    final TextAttributes errorOutputAttributes = errorOutput.getAttributes();
    errorOutputAttributes.setForegroundColor(new JBColor(color, color));
    return new ConsoleViewContentType("CONSOLE_DEBUG_OUTPUT", errorOutputAttributes);
  }
}
