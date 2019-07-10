package io.coala.jetbrains.analysis;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import io.coala.jetbrains.settings.ProjectSettings;
import io.coala.jetbrains.ui.CodeAnalysisConsoleView;
import io.coala.jetbrains.utils.CodeInspectionSeverity;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

public class CodeAnalysisRunner implements ProjectComponent {

  private static final Logger LOGGER = Logger.getInstance(CodeAnalysisRunner.class);
  private final ProjectSettings projectSettings;
  private final Project project;
  private final CodeAnalysisConsoleView codeAnalysisConsoleView;

  public CodeAnalysisRunner(Project project, ProjectSettings projectSettings,
      CodeAnalysisConsoleView codeAnalysisConsoleView) {
    this.project = project;
    this.projectSettings = projectSettings;
    this.codeAnalysisConsoleView = codeAnalysisConsoleView;
  }

  /**
   * This method creates a runnable process and runs it.
   * {@link OSProcessHandler#startNotify()} is used to capture the output
   *
   * @return the instance with all the required information about the run
   */
  public ProcessOutput analyze() throws ExecutionException {
    final GeneralCommandLine commandLine = getNewGeneralCommandLine();
    final String commandLineString = commandLine.getCommandLineString();
    final Process process = commandLine.createProcess();
    final OSProcessHandler processHandler = new OSProcessHandler(process, commandLineString);
    final ProcessOutput processOutput = getProcessOutputWithTextAvailableListener(processHandler);

    codeAnalysisConsoleView.clear();
    LOGGER.info("Running coala command " + commandLineString);
    codeAnalysisConsoleView
        .print("Running coala command " + commandLineString + "\n", CodeInspectionSeverity.VERBOSE);

    addConsolePrinterListener(processHandler);
    processHandler.startNotify();
    holdAndWaitProcess(processHandler, processOutput);

    LOGGER.info("Finished running coala.");
    codeAnalysisConsoleView.print("Finished running coala.", CodeInspectionSeverity.VERBOSE);

    return processOutput;
  }

  /**
   * This method creates a new instance of GeneralCommandLine
   * and sets its configurations according to the input params provided
   *
   * <p>The following configurations are set:
   * the current working directory
   * the executable path
   * and the following parameters
   * --filter-by specifies which sections (in .coafile) to run coala on
   * --json specifies the output should be in JSON format
   * --log-json specifies the logging statements must be in JSON as well
   *
   * @return an instance of GeneralCommandLine
   */
  public GeneralCommandLine getNewGeneralCommandLine() {
    final String cwd = projectSettings.getCwd();
    final String executable = projectSettings.getExecutable();
    final List<String> sections = projectSettings.getSections();

    final GeneralCommandLine commandLine = new GeneralCommandLine();
    commandLine.setWorkDirectory(cwd);
    commandLine.setExePath(executable);
    commandLine.addParameter("--json");
    commandLine.addParameter("-V");

    commandLine.addParameters("--filter-by", "section_tags");
    for (String section : sections) {
      commandLine.addParameters(section);
    }

    return commandLine;
  }

  /**
   * This method waits for the process to exit and subsequently
   * sets the required flags for {@link ProcessOutput}.
   *
   * @param processHandler the instance of running process
   * @param processOutput the instance to set appropriate flags
   */
  private void holdAndWaitProcess(@NotNull OSProcessHandler processHandler,
      @NotNull ProcessOutput processOutput) {
    final long timeOutInMilliseconds = TimeUnit.SECONDS
        .toMillis(projectSettings.getTimeOutInSeconds());

    if (processHandler.waitFor(timeOutInMilliseconds)) {
      LOGGER.info("Process exited with exit code " + processHandler.getExitCode());
      processOutput.setExitCode(processHandler.getExitCode());
    } else {
      LOGGER.info("Thanos finger snap!");
      processHandler.destroyProcess();
      processOutput.setTimeout();
    }
  }

  /**
   * This method creates an instance of {@link ProcessOutput}
   * and attaches it to the given {@link OSProcessHandler}.
   *
   * @param processHandler an instance of {@link OSProcessHandler} which is to be linked with
   *     {@link ProcessOutput}
   * @return an instance of {@link ProcessOutput} attached to the process handler
   */
  private ProcessOutput getProcessOutputWithTextAvailableListener(
      @NotNull OSProcessHandler processHandler) {
    final ProcessOutput processOutput = new ProcessOutput();

    addTextAvailableListener(processHandler, processOutput);

    return processOutput;
  }

  /**
   * This method attaches a {@link ProcessAdapter#onTextAvailable} listener
   * to the given instance of {@link OSProcessHandler}.
   *
   * @param processHandler the instance to which the listener is to be attached
   * @param processOutput the instance to which output from stdout and stderr is appended
   *     to create strings
   */
  private void addTextAvailableListener(@NotNull OSProcessHandler processHandler,
      @NotNull ProcessOutput processOutput) {
    processHandler.addProcessListener(new ProcessAdapter() {
      @Override
      public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
        String text = event.getText();

        if (outputType.equals(ProcessOutputTypes.STDERR)) {
          processOutput.appendStderr(text);
        } else if (!outputType.equals(ProcessOutputTypes.SYSTEM)) {
          processOutput.appendStdout(text);
        }
      }
    });
  }

  private String determineFinalText(String text, CodeInspectionSeverity severity,
      boolean isOverflow) {
    if (!isOverflow) {
      if (severity == CodeInspectionSeverity.ERROR || severity == CodeInspectionSeverity.DEBUG) {
        return text.substring(7);
      } else if (severity == CodeInspectionSeverity.WARNING) {
        return text.substring(9);
      }
    }

    return text;
  }

  private void printToConsole(String text, CodeInspectionSeverity prefixSeverity,
      CodeInspectionSeverity suffixSeverity, boolean isOverflow) {
    final String errorPrefix = "[ERROR]";
    final String debugPrefix = "[DEBUG]";
    final String warningPrefix = "[WARNING]";

    if (!isOverflow) {
      final String textToPrint = determineFinalText(text, prefixSeverity, isOverflow);

      if (prefixSeverity == CodeInspectionSeverity.ERROR) {
        codeAnalysisConsoleView.print(errorPrefix, prefixSeverity);
      } else if (prefixSeverity == CodeInspectionSeverity.DEBUG) {
        codeAnalysisConsoleView.print(debugPrefix, prefixSeverity);
      } else if (prefixSeverity == CodeInspectionSeverity.WARNING) {
        codeAnalysisConsoleView.print(warningPrefix, prefixSeverity);
      } else {
        codeAnalysisConsoleView.print("\n", CodeInspectionSeverity.INFO);
      }

      codeAnalysisConsoleView.print(textToPrint, suffixSeverity);
    } else {
      if (prefixSeverity == CodeInspectionSeverity.WARNING) {
        codeAnalysisConsoleView.print("\b\b\b\b\b\b\b\b\b" + warningPrefix, prefixSeverity);
      } else if (prefixSeverity == CodeInspectionSeverity.ERROR) {
        codeAnalysisConsoleView.print("\b\b\b\b\b\b\b" + errorPrefix, prefixSeverity);
      } else if (prefixSeverity == CodeInspectionSeverity.DEBUG) {
        codeAnalysisConsoleView.print("\b\b\b\b\b\b\b" + debugPrefix, prefixSeverity);
      } else {
        codeAnalysisConsoleView.print("\n", CodeInspectionSeverity.INFO);
      }

      codeAnalysisConsoleView.print(text, suffixSeverity);
    }
  }

  private void addConsolePrinterListener(@NotNull OSProcessHandler processHandler) {
    processHandler.addProcessListener(new ProcessAdapter() {
      String previousOutput = "";

      @Override
      public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
        final String text = event.getText();

        if (text == null) {
          return;
        }

        if (outputType.equals(ProcessOutputTypes.STDERR)) {
          final String severityCheckText =
              text.length() >= 10 ? text.substring(0, 10).toLowerCase() : null;
          final int previousOutputLength = previousOutput.length();
          final String previousOutputSeverityCheckText =
              previousOutput.length() >= 7 ? previousOutput.substring(previousOutputLength - 7)
                  .toLowerCase() : "";

          if (severityCheckText != null) {
            if (severityCheckText.contains("[error]")) {
              printToConsole(text, CodeInspectionSeverity.ERROR,
                  CodeInspectionSeverity.INFO, false);
            } else if (severityCheckText.contains("[debug]")) {
              printToConsole(text, CodeInspectionSeverity.DEBUG,
                  CodeInspectionSeverity.INFO, false);
            } else if (severityCheckText.contains("[warning]")) {
              printToConsole(text, CodeInspectionSeverity.WARNING,
                  CodeInspectionSeverity.INFO, false);
            } else if (previousOutputSeverityCheckText.contains("[error]")) {
              printToConsole(text, CodeInspectionSeverity.ERROR,
                  CodeInspectionSeverity.INFO, true);
            } else if (previousOutputSeverityCheckText.contains("[debug]")) {
              printToConsole(text, CodeInspectionSeverity.DEBUG,
                  CodeInspectionSeverity.INFO, true);
            } else if (previousOutputSeverityCheckText.contains("arning]")) {
              printToConsole(text, CodeInspectionSeverity.WARNING,
                  CodeInspectionSeverity.INFO, true);
            } else {
              codeAnalysisConsoleView.print(text, CodeInspectionSeverity.INFO);
            }
          } else {
            codeAnalysisConsoleView.print(text, CodeInspectionSeverity.INFO);
          }

          previousOutput = text.toLowerCase();
        }
      }
    });
  }
}
