package io.coala.jetbrains.utils;

import io.coala.jetbrains.utils.deserializers.CodeAnalysisIssueDeserializer;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class CodeAnalysisIssueDeserializerTest {
    private static List<CodeAnalysisIssue> codeAnalysisIssueList;

    @BeforeClass
    public static void getJsonString() throws URISyntaxException, IOException {
        final String fileName = "output.json";
        String jsonString = new String(Files.readAllBytes(Paths.get(CodeAnalysisIssueDeserializerTest.class.getClassLoader().getResource(fileName).toURI())));
        codeAnalysisIssueList = CodeAnalysisIssueDeserializer.getAllCodeAnalysisIssues(jsonString);
    }

    @Test
    public void testListProperties() {
        assertThat(codeAnalysisIssueList).hasSize(2);
        assertThat(codeAnalysisIssueList.get(0).getAffectedCodeList()).hasSize(1);
    }

    @Test
    public void testObjectProperties() {
        assertThat(codeAnalysisIssueList.get(0).getAffectedCodeList().get(0).getStartSourceRange())
                .isEqualToComparingFieldByField(new SourceRange("some_file.ext", 2, 1));
        assertThat(codeAnalysisIssueList.get(0).getAffectedCodeList().get(0).getEndSourceRange())
                .isEqualToComparingFieldByField(new SourceRange("some_file.ext", 2, 1));
        assertThat(codeAnalysisIssueList.get(0).getSeverity()).isEqualByComparingTo(CodeAnalysisIssue.IssueSeverity.WARNING);
        assertThat(codeAnalysisIssueList.get(0).getOrigin()).isNotEmpty().isEqualTo("PycodestyleBear (W191)");
        assertThat(codeAnalysisIssueList.get(1)).isEqualToIgnoringNullFields(new CodeAnalysisIssue("SpaceConsistencyBear", "Line contains following spacing inconsistencies:\n- Spaces used instead of tabs.", null, 1));
    }
}
