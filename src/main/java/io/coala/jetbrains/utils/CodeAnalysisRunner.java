package io.coala.jetbrains.utils;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.*;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class CodeAnalysisRunner implements ProjectComponent {
    private static Logger LOGGER = Logger.getInstance(CodeAnalysisRunner.class);
    private final Project project;

    public CodeAnalysisRunner(Project project) {
        this.project = project;
    }

    /**
     * This method creates a runnable process and runs it.
     * {@link OSProcessHandler#startNotify()} is used to capture the output
     *
     * @param cwd the current working directory for coala
     * @param executable the path to coala executable
     * @param section the coala section tag to run analysis on
     * @throws ExecutionException
     */
    public void submit(@Nonnull String cwd, @Nonnull String executable, @Nonnull String section) throws ExecutionException {
        final GeneralCommandLine commandLine = getNewGeneralCommandLine(cwd, executable, section);

        final String commandLineString = commandLine.getCommandLineString();
        final Process process = commandLine.createProcess();

        LOGGER.warn("Starting process");
        LOGGER.warn(commandLine.getCommandLineString());
        LOGGER.warn("in " + commandLine.getWorkDirectory());

        final OSProcessHandler processHandler = new OSProcessHandler(process, commandLineString);

        final ProcessOutput processOutput = new ProcessOutput();

        processHandler.addProcessListener(new ProcessAdapter() {
            @Override
            public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
                super.onTextAvailable(event, outputType);
                if(outputType.equals(ProcessOutputTypes.STDERR)) {
                    processOutput.appendStderr(event.getText());
                } else if(!outputType.equals(ProcessOutputTypes.SYSTEM)) {
                    processOutput.appendStdout(event.getText());
                }
            }
        });

        processHandler.startNotify();

        // TODO: add wait logic
        processHandler.waitFor(30000);


        // TODO: handle stdout and stderr
        final String stdout = processOutput.getStdout();
        final String stderr = processOutput.getStderr();

        LOGGER.warn(stdout);
        LOGGER.warn(stderr);

        LOGGER.warn("Done!");
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
     * @param cwd the current working directory for coala
     * @param executable the path to coala executable
     * @param section the coala section tag to run analysis on
     * @return an instance of GeneralCommandLine
     */
    public GeneralCommandLine getNewGeneralCommandLine(@Nonnull String cwd, @Nonnull String executable, @Nonnull String section) {
        final GeneralCommandLine commandLine = new GeneralCommandLine();
        commandLine.setWorkDirectory(cwd);
        commandLine.setExePath(executable);
        commandLine.addParameters("--filter-by", "section_tags", section);
        commandLine.addParameter("--json");
        commandLine.addParameter("--log-json");
        return commandLine;
    }
}
