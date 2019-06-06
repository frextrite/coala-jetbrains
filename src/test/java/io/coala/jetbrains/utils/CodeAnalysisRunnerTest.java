package io.coala.jetbrains.utils;

import com.intellij.execution.configurations.GeneralCommandLine;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CodeAnalysisRunnerTest {
    private static GeneralCommandLine cmd;

    @BeforeClass
    public static void loadGeneralCommandLine() {
        String cwd = "/path/to/file";
        String executable = "coala";
        String section = "jetbrains";
        cmd = CodeAnalysisRunner.getNewGeneralCommandLine(cwd, executable, section);
    }

    @Test
    public void testCommandString() {
        final String commandLineString = cmd.getCommandLineString();
        assertThat(commandLineString).isNotEmpty().isEqualTo("coala --filter-by section_tags jetbrains --json --log-json");
    }

    @Test
    public void testCWD() {
        assertThat(cmd.getWorkDirectory().getPath()).isNotEmpty().isEqualTo("/path/to/file");
    }
}
