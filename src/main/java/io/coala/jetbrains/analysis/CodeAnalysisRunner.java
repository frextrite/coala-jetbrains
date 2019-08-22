package io.coala.jetbrains.analysis;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import io.coala.jetbrains.settings.ProjectSettings;
import io.coala.jetbrains.ui.CodeAnalysisConsoleView;
import io.coala.jetbrains.utils.CodeInspectionSeverity;
import java.io.FileNotFoundException;
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
  public ProcessOutput analyze() throws ExecutionException, FileNotFoundException {
    final GeneralCommandLine commandLine = getNewGeneralCommandLine();
    final String commandLineString = commandLine.getCommandLineString();
    final Process process = commandLine.createProcess();
    final OSProcessHandler processHandler = new OSProcessHandler(process, commandLineString);
    final ProcessOutput processOutput = getProcessOutputWithTextAvailableListener(processHandler);

    showToolWindow();

    final String logRunCommand = "Running coala command \"" + commandLineString + "\" in " + project.getBasePath() + "\n";
    codeAnalysisConsoleView.clear();
    codeAnalysisConsoleView
        .print(logRunCommand, CodeInspectionSeverity.VERBOSE);
    LOGGER.info(logRunCommand);

    processHandler.startNotify();
    holdAndWaitProcess(processHandler, processOutput);

    LOGGER.info("Finished running coala.");

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
  public GeneralCommandLine getNewGeneralCommandLine() throws FileNotFoundException {
    final String cwd = projectSettings.getCwd();
    final String executable = projectSettings.getExecutable();
    final List<String> sections = projectSettings.getSections();

    if (executable == null) {
      throw new FileNotFoundException(null);
    }

    final GeneralCommandLine commandLine = new GeneralCommandLine();
    commandLine.setWorkDirectory(cwd);
    commandLine.setExePath(executable);
    commandLine.addParameter("--json");
    commandLine.addParameter("--log-json");

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

  private void showToolWindow() {
    ApplicationManager.getApplication().invokeLater(
        () -> {
          final ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("coala");
          if (toolWindow == null) {
            return;
          }
          toolWindow.show(null);
        });
  }
}
