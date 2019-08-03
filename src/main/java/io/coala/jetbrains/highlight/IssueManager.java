package io.coala.jetbrains.highlight;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import io.coala.jetbrains.utils.AffectedCode;
import io.coala.jetbrains.utils.CodeAnalysisIssue;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class IssueManager implements ProjectComponent {

  private final Project project;
  private final PsiDocumentManager documentManager;

  public IssueManager(Project project, PsiDocumentManager documentManager) {
    this.project = project;
    this.documentManager = documentManager;
  }

  public List<RangeMarker> getAffectedCodeSegmentList(CodeAnalysisIssue issue)
      throws FileNotFoundException {
    List<RangeMarker> rangeMarkerList = new ArrayList<>();

    for (AffectedCode affectedCode : issue.getAffectedCodeList()) {
      final String filePath = affectedCode.getFileName();
      final PsiFile psiFile = getPsiFile(filePath);
      final Document document = getDocument(psiFile);

      rangeMarkerList.add(createRangeMarker(affectedCode, document));
    }

    return rangeMarkerList;
  }

  private Document getDocument(PsiFile psiFile) {
    return documentManager.getDocument(psiFile);
  }

  private PsiFile getPsiFile(String filePath) throws FileNotFoundException {
    final VirtualFile vfsFile = VirtualFileManager.getInstance().findFileByUrl(filePath);

    if (vfsFile == null) {
      throw new FileNotFoundException(filePath);
    }

    return PsiManager.getInstance(project).findFile(vfsFile);
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
