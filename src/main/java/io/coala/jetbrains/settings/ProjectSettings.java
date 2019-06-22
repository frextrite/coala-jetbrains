package io.coala.jetbrains.settings;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;

public class ProjectSettings implements ProjectComponent {
    private final Project project;
    private long timeOutInSeconds;
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
        /* TODO: add timeout option in settings panel
         */
        this.timeOutInSeconds = 120;
    }

    public Project getProject() {
        return project;
    }

    public long getTimeOutInSeconds() {
        return timeOutInSeconds;
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

    public void setTimeOutInSeconds(long timeOutInSeconds) {
        this.timeOutInSeconds = timeOutInSeconds;
    }

    public void setExecutable(String executable) {
        this.executable = executable;
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
}
