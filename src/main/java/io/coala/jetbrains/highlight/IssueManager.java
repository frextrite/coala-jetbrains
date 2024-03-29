package io.coala.jetbrains.highlight;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
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
import io.coala.jetbrains.utils.SourceRange;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IssueManager implements ProjectComponent {

  private static final Logger LOGGER = Logger.getInstance(IssueManager.class);

  private final Project project;

  public IssueManager(Project project) {
    this.project = project;
  }

  /**
   * This method returns all {@link RangeMarker} created from all the issues.
   *
   * <p/>
   * Extension of {@link #getRangeMarkerFromIssue(CodeAnalysisIssue)}
   * to return a collection of range markers per document
   * <p/>
   * the size of the map may be greater than or equal to 0
   * <p/>
   * the size of the collection may be greater than or equal to 0
   *
   * @param issueList the list of issues for processing
   * @return a collection of range markers associated with document
   * @throws FileNotFoundException
   */
  public Map<Document, Collection<RangeMarker>> getRangeMarkers(List<CodeAnalysisIssue> issueList)
      throws FileNotFoundException {
    if (issueList == null) {
      return null;
    }

    final Map<Document, Collection<RangeMarker>> rangeMarkers = new HashMap<>();

    for (CodeAnalysisIssue issue : issueList) {
      final Map<Document, Collection<RangeMarker>> rangeMarkerFromIssue =
          getRangeMarkerFromIssue(issue);

      for (Map.Entry<Document, Collection<RangeMarker>> element : rangeMarkerFromIssue.entrySet()) {
        final Document document = element.getKey();
        final Collection<RangeMarker> rangeMarkerCollection = element.getValue();

        if (!rangeMarkers.containsKey(document)) {
          rangeMarkers.put(document, new ArrayList<>());
        }

        for (RangeMarker rangeMarker : rangeMarkerCollection) {
          if (!rangeMarkers.get(document).contains(rangeMarker)) {
            rangeMarkers.get(document).add(rangeMarker);
          }
        }
      }

    }

    return rangeMarkers;
  }

  /**
   * This method returns {@link RangeMarker} created from a specific issue.
   *
   * <p/>
   * These range markers are then used for highlighting inside documents.
   * <p/>
   * Per document collection of range markers is created by calculating the
   * start and end offsets associated with the respective document
   * of the lint problem
   * <p/>
   * the size of the returned map is always 1
   * the size of the corresponding collection of range markers is always 1
   * <p/>
   * each {@link AffectedCode} has 2 instances of {@link SourceRange}
   * namely start and end which have the contextual information about the line and column affected
   *
   * @param issue the instance which needs to be processed
   * @return the map with document as the key and collection of range markers as value
   *     size of the map and the collection is always 1 since every issue object contains information
   *     about exactly one lint problem associated with a document
   * @throws FileNotFoundException
   */
  public Map<Document, Collection<RangeMarker>> getRangeMarkerFromIssue(CodeAnalysisIssue issue)
      throws FileNotFoundException {
    Map<Document, Collection<RangeMarker>> rangeMarkers = new HashMap<>();

    for (AffectedCode affectedCode : issue.getAffectedCodeList()) {
      final String filePath = affectedCode.getFileName();
      final Document document = getDocument(filePath);

      if (!rangeMarkers.containsKey(document)) {
        rangeMarkers.put(document, new ArrayList<>());
      }

      rangeMarkers.get(document).add(createRangeMarker(affectedCode, document));
    }

    return rangeMarkers;
  }

  /**
   * This method returns {@link Document} instance corresponding to the provided source path.
   *
   * @param filePath the path of the file as a string
   * @return the document instance corresponding to the file
   * @throws FileNotFoundException
   */
  public Document getDocument(@Nullable String filePath) throws FileNotFoundException {
    if (filePath == null) {
      return null;
    }

    final File file = new File(filePath);
    final VirtualFile vfsFile = LocalFileSystem.getInstance().findFileByIoFile(file);

    if (vfsFile == null) {
      throw new FileNotFoundException(filePath);
    }

    return FileDocumentManager.getInstance().getDocument(vfsFile);
  }

  /**
   * This methods returns {@link PsiFile} instance corresponding to the provided {@link Document}.
   *
   * @param document the instance whose corresponding psi file needs to be generated
   * @return the instance of psi file corresponding to the document
   */
  public PsiFile getPsiFile(@Nullable Document document) {
    if (document == null) {
      return null;
    }

    return PsiDocumentManager.getInstance(project).getPsiFile(document);
  }

  private RangeMarker createRangeMarker(@NotNull AffectedCode affectedCode,
      @NotNull Document document) {
    final TextRange textRange = getIssueTextRange(affectedCode, document);

    return document.createRangeMarker(textRange);
  }

  private TextRange getIssueTextRange(@NotNull AffectedCode affectedCode,
      @NotNull Document document) {
    int startLine = affectedCode.getStartLine() - 1;
    int endLine = affectedCode.getEndLine() - 1;
    int startColumn = affectedCode.getStartColumn();
    int endColumn = affectedCode.getEndColumn();

    if (startColumn == -1 && endColumn == -1) {
      startColumn = 0;
      endLine += 1;
    } else {
      startColumn -= 1;
      endColumn -= 1;
    }

    final int startOffset = getOffsetInDocument(document, startLine, startColumn);
    final int endOffset = getOffsetInDocument(document, endLine, endColumn);

    return new TextRange(startOffset, endOffset);
  }

  private int getOffsetInDocument(@NotNull Document document, int line, int column) {
    return document.getLineStartOffset(line) + column;
  }

}
