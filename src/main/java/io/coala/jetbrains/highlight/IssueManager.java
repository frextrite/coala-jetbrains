package io.coala.jetbrains.highlight;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import io.coala.jetbrains.utils.AffectedCode;
import io.coala.jetbrains.utils.CodeAnalysisIssue;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IssueManager implements ProjectComponent {

  private final Project project;
  private final PsiDocumentManager documentManager;

  public IssueManager(Project project, PsiDocumentManager documentManager) {
    this.project = project;
    this.documentManager = documentManager;
  }

  public Map<Document, Collection<RangeMarker>> getAllRangeMarkers(
      List<CodeAnalysisIssue> issueList) throws FileNotFoundException {
    final Map<Document, Collection<RangeMarker>> rangeMarkers = new HashMap<>();

    for (CodeAnalysisIssue issue : issueList) {
      final Map<Document, Collection<RangeMarker>> rangeMarkerFromIssue = getRangeMarkerFromIssue(issue);

      for (Map.Entry<Document, Collection<RangeMarker>> element : rangeMarkerFromIssue.entrySet()) {
        final Document document = element.getKey();
        final Collection<RangeMarker> rangeMarkerCollection = element.getValue();

        for (RangeMarker rangeMarker : rangeMarkerCollection) {
          if (!rangeMarkers.get(document).contains(rangeMarker)) {
            rangeMarkers.get(document).add(rangeMarker);
          }
        }
      }

    }

    return rangeMarkers;
  }

  public Map<Document, Collection<RangeMarker>> getRangeMarkerFromIssue(CodeAnalysisIssue issue)
      throws FileNotFoundException {
    Map<Document, Collection<RangeMarker>> rangeMarkers = new HashMap<>();

    for (AffectedCode affectedCode : issue.getAffectedCodeList()) {
      final String filePath = affectedCode.getFileName();
      final Document document = getDocument(filePath);

      rangeMarkers.get(document).add(createRangeMarker(affectedCode, document));
    }

    return rangeMarkers;
  }

  private Document getDocument(String filePath) throws FileNotFoundException {
    final File file = new File(filePath);
    final VirtualFile vfsFile = LocalFileSystem.getInstance().findFileByIoFile(file);

    if (vfsFile == null) {
      throw new FileNotFoundException(filePath);
    }

    return FileDocumentManager.getInstance().getDocument(vfsFile);
  }

  private PsiFile getPsiFile(Document document) {
    return documentManager.getPsiFile(document);
  }

  private RangeMarker createRangeMarker(AffectedCode affectedCode, Document document) {
    final TextRange textRange = getIssueTextRange(affectedCode, document);

    return document.createRangeMarker(textRange);
  }

  private TextRange getIssueTextRange(AffectedCode affectedCode, Document document) {
    final int startLine = affectedCode.getStartLine() - 1;
    final int endLine = affectedCode.getEndLine() - 1;
    final int startColumn = affectedCode.getStartColumn();
    final int endColumn = affectedCode.getEndColumn();

    final int startOffset = getOffsetInDocument(document, startLine, startColumn);
    final int endOffset = getOffsetInDocument(document, endLine, endColumn);

    return new TextRange(startOffset, endOffset);
  }

  private int getOffsetInDocument(Document document, int line, int column) {
    return document.getLineStartOffset(line) + column;
  }

}
