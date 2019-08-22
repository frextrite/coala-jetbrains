package io.coala.jetbrains;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.testFramework.fixtures.TempDirTestFixture;
import io.coala.jetbrains.highlight.HighlightIssueFactory;
import io.coala.jetbrains.highlight.HighlightIssueWrapper;
import io.coala.jetbrains.settings.CodeAnalysisSettingsPersistent;
import io.coala.jetbrains.settings.ProjectSettings;
import io.coala.jetbrains.utils.deserializers.CodeAnalysisIssueDeserializerTest;
import java.util.List;
import java.util.Map;

public class MainHighlightTest extends BasePlatformTestCase {

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    final Project myProject = getProject();

    CodeAnalysisSettingsPersistent
        .getInstance(myProject).coalaLocation = "/home/amol/anaconda3/bin/coala";
    myProject.getComponent(ProjectSettings.class).setExecutable("/home/amol/anaconda3/bin/coala");
  }

  @Override
  protected TempDirTestFixture createTempDirTestFixture() {
    return super.createTempDirTestFixture();
  }

  @Override
  protected String getBasePath() {
    return CodeAnalysisIssueDeserializerTest.class.getClassLoader().getResource("main").getPath();
  }

  @Override
  protected String getTestDataPath() {
    return getBasePath();
  }

  public void testOne() {
    System.out.println(getProject().getBasePath());
    System.out.println(this.getBasePath());
    System.out.println(this.getTestDataPath());
  }

  public void testHighlighting() {
    String checkFile = getTestDataPath() + "/check.py";
    String checkFileHighlight = checkFile + ".highlights";

    myFixture.configureByFiles(checkFile, checkFileHighlight);

    myFixture
        .testAction(ActionManager.getInstance().getAction("io.coala.jetbrains.CodeAnalysisAction"));

    myFixture.checkHighlighting(true, true, true, true);
//    myFixture.testHighlighting(true, true, true, checkFile);

    System.out.println(getProject().getBasePath());

    final Map<Document, HighlightIssueWrapper> highlightIssueWrappers = getProject()
        .getComponent(HighlightIssueFactory.class).getHighlightIssueWrappers();

    System.out.println("Map size: " + highlightIssueWrappers.size());
    for (Document document : highlightIssueWrappers.keySet()) {
      System.out.println(highlightIssueWrappers.get(document).getHighlightInfos().size());
    }
  }
}
