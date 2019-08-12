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
import org.jetbrains.annotations.Nullable;

public class CodeAnalysisConsoleView implements ProjectComponent {

  private final Project project;
  private ConsoleView consoleView;

  public CodeAnalysisConsoleView(Project project) {
    this.project = project;
    this.consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
  }

  /**
   * This method returns the {@link ConsoleView} instance we're working with.
   *
   * @return instance of {@link ConsoleView}
   */
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
  public void print(@Nullable String message, CodeInspectionSeverity severity) {
    if (message == null || message.isEmpty()) {
      return;
    }

    if (consoleView == null) {
      error("Error printing message. Cannot initialize console.");
      return;
    }

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
    consoleView.clear();
  }

  private void info(String message) {
    consoleView.print(message, ConsoleViewContentType.NORMAL_OUTPUT);
  }

  private void warn(String message) {
    consoleView.print(message, ConsoleViewContentType.LOG_WARNING_OUTPUT);
  }

  private void error(String message) {
    consoleView.print(message, ConsoleViewContentType.ERROR_OUTPUT);
  }

  private void debug(String message) {
    consoleView.print(message, getDebugConsoleViewContentType());
  }

  private void verbose(String message) {
    consoleView.print(message, ConsoleViewContentType.LOG_VERBOSE_OUTPUT);
  }

  private ConsoleViewContentType getDebugConsoleViewContentType() {
    final Color color = new Color(34, 183, 52);
    final JBColor debugColor = new JBColor(color, color);

    final TextAttributes debugOutputAttributes = ConsoleViewContentType.LOG_DEBUG_OUTPUT
        .getAttributes();
    debugOutputAttributes.setForegroundColor(debugColor);
    return new ConsoleViewContentType("CONSOLE_DEBUG_OUTPUT", debugOutputAttributes);
  }
}
