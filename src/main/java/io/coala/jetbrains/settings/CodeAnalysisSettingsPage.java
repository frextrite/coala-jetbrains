package io.coala.jetbrains.settings;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.ui.TextFieldWithHistory;
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton;
import com.intellij.util.ui.SwingHelper;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;
import org.jetbrains.annotations.Nullable;

public class CodeAnalysisSettingsPage implements Configurable {

  private final Project project;

  private TextFieldWithHistoryWithBrowseButton coalaLocation;
  private JLabel coalaLocationLabel;
  private JPanel panel;

  public CodeAnalysisSettingsPage(Project project) {
    this.project = project;
  }

  @Nls(capitalization = Capitalization.Title)
  @Override
  public String getDisplayName() {
    return "coala";
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    initializeComponents();
    return panel;
  }

  private void initializeComponents() {
    final String coalaLocationLabelString = "coala Script Location";
    coalaLocationLabel.setText(coalaLocationLabelString);

    initializeTextFieldWithHistoryWithBrowseButton();
  }

  private void initializeTextFieldWithHistoryWithBrowseButton() {
    final TextFieldWithHistory textFieldWithHistory = coalaLocation.getChildComponent();
    textFieldWithHistory.setHistorySize(-1);
    textFieldWithHistory.setMinimumAndPreferredWidth(-1);

    SwingHelper.installFileCompletionAndBrowseDialog(project, coalaLocation,
        "Select coala Script Location",
        FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor());
  }

  @Override
  public boolean isModified() {
    // @TODO: Implement logic
    return true;
  }

  @Override
  public void apply() throws ConfigurationException {
    final String location = coalaLocation.getChildComponent().getText();
    if (isPathValid(location)) {
      updateScriptPath(location);
    }
  }

  private boolean isPathValid(String location) {
    // @TODO: Implement logic
    return true;
  }

  private void updateScriptPath(String location) {
    final ProjectSettings settings = getProjectSettings();
    settings.setExecutable(location);
  }

  private ProjectSettings getProjectSettings() {
    return project.getComponent(ProjectSettings.class);
  }
}
