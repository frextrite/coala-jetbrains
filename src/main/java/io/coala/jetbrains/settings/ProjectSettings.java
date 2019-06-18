package io.coala.jetbrains.settings;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;

public class ProjectSettings implements ProjectComponent {
    private final Project project;
    private String cwd;
    private String executable;
    private List<String> sections;

    public ProjectSettings(Project project) {
        this.project = project;
        this.cwd = project.getBasePath();
        /* TODO: handle other OSes
         *  in the future this value will be set from the settings panel
         */
        this.executable = null;
        this.sections = new ArrayList<>();
    }

    public Project getProject() {
        return project;
    }

    public String getCwd() {
        return cwd;
    }

    public String getExecutable() {
        return executable;
    }

    public List<String> getSections() {
        return sections;
    }

    public void setExecutable(String executable) {
        this.executable = executable;
    }

    public void addSections(List<String> sections) {
        this.sections.addAll(sections);
    }

    public void addSection(String section) {
        this.sections.add(section);
    }

    public void removeSections(List<String> sections) {
        this.sections.removeAll(sections);
    }

    public void removeSection(String section) {
        this.sections.remove(section);
    }

    public void clearSections() {
        this.sections.clear();
    }
}
