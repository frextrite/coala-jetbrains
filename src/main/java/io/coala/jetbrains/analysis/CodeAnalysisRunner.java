package io.coala.jetbrains.analysis;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.*;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import io.coala.jetbrains.settings.ProjectSettings;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CodeAnalysisRunner implements ProjectComponent {
    private static Logger LOGGER = Logger.getInstance(CodeAnalysisRunner.class);
    private final ProjectSettings projectSettings;
    private final Project project;

    public CodeAnalysisRunner(Project project, ProjectSettings projectSettings) {
        this.project = project;
        this.projectSettings = projectSettings;
    }

    /**
     * This method creates a runnable process and runs it.
     * {@link OSProcessHandler#startNotify()} is used to capture the output
     *
     * @return the instance with all the required information about the run
     * @throws ExecutionException
     */
    public ProcessOutput analyze() throws ExecutionException {
        final GeneralCommandLine commandLine = getNewGeneralCommandLine();
        final String commandLineString = commandLine.getCommandLineString();
        final Process process = commandLine.createProcess();
        final OSProcessHandler processHandler = new OSProcessHandler(process, commandLineString);
        final ProcessOutput processOutput = getProcessOutputWithTextAvailableListener(processHandler);

        LOGGER.info("Running coala command " + commandLineString);

        processHandler.startNotify();
        holdAndWaitProcess(processHandler, processOutput);

        final String stdout = processOutput.getStdout();
        LOGGER.info(stdout);

        LOGGER.info("Finished running coala.");

        return processOutput;
    }

    /**
     * This method creates a new instance of GeneralCommandLine
     * and sets its configurations according to the input params provided
     * <p>
     * The following configurations are set:
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
        commandLine.addParameter("--log-json");

        commandLine.addParameters("--filter-by", "section_tags");
        for(String section : sections) {
            commandLine.addParameters(section);
        }

        return commandLine;
    }

    /**
     * This method waits for the process to exit and subsequently
     * sets the required flags for {@link ProcessOutput}
     *
     * @param processHandler the instance of running process
     * @param processOutput the instance to set appropriate flags
     */
    private void holdAndWaitProcess(@NotNull OSProcessHandler processHandler, @NotNull ProcessOutput processOutput) {
        final long timeOutInMilliseconds = TimeUnit.SECONDS.toMillis(projectSettings.getTimeOutInSeconds());

        if(processHandler.waitFor(timeOutInMilliseconds)) {
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
     * and attaches it to the given {@link OSProcessHandler}
     *
     * @param processHandler an instance of {@link OSProcessHandler} which
     *                       is to be linked with {@link ProcessOutput}
     * @return an instance of {@link ProcessOutput} attached to the process handler
     */
    private ProcessOutput getProcessOutputWithTextAvailableListener(@NotNull OSProcessHandler processHandler) {
        final ProcessOutput processOutput = new ProcessOutput();

        addTextAvailableListener(processHandler, processOutput);

        return processOutput;
    }

    /**
     * This method attaches a {@link ProcessAdapter#onTextAvailable} listener
     * to the given instance of {@link OSProcessHandler}
     *
     * @param processHandler the instance to which the listener is to be attached
     * @param processOutput the instance to which output from stdout and stderr is appended
     *                      to create strings
     */
    private void addTextAvailableListener(@NotNull OSProcessHandler processHandler, @NotNull ProcessOutput processOutput) {
        processHandler.addProcessListener(new ProcessAdapter() {
            @Override
            public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
                String text = event.getText();

                if(outputType.equals(ProcessOutputTypes.STDERR)) {
                    processOutput.appendStderr(text);
                } else if(!outputType.equals(ProcessOutputTypes.SYSTEM)) {
                    processOutput.appendStdout(text);
                }
            }
        });
    }
}
