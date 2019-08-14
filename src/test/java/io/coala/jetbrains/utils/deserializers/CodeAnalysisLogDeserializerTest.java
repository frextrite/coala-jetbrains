package io.coala.jetbrains.utils.deserializers;

import static org.assertj.core.api.Assertions.assertThat;

import io.coala.jetbrains.utils.CodeAnalysisLog;
import io.coala.jetbrains.utils.CodeInspectionSeverity;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.TimeZone;
import org.junit.BeforeClass;
import org.junit.Test;

public class CodeAnalysisLogDeserializerTest {

  private static List<CodeAnalysisLog> codeAnalysisLogList;

  @BeforeClass
  public static void getJsonString() throws URISyntaxException, IOException {
    TimeZone.setDefault(TimeZone.getTimeZone("IST"));
    final String fileName = "output.json";
    String jsonString = new String(Files.readAllBytes(Paths.get(
        CodeAnalysisIssueDeserializerTest.class.getClassLoader().getResource(fileName).toURI())));
    codeAnalysisLogList = CodeAnalysisLogDeserializer.getAllCodeAnalysisLogs(jsonString);
  }

  @Test
  public void testListProperties() {
    assertThat(codeAnalysisLogList).hasSize(3);
  }

  @Test
  public void testCodeAnalysisLogProperties() {
    assertThat(codeAnalysisLogList.get(0).getPrintableTimeStamp()).isNotNull()
        .isEqualTo("[12:01:23]");
    assertThat(codeAnalysisLogList.get(1)).isEqualToComparingFieldByField(
        new CodeAnalysisLog("A WARNING Message", "2019-07-13T06:31:55.020146", "WARNING"));
    assertThat(codeAnalysisLogList.get(2).getSeverity())
        .isEqualByComparingTo(CodeInspectionSeverity.DEBUG);
    assertThat(codeAnalysisLogList.get(2).getMessage()).isEqualTo("A DEBUG Message");
  }
}
