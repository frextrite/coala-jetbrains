package io.coala.jetbrains.highlight;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;

public class IssueManager implements ProjectComponent {



  private final Project project;

  public IssueManager(Project project) {
    this.project = project;
  }

}
