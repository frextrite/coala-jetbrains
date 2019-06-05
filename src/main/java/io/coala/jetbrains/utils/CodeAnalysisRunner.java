package io.coala.jetbrains.utils;

import com.intellij.execution.configurations.GeneralCommandLine;

import javax.annotation.Nonnull;

public class CodeAnalysisRunner {
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
    public static GeneralCommandLine getNewGeneralCommandLine(@Nonnull String cwd, @Nonnull String executable, @Nonnull String section) {
        final GeneralCommandLine commandLine = new GeneralCommandLine();
        commandLine.setWorkDirectory(cwd);
        commandLine.setExePath(executable);
        commandLine.addParameters("--filter-by", "section_tags", section);
        commandLine.addParameter("--json");
        commandLine.addParameter("--log-json");
        return commandLine;
    }
}
