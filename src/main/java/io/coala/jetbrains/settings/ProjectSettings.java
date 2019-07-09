package io.coala.jetbrains.settings;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;

public class ProjectSettings implements ProjectComponent {

  private static Logger LOGGER = Logger.getInstance(ProjectSettings.class);

  private final String pathToWhichExecutable = "/usr/bin/which";
  private final String pathToWhereExecutable = "C:\\Windows\\System32\\where.exe";

  private final Project project;
  private final ProgressManager progressManager;
  private long timeOutInSeconds;
  private String cwd;
  private String executable;
  private List<String> sections;


  public ProjectSettings(Project project, ProgressManager progressManager) {
    this.project = project;
    this.cwd = project.getBasePath();
    this.executable = null;
    this.sections = new ArrayList<>();
    /* TODO: add timeout option in settings panel */
    this.timeOutInSeconds = 120;
    this.progressManager = progressManager;
    runAutomaticExecutableRetrieval();
  }

  public Project getProject() {
    return project;
  }

  public long getTimeOutInSeconds() {
    return timeOutInSeconds;
  }

  public void setTimeOutInSeconds(long timeOutInSeconds) {
    this.timeOutInSeconds = timeOutInSeconds;
  }

  public String getCwd() {
    return cwd;
  }

  public String getExecutable() {
    return executable;
  }

  public void setExecutable(String executable) {
    this.executable = executable;
  }

  public List<String> getSections() {
    return sections;
  }

  public void addSectionsToFilter(List<String> sectionsList) {
    this.sections.addAll(sectionsList);
  }

  public void addSectionToFilter(String section) {
    this.sections.add(section);
  }

  public void removeSectionsFromFilter(List<String> sectionsList) {
    this.sections.removeAll(sectionsList);
  }

  public void removeSectionFromFilter(String section) {
    this.sections.remove(section);
  }

  public void clearSections() {
    this.sections.clear();
  }

  /**
   * This methods searches for the executable in PATH and returns its exact path.
   *
   * @param executable the name of the executable to search in PATH
   * @return the path to the executable
   * @throws ExecutionException
   * @throws InterruptedException
   * @throws IOException
   */
  public Path determineAndSetExecutable(String executable)
      throws ExecutionException, InterruptedException, IOException {
    final GeneralCommandLine commandLine = new GeneralCommandLine().withParameters(executable);

    if (SystemInfo.isUnix) {
      commandLine.setExePath(pathToWhichExecutable);
    } else if (SystemInfo.isWindows) {
      commandLine.setExePath(pathToWhereExecutable);
    }

    Process process = commandLine.createProcess();
    process.waitFor();

    if (process.exitValue() == 0) {
      String parsedExecutable = IOUtils
          .toString(process.getInputStream(), Charset.defaultCharset()).trim();
      return Paths.get(parsedExecutable);
    }

    return null;
  }

  private void runAutomaticExecutableRetrieval() {
    final Application app = ApplicationManager.getApplication();
    app.executeOnPooledThread(() -> app.invokeLater(() -> {
      try {
        Path coalaExecutableLocation = determineAndSetExecutable("coala");
        if (coalaExecutableLocation != null) {
          executable = coalaExecutableLocation.toString();
        }
      } catch (ExecutionException | InterruptedException | IOException e) {
        e.printStackTrace();
      }
    }, ModalityState.NON_MODAL));
  }
}
