package io.coala.jetbrains.settings;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ProjectSettingsTest extends BasePlatformTestCase {

  private ProjectSettings projectSettings;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    Project project = myFixture.getProject();
    ProgressManager progressManager = project.getComponent(ProgressManager.class);
    this.projectSettings = new ProjectSettings(project, progressManager);
  }

  public void testAutomaticDetectionAndSettingOfExecutable()
      throws IOException, ExecutionException, InterruptedException {
    Path projectSettingsExecutable;

    if (SystemInfo.isUnix) {
      projectSettingsExecutable = projectSettings.determineAndSetExecutable("which");

      assertThat(projectSettingsExecutable).isNotNull()
          .isEqualTo(Paths.get("/usr/bin/which").toAbsolutePath());
    }
    if (SystemInfo.isWindows) {
      projectSettingsExecutable = projectSettings.determineAndSetExecutable("where");

      assertThat(projectSettingsExecutable).isNotNull()
          .isEqualTo(Paths.get("C:\\Windows\\System32\\where.exe").toAbsolutePath());
    }
  }

  public void testAutomaticDetectionAndSettingOfExecutableIfNotExists()
      throws InterruptedException, ExecutionException, IOException {
    Path projectSettingsExecutable;

    if (SystemInfo.isUnix) {
      projectSettingsExecutable = projectSettings.determineAndSetExecutable("does_not_exist");

      assertThat(projectSettingsExecutable).isNull();
    }
    if (SystemInfo.isWindows) {
      projectSettingsExecutable = projectSettings.determineAndSetExecutable("does_not_exist");

      assertThat(projectSettingsExecutable).isNull();
    }
  }
}
