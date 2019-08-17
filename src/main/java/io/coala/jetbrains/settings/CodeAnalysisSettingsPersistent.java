package io.coala.jetbrains.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
    name = "CodeAnalysisSettingsPersistent"
)
public class CodeAnalysisSettingsPersistent implements
    PersistentStateComponent<CodeAnalysisSettingsPersistent> {

  public String coalaLocation;

  public CodeAnalysisSettingsPersistent() {
    coalaLocation = "";
  }

  @Nullable
  @Override
  public CodeAnalysisSettingsPersistent getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull CodeAnalysisSettingsPersistent state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  @Nullable
  public static CodeAnalysisSettingsPersistent getInstance(@NotNull Project project) {
    return ServiceManager.getService(project, CodeAnalysisSettingsPersistent.class);
  }
}
