package io.coala.jetbrains.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.project.Project;
import io.coala.jetbrains.analysis.CodeAnalysisRunner;
import io.coala.jetbrains.settings.ProjectSettings;
import io.coala.jetbrains.ui.CodeAnalysisConsoleView;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;

public class CodeAnalysisRunnerTest {

  private static final Project project = mock(Project.class);
  private static final ProjectSettings projectSettings = mock(ProjectSettings.class);
  private static final CodeAnalysisConsoleView codeAnalysisConsoleView = mock(
      CodeAnalysisConsoleView.class);

  private static final String cwd = "/path/to/file";
  private static final String executable = "coala";
  private static final List<String> sections = new ArrayList<String>(Arrays.asList("jetbrains"));

  private static GeneralCommandLine cmd;

  @BeforeClass
  public static void loadGeneralCommandLine() throws FileNotFoundException {
    when(projectSettings.getCwd()).thenReturn(cwd);
    when(projectSettings.getExecutable()).thenReturn(executable);
    when(projectSettings.getSections()).thenReturn(sections);

    final CodeAnalysisRunner codeAnalysisRunner = new CodeAnalysisRunner(project, projectSettings,
        codeAnalysisConsoleView);
    cmd = codeAnalysisRunner.getNewGeneralCommandLine();
  }

  @Test
  public void testCommandString() {
    final String commandLineString = cmd.getCommandLineString();
    assertThat(commandLineString).isNotEmpty()
        .isEqualTo("coala --json --log-json --filter-by section_tags jetbrains");
  }

  @Test
  public void testCwd() {
    final Path path = cmd.getWorkDirectory().toPath();
    assertThat(path).isEqualTo(Paths.get("/path/to/file"));
  }
}
