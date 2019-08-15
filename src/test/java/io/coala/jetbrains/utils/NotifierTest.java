package io.coala.jetbrains.utils;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class NotifierTest extends BasePlatformTestCase {

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  public void testShowInformationNotification() {
    Notifier.showInformationNotification("Information");
  }

  public void testShowWarningNotification() {
    Notifier.showWarningNotification("Warning");
  }

  public void testShowErrorNotification() {
    Notifier.showErrorNotification("Random exception", new Exception("Random exception"));
  }
}
